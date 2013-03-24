package org.lightmare.ejb.startup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.log4j.Logger;
import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.ejb.meta.ConnectionData;
import org.lightmare.ejb.meta.ConnectionSemaphore;
import org.lightmare.ejb.meta.InjectionData;
import org.lightmare.ejb.meta.MetaContainer;
import org.lightmare.ejb.meta.MetaData;
import org.lightmare.jndi.NamingUtils;
import org.lightmare.jpa.JPAManager;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.beans.BeanUtils;
import org.lightmare.utils.fs.FileUtils;

/**
 * Class for running in distinct thread to initialize
 * {@link javax.sql.DataSource}s load libraries and {@link javax.ejb.Stateless}
 * session beans and cache them and clean resources after deployments
 * 
 * @author levan
 * 
 */
public class BeanLoader {

    private static final int LOADER_POOL_SIZE = 5;

    private static final Logger LOG = Logger.getLogger(BeanLoader.class);

    // Thread pool for deploying and removal of beans and temporal resources
    private static ExecutorService loaderPool = Executors.newFixedThreadPool(
	    LOADER_POOL_SIZE, new ThreadFactory() {

		@Override
		public Thread newThread(Runnable runnable) {
		    Thread thread = new Thread(runnable);
		    thread.setName(String.format("Ejb-Loader-Thread-%s",
			    thread.getId()));
		    return thread;
		}
	    });

    /**
     * {@link Runnable} implementation for initializing and deploying
     * {@link javax.sql.DataSource}
     * 
     * @author levan
     * 
     */
    private static class ConnectionDeployer implements Runnable {

	private DataSourceInitializer initializer;

	private Properties properties;

	private CountDownLatch dsLatch;

	private boolean countedDown;

	public ConnectionDeployer(DataSourceInitializer initializer,
		Properties properties, CountDownLatch dsLatch) {

	    this.initializer = initializer;
	    this.properties = properties;
	    this.dsLatch = dsLatch;
	}

	private void notifyDs() {

	    if (!countedDown) {
		dsLatch.countDown();
		countedDown = true;
	    }
	}

	@Override
	public void run() {

	    try {
		initializer.registerDataSource(properties);
		notifyDs();
	    } catch (IOException ex) {
		notifyDs();
		LOG.error("Could not initialize datasource", ex);
	    }

	}

    }

    /**
     * {@link Runnable} implementation for temporal resources removal
     * 
     * @author levan
     * 
     */
    private static class ResourceCleaner implements Runnable {

	List<File> tmpFiles;

	public ResourceCleaner(List<File> tmpFiles) {
	    this.tmpFiles = tmpFiles;
	}

	/**
	 * Removes temporal resources after deploy {@link Thread} notifies
	 * 
	 * @throws InterruptedException
	 */
	private void clearTmpData() throws InterruptedException {

	    synchronized (tmpFiles) {
		tmpFiles.wait();
	    }

	    for (File tmpFile : tmpFiles) {
		FileUtils.deleteFile(tmpFile);
		LOG.info(String.format("Cleaning temporal resource %s done",
			tmpFile.getName()));
	    }
	}

	@Override
	public void run() {

	    try {
		clearTmpData();
	    } catch (InterruptedException ex) {
		LOG.error("Coluld not clear temporary resources", ex);
	    }

	}

    }

    /**
     * {@link Callable} implementation for deploying {@link javax.ejb.Stateless}
     * session beans and cache {@link MetaData} keyed by bean name
     * 
     * @author levan
     * 
     */
    private static class BeanDeployer implements Callable<String> {

	private MetaCreator creator;

	private String beanName;

	private String className;

	private ClassLoader loader;

	private List<File> tmpFiles;

	private MetaData metaData;

	private CountDownLatch conn;

	private boolean isCounted;

	private List<Field> unitFields;

	public BeanDeployer(MetaCreator creator, String beanName,
		String className, ClassLoader loader, MetaData metaData,
		List<File> tmpFiles, CountDownLatch conn) {
	    this.creator = creator;
	    this.beanName = beanName;
	    this.className = className;
	    this.loader = loader;
	    this.tmpFiles = tmpFiles;
	    this.metaData = metaData;
	    this.conn = conn;
	}

	/**
	 * Locks {@link ConnectionSemaphore} if needed for connection processing
	 * 
	 * @param semaphore
	 * @param unitName
	 * @param jndiName
	 * @throws IOException
	 */
	private void lockSemaphore(ConnectionSemaphore semaphore,
		String unitName, String jndiName) throws IOException {
	    synchronized (semaphore) {
		if (!semaphore.isCheck()) {
		    creator.configureConnection(unitName, beanName);
		    semaphore.notifyAll();
		}
	    }
	}

	/**
	 * Increases {@link CountDownLatch} conn if it is first time in current
	 * thread
	 */
	private void notifyConn() {
	    if (!isCounted) {
		conn.countDown();
		isCounted = true;
	    }
	}

