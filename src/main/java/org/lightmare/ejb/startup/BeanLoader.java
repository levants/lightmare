package org.lightmare.ejb.startup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.log4j.Logger;
import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.ejb.meta.ConnectionSemaphore;
import org.lightmare.ejb.meta.MetaContainer;
import org.lightmare.ejb.meta.MetaData;
import org.lightmare.jndi.NamingUtils;
import org.lightmare.jpa.JPAManager;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.beans.BeanUtils;
import org.lightmare.utils.fs.FileUtils;

/**
 * Class for running in distinct thread to load libraries and stateless beans
 * and cache
 * 
 * @author levan
 * 
 */
public class BeanLoader implements Callable<String> {

	private static final int LOADER_POOL_SIZE = 5;

	/**
	 * {@link Callable} implementation for temporal resources removal
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

	private MetaCreator creator;

	private String beanName;

	private String className;

	private ClassLoader loader;

	private List<File> tmpFiles;

	private MetaData metaData;

	private CountDownLatch conn;

	private boolean isCounted;

	public BeanLoader(MetaCreator creator, String beanName, String className,
			ClassLoader loader, MetaData metaData, List<File> tmpFiles,
			CountDownLatch conn) {
		this.creator = creator;
		this.beanName = beanName;
		this.className = className;
		this.loader = loader;
		this.tmpFiles = tmpFiles;
		this.metaData = metaData;
		this.conn = conn;
	}

	/**
	 * Locks {@link ConnectionSemaphore} if needed for connection proccessing
	 * 
	 * @param semaphore
	 * @param unitName
	 * @param jndiName
	 * @throws IOException
	 */
	private void lockSemaphore(ConnectionSemaphore semaphore, String unitName,
			String jndiName) throws IOException {
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

	private boolean checkOnBreak(PersistenceContext context, Resource resource,
			PersistenceUnit unit) {
		return context != null && resource != null && unit != null;
	}

	/**
	 * Finds and caches {@link PersistenceContext}, {@link PersistenceUnit} and
	 * {@link Resource} annotated {@link Field}s in bean class and configures
	 * connections and creates {@link ConnectionSemaphore}s if it does not
	 * exists for {@link PersistenceContext#unitName()} object
	 * 
	 * @throws IOException
	 */
	private void retrieveConnections() throws IOException {

		Class<?> beanClass = metaData.getBeanClass();
		Field[] fields = beanClass.getDeclaredFields();

		PersistenceUnit unit;
		PersistenceContext context;
		Resource resource;
		String unitName;
		String jndiName;
		boolean checkForEmf;
		if (fields == null || fields.length == 0) {
			notifyConn();
		}
		for (Field field : fields) {
			context = field.getAnnotation(PersistenceContext.class);
			resource = field.getAnnotation(Resource.class);
			unit = field.getAnnotation(PersistenceUnit.class);
			if (context != null) {
				metaData.setConnectorField(field);
				unitName = context.unitName();
				jndiName = context.name();
				metaData.setUnitName(unitName);
				metaData.setJndiName(jndiName);
				if (jndiName == null || jndiName.isEmpty()) {
					checkForEmf = JPAManager.checkForEmf(unitName);
				} else {
					jndiName = NamingUtils.createJndiName(jndiName);
					checkForEmf = JPAManager.checkForEmf(unitName)
							&& JPAManager.checkForEmf(jndiName);
				}
				if (checkForEmf) {
					notifyConn();
					if (checkOnBreak(context, resource, unit)) {
						break;
					}
				} else {
					ConnectionSemaphore semaphore = JPAManager.setSemaphore(
							unitName, jndiName);
					notifyConn();
					if (semaphore != null) {
						lockSemaphore(semaphore, unitName, jndiName);
					}

					if (checkOnBreak(context, resource, unit)) {
						break;
					}
				}
			} else if (resource != null) {

				metaData.setTransactionField(field);
				if (checkOnBreak(context, resource, unit)) {
					break;
				}
			} else if (unit != null) {
				metaData.setUnitField(field);
				if (checkOnBreak(context, resource, unit)) {
					break;
				}
			}
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
	 * Cecks if bean is already deployed
	 * 
	 * @param name
	 * @throws BeanInUseException
	 */
	private void checkBean(String name) throws BeanInUseException {
		if (MetaContainer.checkMetaData(beanName)) {
			notifyConn();
			throw new BeanInUseException(String.format(
					"bean % is alredy in use", beanName));
		}

	}

	/**
	 * Loads and caches bean {@link Class} by name
	 * 
	 * @return
	 * @throws IOException
	 */
	private String createBeanClass() throws IOException {
		checkBean(beanName);
		try {
			Class<?> beanClass;
			if (loader == null) {
				beanClass = Class.forName(className);
			} else {
				beanClass = Class.forName(className, true, loader);
			}
			Stateless annotation = beanClass.getAnnotation(Stateless.class);
			String beanEjbName = annotation.name();
			if (beanEjbName == null || beanEjbName.isEmpty()) {
				beanEjbName = beanName;
			} else {
				checkBean(beanEjbName);
				try {
					MetaContainer.checkAndAddMetaData(beanEjbName, metaData);
				} catch (BeanInUseException ex) {
					notifyConn();
					throw ex;
				}
				MetaContainer.removeMeta(beanName);
			}
			createMeta(beanClass);
			metaData.setInProgress(false);

			return beanEjbName;

		} catch (ClassNotFoundException ex) {
			notifyConn();
			throw new IOException(ex);
		}
	}

	private String realCall() {

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
						deployed = realCall();
						tmpFiles.notifyAll();
					}
				} else {
					deployed = realCall();
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
		try {
			MetaContainer.checkAndAddMetaData(beanName, metaData);
		} catch (BeanInUseException ex) {
			conn.countDown();
			throw ex;
		}
		Future<String> future = loaderPool.submit(new BeanLoader(creator,
				beanName, className, loader, metaData, tmpFiles, conn));

		return future;
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
