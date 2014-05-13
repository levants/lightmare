/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2014, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.jpa.hibernate.boot.registry.classloading.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.util.ClassLoaderHelper;
import org.jboss.logging.Logger;
import org.lightmare.libraries.loaders.EjbClassLoader;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Implementation of class loader services
 * 
 * @author Levan Tsinadze
 * @since 0.1.0
 * @see ClassLoaderServiceImpl
 */
public class ClassLoaderServiceExt extends ClassLoaderServiceImpl {

    private static final long serialVersionUID = 1L;

    private static final String PERSISTENCE_XML_PATH = "META-INF/persistence.xml";

    private static final Logger LOG = CoreLogging
	    .logger(ClassLoaderServiceExt.class);

    @SuppressWarnings("rawtypes")
    private final Map<Class, ServiceLoader> serviceLoaders = new HashMap<Class, ServiceLoader>();
    private AggregatedClassLoader aggregatedClassLoader;

    /**
     * Constructs a {@link ClassLoaderServiceExt} with standard set-up
     */
    public ClassLoaderServiceExt() {
	this(ClassLoaderServiceExt.class.getClassLoader());
    }

    /**
     * Constructs a ClassLoaderServiceExt with the given ClassLoader
     * 
     * @param classLoader
     *            The ClassLoader to use
     */
    public ClassLoaderServiceExt(ClassLoader classLoader) {
	this(Collections.singletonList(classLoader));
    }

    /**
     * Constructs a {@link ClassLoaderServiceExt} with the given ClassLoader
     * instances
     * 
     * @param providedClassLoaders
     *            The ClassLoader instances to use
     */
    public ClassLoaderServiceExt(Collection<ClassLoader> providedClassLoaders) {
	final LinkedHashSet<ClassLoader> orderedClassLoaderSet = new LinkedHashSet<ClassLoader>();

	// first, add all provided class loaders, if any
	if (providedClassLoaders != null) {
	    for (ClassLoader classLoader : providedClassLoaders) {
		if (classLoader != null) {
		    orderedClassLoaderSet.add(classLoader);
		}
	    }
	}

	// normalize adding known class-loaders...
	// then the Hibernate class loader
	orderedClassLoaderSet.add(ClassLoaderServiceExt.class.getClassLoader());

	// then the TCCL, if one...
	final ClassLoader tccl = locateTCCL();
	if (tccl != null) {
	    orderedClassLoaderSet.add(tccl);
	}
	// finally the system classloader
	final ClassLoader sysClassLoader = locateSystemClassLoader();
	if (sysClassLoader != null) {
	    orderedClassLoaderSet.add(sysClassLoader);
	}

	// now build the aggregated class loader...
	this.aggregatedClassLoader = new AggregatedClassLoader(
		orderedClassLoaderSet);
    }

    /**
     * Adds {@link ClassLoader} at runtime
     * 
     * @param loaders
     */
    public void addLoaders(ClassLoader... loaders) {

	Collection<ClassLoader> providedLoaders;

	if (CollectionUtils.valid(loaders)) {
	    providedLoaders = new HashSet<ClassLoader>(Arrays.asList(loaders));
	} else {
	    providedLoaders = Collections.emptySet();
	}

	this.aggregatedClassLoader.addLoaders(providedLoaders);
    }

    /**
     * No longer used/supported!
     * 
     * @param configValues
     *            The config values
     * 
     * @return The built service
     * 
     * @deprecated No longer used/supported!
     */
    @Deprecated
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ClassLoaderServiceExt fromConfigSettings(Map configValues) {
	final List<ClassLoader> providedClassLoaders = new ArrayList<ClassLoader>();

	final Collection<ClassLoader> classLoaders = (Collection<ClassLoader>) configValues
		.get(AvailableSettings.CLASSLOADERS);
	if (classLoaders != null) {
	    for (ClassLoader classLoader : classLoaders) {
		providedClassLoaders.add(classLoader);
	    }
	}

	addIfSet(providedClassLoaders, AvailableSettings.APP_CLASSLOADER,
		configValues);
	addIfSet(providedClassLoaders, AvailableSettings.RESOURCES_CLASSLOADER,
		configValues);
	addIfSet(providedClassLoaders, AvailableSettings.HIBERNATE_CLASSLOADER,
		configValues);
	addIfSet(providedClassLoaders,
		AvailableSettings.ENVIRONMENT_CLASSLOADER, configValues);

	if (providedClassLoaders.isEmpty()) {
	    LOG.debugf("Incoming config yielded no classloaders; adding standard SE ones");
	    final ClassLoader tccl = locateTCCL();
	    if (tccl != null) {
		providedClassLoaders.add(tccl);
	    }
	    providedClassLoaders.add(ClassLoaderServiceExt.class
		    .getClassLoader());
	}

	return new ClassLoaderServiceExt(providedClassLoaders);
    }