	/**
	 * Checks if bean {@link MetaData} with same name already cached if it
	 * is increases {@link CountDownLatch} for connection and throws
	 * {@link BeanInUseException} else caches meta data with associated name
	 * 
	 * @param beanEjbName
	 * @throws BeanInUseException
	 */
	private void checkAndSetBean(String beanEjbName)
		throws BeanInUseException {
	    try {
		MetaContainer.checkAndAddMetaData(beanEjbName, metaData);
	    } catch (BeanInUseException ex) {
		notifyConn();
		throw ex;
	    }
	}

	private void addUnitField(Field unitField) {

	    if (unitFields == null) {
		unitFields = new ArrayList<Field>();
	    }

	    unitFields.add(unitField);
	}

	/**
	 * Checks weather connection with passed unit or jndi name already
	 * exists
	 * 
	 * @param unitName
	 * @param jndiName
	 * @return <code>boolean</code>
	 */
	private boolean checkOnEmf(String unitName, String jndiName) {
	    boolean checkForEmf;
	    if (jndiName == null || jndiName.isEmpty()) {
		checkForEmf = JPAManager.checkForEmf(unitName);
	    } else {
		jndiName = NamingUtils.createJpaJndiName(jndiName);
		checkForEmf = JPAManager.checkForEmf(unitName)
			&& JPAManager.checkForEmf(jndiName);
	    }

	    return checkForEmf;
	}

	/**
	 * Creates {@link ConnectionSemaphore} if such does not exists
	 * 
	 * @param context
	 * @param field
	 * @return <code>boolean</code>
	 * @throws IOException
	 */
	private void identifyConnections(PersistenceContext context,
		Field connectionField) throws IOException {

	    ConnectionData connection = new ConnectionData();

	    connection.setConnectionField(connectionField);
	    String unitName = context.unitName();
	    String jndiName = context.name();
	    connection.setUnitName(unitName);
	    connection.setJndiName(jndiName);
	    boolean checkForEmf = checkOnEmf(unitName, jndiName);

	    ConnectionSemaphore semaphore;

	    if (checkForEmf) {
		notifyConn();
		semaphore = JPAManager.getSemaphore(unitName);
		connection.setConnection(semaphore);
	    } else {
		// Sets connection semaphore for this connection
		semaphore = JPAManager.setSemaphore(unitName, jndiName);
		connection.setConnection(semaphore);
		notifyConn();
		if (semaphore != null) {
		    lockSemaphore(semaphore, unitName, jndiName);
		}

	    }

	    metaData.addConnection(connection);
	}

	/**
	 * Caches {@link EJB} annotated fields
	 * 
	 * @param beanClass
	 */
	private void cacheInjectFields(Field field) {

	    EJB ejb = field.getAnnotation(EJB.class);

	    Class<?> beanClass = field.getClass();
	    Class<?> interfaceClass = ejb.beanInterface();
	    String name = ejb.beanName();
	    String description = ejb.description();
	    String mappedName = ejb.mappedName();

	    InjectionData injectionData = new InjectionData();
	    injectionData.setBeanClass(beanClass);
	    injectionData.setInterfaceClass(interfaceClass);
	    injectionData.setName(name);
	    injectionData.setDescription(description);
	    injectionData.setMappedName(mappedName);

	    metaData.addInject(injectionData);
	}

	/**
	 * Finds and caches {@link PersistenceContext}, {@link PersistenceUnit}
	 * and {@link Resource} annotated {@link Field}s in bean class and
	 * configures connections and creates {@link ConnectionSemaphore}s if it
	 * does not exists for {@link PersistenceContext#unitName()} object
	 * 
	 * @throws IOException
	 */
	private void retrieveConnections() throws IOException {

	    Class<?> beanClass = metaData.getBeanClass();
	    Field[] fields = beanClass.getDeclaredFields();

	    PersistenceUnit unit;
	    PersistenceContext context;
	    Resource resource;
	    EJB ejbAnnot;
	    if (fields == null || fields.length == 0) {
		notifyConn();
	    }
	    for (Field field : fields) {
		context = field.getAnnotation(PersistenceContext.class);
		resource = field.getAnnotation(Resource.class);
		unit = field.getAnnotation(PersistenceUnit.class);
		ejbAnnot = field.getAnnotation(EJB.class);
		if (context != null) {
		    identifyConnections(context, field);
		} else if (resource != null) {
		    metaData.setTransactionField(field);
		} else if (unit != null) {
		    addUnitField(field);
		} else if (ejbAnnot != null) {
		    // caches EJB annotated fields
		    cacheInjectFields(field);
		}

	    }

	    if (unitFields != null && !unitFields.isEmpty()) {
		metaData.addUnitFields(unitFields);
	    }
	}

