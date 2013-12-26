package org.lightmare.deploy;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.lightmare.cache.DeployData;
import org.lightmare.cache.MetaData;
import org.lightmare.config.Configuration;
import org.lightmare.deploy.deployers.BeanDeployer;
import org.lightmare.deploy.deployers.ConnectionDeployer;
import org.lightmare.deploy.deployers.ResourceCleaner;
import org.lightmare.utils.beans.BeanUtils;

/**
 * Class for running in distinct thread to initialize
 * {@link javax.sql.DataSource}s load libraries and {@link javax.ejb.Stateless}
 * session beans and cache them and clean resources after deployments
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 */
public class BeanLoader {

    private static final Logger LOG = Logger.getLogger(BeanLoader.class);

    /**
     * PrivilegedAction implementation to set
     * {@link Executors#privilegedCallableUsingCurrentClassLoader()} passed
     * {@link Callable} class
     * 
     * @author Levan Tsinadze
     * 
     * @param <T>
     * @since 0.0.45-SNAPSHOT
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
     * Contains parameters for bean deploy classes
     * 
     * @author Levan Tsinadze
     * @since 0.0.45-SNAPSHOT
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
     * @author Levan Tsinadze
     * @since 0.0.45-SNAPSHOT
     */
    public static class DataSourceParameters {

	public Properties properties;

	public Properties poolProperties;

	public String poolPath;

	public CountDownLatch blocker;
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
	Future<String> future = LoaderPoolManager.submit(beanDeployer);

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

	LoaderPoolManager.submit(privileged);
    }

    /**
     * Creates and starts temporal resources removal process
     * 
     * @param tmpFiles
     */
    public static void removeResources(List<File> tmpFiles) throws IOException {

	ResourceCleaner cleaner = new ResourceCleaner(tmpFiles);
	Callable<Boolean> privileged = AccessController
		.doPrivileged(new ContextLoaderAction<Boolean>(cleaner));

	try {
	    LoaderPoolManager.submit(privileged);
	} catch (IOException ex) {
	    LOG.error(ex.getMessage(), ex);
	}
    }
}