    @SuppressWarnings("rawtypes")
    private static void addIfSet(List<ClassLoader> providedClassLoaders,
	    String name, Map configVales) {
	final ClassLoader providedClassLoader = (ClassLoader) configVales
		.get(name);
	if (providedClassLoader != null) {
	    providedClassLoaders.add(providedClassLoader);
	}
    }

    protected static ClassLoader locateSystemClassLoader() {
	try {
	    return ClassLoader.getSystemClassLoader();
	} catch (Exception e) {
	    return null;
	}
    }

    private static ClassLoader locateTCCL() {
	try {
	    return ClassLoaderHelper.getContextClassLoader();
	} catch (Exception e) {
	    return null;
	}
    }

    private static class AggregatedClassLoader extends ClassLoader {
	private ClassLoader[] individualClassLoaders;

	private AggregatedClassLoader(
		final LinkedHashSet<ClassLoader> orderedClassLoaderSet) {
	    super(null);
	    individualClassLoaders = orderedClassLoaderSet
		    .toArray(new ClassLoader[orderedClassLoaderSet.size()]);
	}

	/**
	 * To add {@link ClassLoader} at runtime
	 * 
	 * @param loaders
	 */
	public void addLoaders(Collection<ClassLoader> loaders) {

	    if (CollectionUtils.valid(loaders)) {
		Set<ClassLoader> existeds = new LinkedHashSet<ClassLoader>(
			Arrays.asList(individualClassLoaders));
		existeds.addAll(loaders);
		individualClassLoaders = existeds
			.toArray(new ClassLoader[existeds.size()]);
	    }
	}

	/**
	 * Gets {@link Collection} of {@link EjbClassLoader} only if persistence
	 * configuration path is needed
	 * 
	 * @param name
	 * @return {@link Collection}
	 */
	private Collection<ClassLoader> getAppropriateLoaders(String name) {

	    Collection<ClassLoader> loaders;

	    boolean valid = name.contains(PERSISTENCE_XML_PATH)
		    && EjbClassLoader.checkPlatform();

	    if (valid) {
		loaders = new HashSet<ClassLoader>();
		for (ClassLoader classLoader : individualClassLoaders) {
		    if (classLoader instanceof EjbClassLoader) {
			loaders.add(classLoader);
		    }
		}

		if (loaders.isEmpty()) {
		    loaders = Arrays.asList(individualClassLoaders);
		}
	    } else {
		loaders = Arrays.asList(individualClassLoaders);
	    }

	    return loaders;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
	    final HashSet<URL> resourceUrls = new HashSet<URL>();

	    Collection<ClassLoader> loaders = getAppropriateLoaders(name);

	    for (ClassLoader classLoader : loaders) {
		final Enumeration<URL> urls;
		if (classLoader instanceof EjbClassLoader) {
		    EjbClassLoader ejbLoader = ObjectUtils.cast(classLoader,
			    EjbClassLoader.class);
		    urls = ejbLoader.getOnlyResources(name);
		} else {
		    urls = classLoader.getResources(name);
		}

		while (urls.hasMoreElements()) {
		    resourceUrls.add(urls.nextElement());
		}
	    }

	    return new Enumeration<URL>() {
		final Iterator<URL> resourceUrlIterator = resourceUrls
			.iterator();

		@Override
		public boolean hasMoreElements() {
		    return resourceUrlIterator.hasNext();
		}

		@Override
		public URL nextElement() {
		    return resourceUrlIterator.next();
		}
	    };
	}

