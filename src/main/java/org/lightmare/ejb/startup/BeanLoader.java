package org.lightmare.ejb.startup;

import static org.lightmare.ejb.meta.MetaContainer.addMetaData;
import static org.lightmare.ejb.meta.MetaContainer.checkMetaData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
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
public class BeanLoader implements Callable<Boolean> {

	private static final int LOADER_POOL_SIZE = 5;

	private static class ResourceCleaner implements Runnable {

		Map.Entry<Future<Boolean>, List<File>> tmpResource;

		public ResourceCleaner(
				Map.Entry<Future<Boolean>, List<File>> tmpResource) {
			this.tmpResource = tmpResource;
		}

		@Override
		public void run() {

			Future<Boolean> future = tmpResource.getKey();
			List<File> tmpFiles = tmpResource.getValue();

			try {

				future.get();
				for (File tmpFile : tmpFiles) {
					FileUtils.deleteFile(tmpFile);
					LOG.info(String.format(
							"Cleaning temporal resource %s done",
							tmpFile.getName()));
				}

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

	private Boolean createBeanClass() throws IOException {
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
				String beanName = annotation.name();
				if (beanName == null || beanName.isEmpty()) {
					beanName = beanClass.getSimpleName();
				}
				addMetaData(beanName, metaData);

				return true;

			} catch (ClassNotFoundException ex) {
				throw new IOException(ex);
			}
		}
	}

	private boolean realCall() {

		boolean deployed = false;

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
	public Boolean call() throws Exception {

		boolean deployed = realCall();

		return deployed;
	}

	public static Future<Boolean> loadBean(MetaCreator creator,
			String beanName, ClassLoader loader) throws IOException {
		Future<Boolean> future = loaderPool.submit(new BeanLoader(creator,
				beanName, loader));

		return future;
	}

	public static void removeResources(
			Map.Entry<Future<Boolean>, List<File>> tmpResource) {
		ResourceCleaner cleaner = new ResourceCleaner(tmpResource);
		loaderPool.submit(cleaner);
	}
}