	/**
	 * Creates {@link MetaData} for bean class
	 * 
	 * @param beanClass
	 * @throws ClassNotFoundException
	 */
	private void createMeta(Class<?> beanClass) throws IOException {

	    metaData.setBeanClass(beanClass);
	    if (MetaCreator.CONFIG.isServer()) {
		retrieveConnections();
	    } else {
		notifyConn();
	    }

	    metaData.setLoader(loader);
	}

	/**
	 * Checks if bean class is annotated as {@link TransactionAttribute} and
	 * {@link TransactionManagement} and caches
	 * {@link TransactionAttribute#value()} and
	 * {@link TransactionManagement#value()} in {@link MetaData} object
	 * 
	 * @param beanClass
	 */
	private void checkOnTransactional(Class<?> beanClass) {

	    TransactionAttribute transactionAttribute = beanClass
		    .getAnnotation(TransactionAttribute.class);
	    TransactionManagement transactionManagement = beanClass
		    .getAnnotation(TransactionManagement.class);
	    boolean transactional = false;
	    TransactionAttributeType transactionAttrType;
	    TransactionManagementType transactionManType;
	    if (transactionAttribute == null) {

		transactional = true;
		transactionAttrType = TransactionAttributeType.REQUIRED;
		transactionManType = TransactionManagementType.CONTAINER;

	    } else if (transactionManagement == null) {

		transactionAttrType = transactionAttribute.value();
		transactionManType = TransactionManagementType.CONTAINER;
	    } else {
		transactionAttrType = transactionAttribute.value();
		transactionManType = transactionManagement.value();
	    }

	    metaData.setTransactional(transactional);
	    metaData.setTransactionAttrType(transactionAttrType);
	    metaData.setTransactionManType(transactionManType);
	}

	/**
	 * Loads and caches bean {@link Class} by name
	 * 
	 * @return
	 * @throws IOException
	 */
	private String createBeanClass() throws IOException {
	    try {
		Class<?> beanClass;
		if (loader == null) {
		    beanClass = Class.forName(className);
		} else {
		    beanClass = Class.forName(className, true, loader);
		}

		checkOnTransactional(beanClass);

		Stateless annotation = beanClass.getAnnotation(Stateless.class);
		String beanEjbName = annotation.name();
		if (beanEjbName == null || beanEjbName.isEmpty()) {
		    beanEjbName = beanName;
		}
		checkAndSetBean(beanEjbName);
		createMeta(beanClass);
		metaData.setInProgress(false);

		return beanEjbName;

	    } catch (ClassNotFoundException ex) {
		notifyConn();
		throw new IOException(ex);
	    }
	}

	private String deploy() {

	    String deployed = beanName;

	    try {
		LibraryLoader.loadCurrentLibraries(loader);
		deployed = createBeanClass();
		LOG.info(String.format("bean %s deployed", beanName));
	    } catch (IOException ex) {
		LOG.error(String.format("Could not deploy bean %s cause %s",
			beanName, ex.getMessage()), ex);
	    }

	    return deployed;
	}

	@Override
	public String call() throws Exception {

	    synchronized (metaData) {
		try {
		    String deployed;
		    if (tmpFiles != null) {
			synchronized (tmpFiles) {
			    deployed = deploy();
			    tmpFiles.notifyAll();
			}
		    } else {
			deployed = deploy();
		    }
		    notifyConn();
		    metaData.notifyAll();

		    return deployed;

		} catch (Exception ex) {
		    LOG.error(ex.getMessage(), ex);
		    metaData.notifyAll();
		    notifyConn();
		    return null;
		}
	    }
	}

    }

    /**
     * Creates and starts bean deployment process
     * 
     * @param creator
     * @param className
     * @param loader
     * @param tmpFiles
     * @param conn
     * @return {@link Future}
     * @throws IOException
     */
    public static Future<String> loadBean(MetaCreator creator,
	    String className, ClassLoader loader, List<File> tmpFiles,
	    CountDownLatch conn) throws IOException {
	MetaData metaData = new MetaData();
	String beanName = BeanUtils.parseName(className);
	BeanDeployer beanDeployer = new BeanDeployer(creator, beanName,
		className, loader, metaData, tmpFiles, conn);
	Future<String> future = loaderPool.submit(beanDeployer);

	return future;
    }

    /**
     * Initialized {@link javax.sql.DataSource}s in parallel mode
     * 
     * @param initializer
     * @param properties
     * @param sdLatch
     */
    public static void initializeDatasource(DataSourceInitializer initializer,
	    Properties properties, CountDownLatch dsLatch) throws IOException {

	ConnectionDeployer connectionDeployer = new ConnectionDeployer(
		initializer, properties, dsLatch);
	loaderPool.submit(connectionDeployer);
    }

    /**
     * Creates and starts temporal resources removal process
     * 
     * @param tmpFiles
     */
    public static <V> void removeResources(List<File> tmpFiles) {
	ResourceCleaner cleaner = new ResourceCleaner(tmpFiles);
	loaderPool.submit(cleaner);
    }
}
