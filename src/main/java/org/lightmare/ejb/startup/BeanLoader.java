package org.lightmare.ejb.startup;

import static org.lightmare.ejb.meta.MetaContainer.addMetaData;
import static org.lightmare.ejb.meta.MetaContainer.checkMetaData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.ejb.meta.MetaData;
import org.lightmare.ejb.meta.TmpData;
import org.lightmare.jpa.JPAManager;
import org.lightmare.libraries.LibraryLoader;
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

	private static class ResourceCleaner<V> implements Runnable {

		List<TmpData<V>> tmpDatas;

		public ResourceCleaner(List<TmpData<V>> tmpDatas) {
			this.tmpDatas = tmpDatas;
		}

		private void clearTmpData(TmpData<V> tmpData) {

			List<File> tmpFiles = tmpData.getTmpFiles();

			for (File tmpFile : tmpFiles) {
				FileUtils.deleteFile(tmpFile);
				LOG.info(String.format("Cleaning temporal resource %s done",
						tmpFile.getName()));
			}
		}

		@Override
		public void run() {

			for (TmpData<V> tmpData : tmpDatas) {
				Future<V> future = tmpData.getFuture();
				try {
					future.get();
				} catch (InterruptedException ex) {
					LOG.error(String.format(
							"Could not remove temporal resources cause %s",
							ex.getMessage()), ex);
				} catch (ExecutionException ex) {
					LOG.error(String.format(
							"Could not remove temporal resources cause %s",
							ex.getMessage()), ex);
				}
			}

			for (TmpData<V> tmpData : tmpDatas) {
				clearTmpData(tmpData);
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

	private ClassLoader loader;

	public BeanLoader(MetaCreator creator, String beanName, ClassLoader loader) {
		this.creator = creator;
		this.beanName = beanName;
		this.loader = loader;
	}

	private void retriveConnections(MetaData metaData) throws IOException {

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
	private MetaData createMeta(Class<?> beanClass) throws IOException {

		MetaData metaData = new MetaData();
		metaData.setBeanClass(beanClass);
		if (MetaCreator.configuration.isServer()) {
			retriveConnections(metaData);
		}

		metaData.setLoader(loader);

		return metaData;
	}

	private String createBeanClass() throws IOException {
		if (checkMetaData(beanName)) {
			throw new BeanInUseException(String.format(
					"bean % is alredy in use", beanName));
		} else {
			try {
				Class<?> beanClass;
				if (loader == null) {
					beanClass = Class.forName(beanName);
				} else {
					beanClass = Class.forName(beanName, true, loader);
				}
				MetaData metaData = createMeta(beanClass);
				Stateless annotation = beanClass.getAnnotation(Stateless.class);
				String beanEjbName = annotation.name();
				if (beanEjbName == null || beanEjbName.isEmpty()) {
					beanEjbName = beanClass.getSimpleName();
				}
				addMetaData(beanEjbName, metaData);

				return beanEjbName;

			} catch (ClassNotFoundException ex) {
				throw new IOException(ex);
			}
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

		String deployed = realCall();

		return deployed;
	}

	public static Future<String> loadBean(MetaCreator creator, String beanName,
			ClassLoader loader) throws IOException {
		Future<String> future = loaderPool.submit(new BeanLoader(creator,
				beanName, loader));

		return future;
	}

	public static <V> void removeResources(List<TmpData<V>> tmpDatas) {
		ResourceCleaner<V> cleaner = new ResourceCleaner<V>(tmpDatas);
		loaderPool.submit(cleaner);
	}
}
