package org.lightmare.ejb.startup;

import static org.lightmare.ejb.meta.MetaContainer.addMetaData;
import static org.lightmare.ejb.meta.MetaContainer.checkMetaData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.ejb.meta.MetaContainer;
import org.lightmare.ejb.meta.MetaData;
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

	private static class ResourceCleaner implements Runnable {

		List<File> tmpFiles;

		public ResourceCleaner(List<File> tmpFiles) {
			this.tmpFiles = tmpFiles;
		}

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

	public BeanLoader(MetaCreator creator, String beanName, String className,
			ClassLoader loader, MetaData metaData, List<File> tmpFiles) {
		this.creator = creator;
		this.beanName = beanName;
		this.className = className;
		this.loader = loader;
		this.tmpFiles = tmpFiles;
		this.metaData = metaData;
	}

	private void retriveConnections() throws IOException {

		Class<?> beanClass = metaData.getBeanClass();
		Field[] fields = beanClass.getDeclaredFields();

		PersistenceContext context;
		String unitName;
		boolean checkForEmf;
		for (Field field : fields) {
			context = field.getAnnotation(PersistenceContext.class);
			if (context != null) {
				metaData.setConnectorField(field);
				unitName = context.unitName();
				String jndiName = context.name();
				checkForEmf = JPAManager.checkForEmf(unitName);
				if (checkForEmf) {
					continue;
				} else {
					creator.configureConnection(unitName, beanName, jndiName);
					metaData.setUnitName(unitName);
				}
				break;
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
		if (MetaCreator.configuration.isServer()) {
			retriveConnections();
		}

		metaData.setLoader(loader);
	}

	private void checkBean(String name) throws BeanInUseException {
		if (checkMetaData(beanName)) {
			throw new BeanInUseException(String.format(
					"bean % is alredy in use", beanName));
		}

	}

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
				addMetaData(beanEjbName, metaData);
				MetaContainer.removeMeta(beanName);
			}
			createMeta(beanClass);
			metaData.setInProgress(false);

			return beanEjbName;

		} catch (ClassNotFoundException ex) {
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
			String deployed;
			if (tmpFiles != null) {
				synchronized (tmpFiles) {
					deployed = realCall();
					tmpFiles.notifyAll();
				}
			} else {
				deployed = realCall();
			}

			metaData.notifyAll();

			return deployed;
		}
	}

	public static Future<String> loadBean(MetaCreator creator,
			String className, ClassLoader loader, List<File> tmpFiles)
			throws IOException {
		MetaData metaData = new MetaData();
		String beanName = BeanUtils.parseName(className);
		MetaData tmpMeta = MetaContainer.addMetaData(beanName, metaData);
		if (tmpMeta != null && !tmpMeta.isInProgress()) {
			throw new BeanInUseException(String.format(
					"bean % is alredy in use", beanName));
		}
		Future<String> future = loaderPool.submit(new BeanLoader(creator,
				beanName, className, loader, metaData, tmpFiles));

		return future;
	}

	public static <V> void removeResources(List<File> tmpFiles) {
		ResourceCleaner cleaner = new ResourceCleaner(tmpFiles);
		loaderPool.submit(cleaner);
	}
}