	@Override
	protected URL findResource(String name) {
	    for (ClassLoader classLoader : individualClassLoaders) {
		final URL resource = classLoader.getResource(name);
		if (resource != null) {
		    return resource;
		}
	    }
	    return super.findResource(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
	    for (ClassLoader classLoader : individualClassLoaders) {
		try {
		    return classLoader.loadClass(name);
		} catch (Exception ignore) {
		}
	    }

	    throw new ClassNotFoundException(
		    "Could not load requested class : " + name);
	}

	public void destroy() {
	    individualClassLoaders = null;
	}
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public <T> Class<T> classForName(String className) {
	try {
	    return (Class<T>) Class.forName(className, true,
		    aggregatedClassLoader);
	} catch (Exception e) {
	    throw new ClassLoadingException("Unable to load class ["
		    + className + "]", e);
	}
    }

    @Override
    public URL locateResource(String name) {
	// first we try name as a URL
	try {
	    return new URL(name);
	} catch (Exception ignore) {
	}

	try {
	    return aggregatedClassLoader.getResource(name);
	} catch (Exception ignore) {
	}

	return null;
    }

    @Override
    public InputStream locateResourceStream(String name) {
	// first we try name as a URL
	try {
	    LOG.tracef("trying via [new URL(\"%s\")]", name);
	    return new URL(name).openStream();
	} catch (Exception ignore) {
	}

	try {
	    LOG.tracef("trying via [ClassLoader.getResourceAsStream(\"%s\")]",
		    name);
	    final InputStream stream = aggregatedClassLoader
		    .getResourceAsStream(name);
	    if (stream != null) {
		return stream;
	    }
	} catch (Exception ignore) {
	}

	final String stripped = name.startsWith("/") ? name.substring(1) : null;

	if (stripped != null) {
	    try {
		LOG.tracef("trying via [new URL(\"%s\")]", stripped);
		return new URL(stripped).openStream();
	    } catch (Exception ignore) {
	    }

	    try {
		LOG.tracef(
			"trying via [ClassLoader.getResourceAsStream(\"%s\")]",
			stripped);
		final InputStream stream = aggregatedClassLoader
			.getResourceAsStream(stripped);
		if (stream != null) {
		    return stream;
		}
	    } catch (Exception ignore) {
	    }
	}

	return null;
    }

    @Override
    public List<URL> locateResources(String name) {
	final ArrayList<URL> urls = new ArrayList<URL>();
	try {
	    final Enumeration<URL> urlEnumeration = aggregatedClassLoader
		    .getResources(name);
	    if (urlEnumeration != null && urlEnumeration.hasMoreElements()) {
		while (urlEnumeration.hasMoreElements()) {
		    urls.add(urlEnumeration.nextElement());
		}
	    }
	} catch (Exception ignore) {
	}

	return urls;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> LinkedHashSet<S> loadJavaServices(Class<S> serviceContract) {
	ServiceLoader<S> serviceLoader;
	if (serviceLoaders.containsKey(serviceContract)) {
	    serviceLoader = serviceLoaders.get(serviceContract);
	} else {
	    serviceLoader = ServiceLoader.load(serviceContract,
		    aggregatedClassLoader);
	    serviceLoaders.put(serviceContract, serviceLoader);
	}

	final LinkedHashSet<S> services = new LinkedHashSet<S>();
	for (S service : serviceLoader) {
	    services.add(service);
	}
	return services;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void stop() {
	for (ServiceLoader serviceLoader : serviceLoaders.values()) {
	    serviceLoader.reload(); // clear service loader providers
	}
	serviceLoaders.clear();

	if (aggregatedClassLoader != null) {
	    aggregatedClassLoader.destroy();
	    aggregatedClassLoader = null;
	}
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // completely temporary !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    /**
     * Hack around continued (temporary) need to sometimes set the TCCL for code
     * we call that expects it.
     * 
     * @param <T>
     *            The result type
     */
    public static interface Work<T> {
	/**
	 * The work to be performed with the TCCL set
	 * 
	 * @return The result of the work
	 */
	public T perform();
    }

    /**
     * Perform some discrete work with with the TCCL set to our aggregated
     * ClassLoader
     * 
     * @param work
     *            The discrete work to be done
     * @param <T>
     *            The type of the work result
     * 
     * @return The work result.
     */
    public <T> T withTccl(Work<T> work) {
	final ClassLoader tccl = Thread.currentThread().getContextClassLoader();

	boolean set = false;

	try {
	    Thread.currentThread().setContextClassLoader(aggregatedClassLoader);
	    set = true;
	} catch (Exception ignore) {
	}

	try {
	    return work.perform();
	} finally {
	    if (set) {
		Thread.currentThread().setContextClassLoader(tccl);
	    }
	}

    }
}
