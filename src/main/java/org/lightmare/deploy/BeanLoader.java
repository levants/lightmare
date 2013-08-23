package org.lightmare.deploy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.log4j.Logger;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.ConnectionData;
import org.lightmare.cache.ConnectionSemaphore;
import org.lightmare.cache.DeployData;
import org.lightmare.cache.InjectionData;
import org.lightmare.cache.InterceptorData;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.config.Configuration;
import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.rest.providers.RestProvider;
import org.lightmare.utils.NamingUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.beans.BeanUtils;
import org.lightmare.utils.fs.FileUtils;
import org.lightmare.utils.fs.WatchUtils;
import org.lightmare.utils.reflect.MetaUtils;
import org.lightmare.utils.rest.RestCheck;

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

    private static final String LOADER_THREAD_NAME = "Ejb-Loader-Thread-%s";

    private static final Logger LOG = Logger.getLogger(BeanLoader.class);

    protected static final class LoaderPoolManager {

	/**
	 * Implementation of {@link ThreadFactory} interface for application
	 * loading
	 * 
	 * @author levan
	 * 
	 */
	private static final class LoaderThreadFactory implements ThreadFactory {

	    @Override
	    public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setName(String.format(LOADER_THREAD_NAME, thread.getId()));
		thread.setPriority(Thread.MAX_PRIORITY);

		ClassLoader parent = getCurrent();
		thread.setContextClassLoader(parent);

		return thread;
	    }
	}

	// Thread pool for deploying and removal of beans and temporal resources
	private static ExecutorService LOADER_POOL = Executors
		.newFixedThreadPool(LOADER_POOL_SIZE, new LoaderThreadFactory());

	public ExecutorService getLoaderPool() {

	    if (LOADER_POOL == null || LOADER_POOL.isShutdown()
		    || LOADER_POOL.isTerminated()) {

		LOADER_POOL = Executors.newFixedThreadPool(LOADER_POOL_SIZE,
			new LoaderThreadFactory());
	    }

	    return LOADER_POOL;
	}
    }

    /**
     * PrivilegedAction implementation to set
     * {@link Executors#privilegedCallableUsingCurrentClassLoader()} passed
     * {@link Callable} class
     * 
     * @author levan
     * 
     * @param <T>
     */
    private static class ContextLoaderAction<T> implements
	    PrivilegedAction<Callable<T>> {

	private final Callable<T> current;

	public ContextLoaderAction(Callable<T> current) {
	    this.current = current;
	}

	@Override
	public Callable<T> run() {
	    Callable<T> privileged = Executors.privilegedCallable(current);

	    return privileged;
	}
    }

    /**
     * {@link Runnable} implementation for initializing and deploying
     * {@link javax.sql.DataSource}
     * 
     * @author levan
     * 
     */
    private static class ConnectionDeployer implements Callable<Boolean> {

	private Properties properties;

	private CountDownLatch blocker;

	private boolean countedDown;

	public ConnectionDeployer(DataSourceParameters parameters) {

	    this.properties = parameters.properties;
	    this.blocker = parameters.blocker;
	}

	private void releaseBlocker() {

	    if (ObjectUtils.notTrue(countedDown)) {
		blocker.countDown();
		countedDown = Boolean.TRUE;
	    }
	}

	@Override
	public Boolean call() throws Exception {

	    boolean result;
	    ClassLoader loader = getCurrent();
	    try {
		DataSourceInitializer.registerDataSource(properties);
		result = Boolean.TRUE;
	    } catch (IOException ex) {
		result = Boolean.FALSE;
		LOG.error("Could not initialize datasource", ex);
	    } finally {
		releaseBlocker();
		LibraryLoader.loadCurrentLibraries(loader);
	    }

	    return result;
	}
    }

    /**
     * {@link Runnable} implementation for temporal resources removal
     * 
     * @author levan
     * 
     */
    private static class ResourceCleaner implements Callable<Boolean> {

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
	public Boolean call() throws Exception {

	    boolean result;
	    ClassLoader loader = getCurrent();
	    try {
		clearTmpData();
		result = Boolean.TRUE;
	    } catch (InterruptedException ex) {
		result = Boolean.FALSE;
		LOG.error("Coluld not clean temporary resources", ex);
	    } finally {
		LibraryLoader.loadCurrentLibraries(loader);
	    }

	    return result;
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

	private CountDownLatch blocker;

	private boolean countedDown;

	private List<Field> unitFields;

	private DeployData deployData;

	private boolean chekcWatch;

	private Configuration configuration;

	public BeanDeployer(BeanParameters parameters) {
	    this.creator = parameters.creator;
	    this.beanName = parameters.beanName;
	    this.className = parameters.className;
	    this.loader = parameters.loader;
	    this.tmpFiles = parameters.tmpFiles;
	    this.metaData = parameters.metaData;
	    this.blocker = parameters.blocker;
	    this.deployData = parameters.deployData;
	    this.configuration = parameters.configuration;
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
		if (ObjectUtils.notTrue(semaphore.isCheck())) {
		    try {
			creator.configureConnection(unitName, beanName, loader,
				configuration);
		    } finally {
			semaphore.notifyAll();
		    }
		}
	    }
	}

	/**
	 * Increases {@link CountDownLatch} conn if it is first time in current
	 * thread
	 */
	private void releaseBlocker() {
	    if (ObjectUtils.notTrue(countedDown)) {
		blocker.countDown();
		countedDown = Boolean.TRUE;
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
		releaseBlocker();
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
	    boolean checkForEmf = ConnectionContainer.checkForEmf(unitName);
	    if (ObjectUtils.available(jndiName)) {
		jndiName = NamingUtils.createJpaJndiName(jndiName);
		checkForEmf = checkForEmf
			&& ConnectionContainer.checkForEmf(jndiName);
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
		releaseBlocker();
		semaphore = ConnectionContainer.getSemaphore(unitName);
		connection.setConnection(semaphore);
	    } else {
		// Sets connection semaphore for this connection
		semaphore = ConnectionContainer.cacheSemaphore(unitName,
			jndiName);
		connection.setConnection(semaphore);
		releaseBlocker();
		if (ObjectUtils.notNull(semaphore)) {
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

	    Class<?> interfaceClass = ejb.beanInterface();
	    if (interfaceClass == null || interfaceClass.equals(Object.class)) {
		interfaceClass = field.getType();
	    }
	    String name = ejb.beanName();
	    if (name == null || name.isEmpty()) {
		name = BeanUtils.nameFromInterface(interfaceClass);
	    }
	    String description = ejb.description();
	    String mappedName = ejb.mappedName();
	    Class<?>[] interfaceClasses = { interfaceClass };
	    InjectionData injectionData = new InjectionData();
	    injectionData.setField(field);
	    injectionData.setInterfaceClasses(interfaceClasses);
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
		releaseBlocker();
	    }
	    for (Field field : fields) {
		context = field.getAnnotation(PersistenceContext.class);
		resource = field.getAnnotation(Resource.class);
		unit = field.getAnnotation(PersistenceUnit.class);
		ejbAnnot = field.getAnnotation(EJB.class);
		if (ObjectUtils.notNull(context)) {
		    identifyConnections(context, field);
		} else if (ObjectUtils.notNull(resource)) {
		    metaData.setTransactionField(field);
		} else if (ObjectUtils.notNull(unit)) {
		    addUnitField(field);
		} else if (ObjectUtils.notNull(ejbAnnot)) {
		    // caches EJB annotated fields
		    cacheInjectFields(field);
		}
	    }

	    if (ObjectUtils.available(unitFields)) {
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
	    if (Configuration.isServer()) {
		retrieveConnections();
	    } else {
		releaseBlocker();
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
	    boolean transactional = Boolean.FALSE;
	    TransactionAttributeType transactionAttrType;
	    TransactionManagementType transactionManType;
	    if (transactionAttribute == null) {

		transactional = Boolean.TRUE;
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
	 * Caches {@link Interceptors} annotation defined data
	 * 
	 * @param beanClass
	 * @param interceptorClasses
	 * @throws IOException
	 */
	private void cacheInterceptors(Class<?> beanClass,
		Class<?>[] interceptorClasses, Method beanMethod)
		throws IOException {

	    int length = interceptorClasses.length;
	    Class<?> interceptorClass;
	    List<Method> interceptorMethods;
	    Method interceptorMethod;
	    for (int i = 0; i < length; i++) {
		interceptorClass = interceptorClasses[i];
		interceptorMethods = MetaUtils.getAnnotatedMethods(beanClass,
			AroundInvoke.class);
		interceptorMethod = ObjectUtils.getFirst(interceptorMethods);
		InterceptorData data = new InterceptorData();
		data.setBeanClass(beanClass);
		data.setBeanMethod(beanMethod);
		data.setInterceptorClass(interceptorClass);
		data.setInterceptorMethod(interceptorMethod);

		metaData.addInterceptor(data);
	    }
	}

	private void cacheInterceptors(Interceptors interceptors,
		Class<?> beanClass, Method... beanMethods) throws IOException {

	    Class<?>[] interceptorClasses = interceptors.value();
	    if (ObjectUtils.available(interceptorClasses)) {
		Method beanMethod = ObjectUtils.getFirst(beanMethods);
		cacheInterceptors(beanClass, interceptorClasses, beanMethod);
	    }
	}

	/**
	 * Identifies and caches {@link Interceptors} annotation data
	 * 
	 * @throws IOException
	 */
	private void identifyInterceptors(Class<?> beanClass)
		throws IOException {

	    Interceptors interceptors = beanClass
		    .getAnnotation(Interceptors.class);
	    if (ObjectUtils.notNull(interceptors)) {
		cacheInterceptors(interceptors, beanClass);
	    }

	    List<Method> beanMethods = MetaUtils.getAnnotatedMethods(beanClass,
		    Interceptors.class);
	    if (ObjectUtils.available(beanMethods)) {
		for (Method beanMethod : beanMethods) {
		    interceptors = beanMethod.getAnnotation(Interceptors.class);
		    cacheInterceptors(interceptors, beanClass, beanMethod);
		}
	    }
	}

	/**
	 * Identifies and caches bean interfaces
	 * 
	 * @param beanClass
	 */
	private void indentifyInterfaces(Class<?> beanClass) {

	    Class<?>[] remoteInterface = null;
	    Class<?>[] localInterface = null;
	    Class<?>[] interfaces;
	    List<Class<?>> interfacesList;
	    Remote remote = beanClass.getAnnotation(Remote.class);
	    Local local = beanClass.getAnnotation(Local.class);
	    interfaces = beanClass.getInterfaces();
	    if (ObjectUtils.notNull(remote)) {
		remoteInterface = remote.value();
	    }
	    interfacesList = new ArrayList<Class<?>>();
	    for (Class<?> interfaceClass : interfaces) {
		if (interfaceClass.isAnnotationPresent(Remote.class))
		    interfacesList.add(interfaceClass);
	    }

	    if (ObjectUtils.available(interfacesList)) {
		remoteInterface = interfacesList
			.toArray(new Class<?>[interfacesList.size()]);
	    }

	    if (ObjectUtils.notNull(local)) {
		localInterface = local.value();
	    }
	    interfacesList = new ArrayList<Class<?>>();
	    for (Class<?> interfaceClass : interfaces) {
		if (interfaceClass.isAnnotationPresent(Local.class))
		    interfacesList.add(interfaceClass);
	    }

	    if (ObjectUtils.available(interfacesList)) {
		localInterface = interfacesList
			.toArray(new Class<?>[interfacesList.size()]);
	    }

	    if (ObjectUtils.notAvailable(localInterface)
		    && ObjectUtils.notAvailable(remoteInterface)) {

		localInterface = interfaces;
	    }

	    metaData.setLocalInterfaces(localInterface);
	    metaData.setRemoteInterfaces(remoteInterface);
	}

	/**
	 * Loads and caches bean {@link Class} by name
	 * 
	 * @return
	 * @throws IOException
	 */
	private String createBeanClass() throws IOException {
	    try {
		Class<?> beanClass = MetaUtils.classForName(className,
			Boolean.FALSE, loader);
		checkOnTransactional(beanClass);
		String beanEjbName = BeanUtils.beanName(beanClass);
		checkAndSetBean(beanEjbName);
		if (RestCheck.check(beanClass)) {
		    RestProvider.add(beanClass);
		}
		createMeta(beanClass);
		indentifyInterfaces(beanClass);
		identifyInterceptors(beanClass);
		metaData.setInProgress(Boolean.FALSE);

		return beanEjbName;

	    } catch (IOException ex) {
		releaseBlocker();
		throw ex;
	    }
	}

	private String deployFile() {

	    String deployed = beanName;
	    ClassLoader currentLoader = getCurrent();
	    try {
		LibraryLoader.loadCurrentLibraries(loader);
		deployed = createBeanClass();
		chekcWatch = WatchUtils.checkForWatch(deployData);
		if (chekcWatch) {
		    URL url = deployData.getUrl();
		    url = WatchUtils.clearURL(url);
		    MetaContainer.addBeanName(url, deployed);
		}
		LOG.info(String.format("bean %s deployed", beanName));
	    } catch (IOException ex) {
		LOG.error(String.format("Could not deploy bean %s cause %s",
			beanName, ex.getMessage()), ex);
	    } finally {
		LibraryLoader.loadCurrentLibraries(currentLoader);
	    }

	    return deployed;
	}

	private String deployExtracted() {

	    String deployed;
	    synchronized (tmpFiles) {
		try {
		    deployed = deployFile();
		} finally {
		    tmpFiles.notifyAll();
		}
	    }

	    return deployed;
	}

	private String deploy() {

	    synchronized (metaData) {
		String deployed;
		try {
		    if (ObjectUtils.notNull(tmpFiles)) {
			deployed = deployExtracted();
		    } else {
			deployed = deployFile();
		    }

		} catch (Exception ex) {
		    LOG.error(ex.getMessage(), ex);
		    deployed = null;
		} finally {
		    releaseBlocker();
		    metaData.notifyAll();
		}

		return deployed;
	    }
	}

	@Override
	public String call() throws Exception {

	    String deployed = deploy();

	    return deployed;
	}
    }

    /**
     * Contains parameters for bean deploy classes
     * 
     * @author levan
     * 
     */
    public static class BeanParameters {

	public MetaCreator creator;

	public String className;

	public String beanName;

	public ClassLoader loader;

	public List<File> tmpFiles;

	public CountDownLatch blocker;

	public MetaData metaData;

	public DeployData deployData;

	public boolean server;

	public Configuration configuration;
    }

    /**
     * Contains parameters for data source deploy classes
     * 
     * @author levan
     * 
     */
    public static class DataSourceParameters {

	public Properties properties;

	public Properties poolProperties;

	public String poolPath;

	public CountDownLatch blocker;
    }

    private static ClassLoader getCurrent() {

	ClassLoader current;
	MetaCreator creator = MetaContainer.getCreator();
	ClassLoader creatorLoader;
	if (ObjectUtils.notNull(creator)) {
	    creatorLoader = creator.getCurrent();
	    if (ObjectUtils.notNull(creatorLoader)) {
		current = creatorLoader;
	    } else {
		current = LibraryLoader.getContextClassLoader();
	    }
	} else {
	    current = LibraryLoader.getContextClassLoader();
	}

	return current;
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
    public static Future<String> loadBean(BeanParameters parameters)
	    throws IOException {

	parameters.metaData = new MetaData();
	String beanName = BeanUtils.parseName(parameters.className);
	parameters.beanName = beanName;
	BeanDeployer beanDeployer = new BeanDeployer(parameters);
	Future<String> future = LOADER_POOL.submit(beanDeployer);

	return future;
    }

    /**
     * Initialized {@link javax.sql.DataSource}s in parallel mode
     * 
     * @param initializer
     * @param properties
     * @param sdLatch
     */
    public static void initializeDatasource(DataSourceParameters parameters)
	    throws IOException {

	final ConnectionDeployer connectionDeployer = new ConnectionDeployer(
		parameters);
	Callable<Boolean> privileged = AccessController
		.doPrivileged(new ContextLoaderAction<Boolean>(
			connectionDeployer));

	LOADER_POOL.submit(privileged);
    }

    /**
     * Creates and starts temporal resources removal process
     * 
     * @param tmpFiles
     */
    public static <V> void removeResources(List<File> tmpFiles) {

	ResourceCleaner cleaner = new ResourceCleaner(tmpFiles);
	Callable<Boolean> privileged = AccessController
		.doPrivileged(new ContextLoaderAction<Boolean>(cleaner));

	LOADER_POOL.submit(privileged);
    }
}
