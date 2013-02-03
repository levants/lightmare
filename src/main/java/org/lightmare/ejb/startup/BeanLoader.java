package org.lightmare.ejb.startup;

import static org.lightmare.ejb.meta.MetaContainer.addMetaData;
import static org.lightmare.ejb.meta.MetaContainer.checkMetaData;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;

import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.ejb.meta.MetaData;
import org.lightmare.libraries.LibraryLoader;

/**
 * Class for running in distinct thread to load libraries and stateless beans
 * and cache
 * 
 * @author levan
 * 
 */
public class BeanLoader implements Callable<Boolean> {

	private static ExecutorService loaderPool = Executors
			.newSingleThreadExecutor(new ThreadFactory() {

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

	/**
	 * Creates {@link MetaData} for bean class
	 * 
	 * @param beanClass
	 * @throws ClassNotFoundException
	 */
	private MetaData createMeta(Class<?> beanClass) throws IOException {
		Field[] fields = beanClass.getDeclaredFields();
		PersistenceContext context;
		String unitName;
		MetaData metaData = new MetaData();
		metaData.setBeanClass(beanClass);

		for (Field field : fields) {
			context = field.getAnnotation(PersistenceContext.class);
			if (context != null) {
				metaData.setConnectorField(field);
				unitName = context.unitName();
				String jndiName = context.name();
				if (MetaCreator.configuration.isServer()) {
					creator.configureConnection(unitName, beanName, jndiName);
					metaData.setUnitName(unitName);
				}
				break;
			}
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

	@Override
	public Boolean call() throws Exception {

		LibraryLoader.loadCurrentLibraries(loader);

		return createBeanClass();
	}

	public static boolean loadBean(MetaCreator creator, String beanName,
			ClassLoader loader) throws IOException {
		Future<Boolean> future = loaderPool.submit(new BeanLoader(creator,
				beanName, loader));

		try {
			return future.get();
		} catch (InterruptedException ex) {
			throw new IOException(ex);
		} catch (ExecutionException ex) {
			throw new IOException(ex);
		}

	}
}
