package org.lightmare.rest.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.rest.RestConfig;
import org.lightmare.rest.providers.RestInflector;
import org.lightmare.rest.providers.RestReloader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.beans.BeanUtils;
import org.lightmare.utils.serialization.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for REST resources
 * 
 * @author levan
 * 
 */
public class RestUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static RestConfig config;

    private static RestConfig oldConfig;

    private static void getConfig() {

	oldConfig = RestConfig.get();
	config = new RestConfig();
    }

    private static RestConfig get() {

	synchronized (RestUtils.class) {
	    getConfig();
	}

	return config;
    }

    public static <T> T convert(String json, Class<T> valueClass)
	    throws IOException {

	T value = JsonSerializer.read(json, valueClass);

	return value;
    }

    public static String json(Object data) throws IOException {

	return JsonSerializer.write(data);
    }

    /**
     * Checks if class is acceptable to build {@link Resource} instance
     * 
     * @param resourceClass
     * @return <code>boolean</code>
     */
    private static boolean isAcceptable(Class<?> resourceClass) {

	boolean valid = Resource.isAcceptable(resourceClass)
		&& resourceClass.isAnnotationPresent(Path.class);

	return valid;
    }

    /**
     * Builds new {@link Resource} from passed one with new
     * {@link org.glassfish.jersey.process.Inflector} implementation
     * {@link RestInflector} and with all child resources
     * 
     * @param resource
     * @return {@link Resource}
     * @throws IOException
     */
    public static Resource defineHandler(Resource resource) throws IOException {

	Resource.Builder builder = Resource.builder(resource.getPath());
	builder.name(resource.getName());
	List<ResourceMethod> methods = resource.getAllMethods();
	ResourceMethod.Builder methodBuilder;
	Collection<Class<?>> handlers = resource.getHandlerClasses();
	Class<?> beanClass;
	String beanEjbName;
	Iterator<Class<?>> iterator = handlers.iterator();
	beanClass = iterator.next();
	beanEjbName = BeanUtils.beanName(beanClass);
	List<MediaType> consumedTypes;
	List<MediaType> producedTypes;
	Invocable invocable;
	MetaData metaData = MetaContainer.getSyncMetaData(beanEjbName);
	Method realMethod;
	MediaType type;
	List<Parameter> parameters;
	for (ResourceMethod method : methods) {
	    consumedTypes = method.getConsumedTypes();
	    producedTypes = method.getProducedTypes();
	    invocable = method.getInvocable();
	    realMethod = invocable.getHandlingMethod();
	    parameters = invocable.getParameters();
	    if (ObjectUtils.available(consumedTypes)) {
		type = ObjectUtils.getFirst(consumedTypes);
	    } else {
		type = null;
	    }
	    RestInflector inflector = new RestInflector(realMethod, metaData,
		    type, parameters);
	    methodBuilder = builder.addMethod(method.getHttpMethod());
	    methodBuilder.consumes(consumedTypes);
	    methodBuilder.produces(producedTypes);
	    methodBuilder.nameBindings(method.getNameBindings());
	    methodBuilder.handledBy(inflector);
	    methodBuilder.build();
	}
	List<Resource> children = resource.getChildResources();
	if (ObjectUtils.available(children)) {
	    Resource child;
	    for (Resource preChild : children) {
		child = defineHandler(preChild);
		builder.addChildResource(child);
	    }
	}
	Resource intercepted = builder.build();

	return intercepted;
    }

    public static void add(Class<?> beanClass) throws IOException {

	boolean valid = isAcceptable(beanClass);
	if (valid) {

	    RestReloader reloader = RestReloader.get();
	    if (ObjectUtils.notNull(reloader)) {
		RestConfig conf = get();
		conf.registerClass(beanClass, oldConfig);
	    }
	}
    }

    public static void remove(Class<?> beanClass) {

	RestReloader reloader = RestReloader.get();
	if (ObjectUtils.notNull(reloader)) {
	    RestConfig conf = get();
	    conf.unregister(beanClass, oldConfig);
	}
    }

    /**
     * Gets common class loader (enriched for each {@link ClassLoader} from
     * {@link MetaData}) to add to REST server
     * 
     * @return {@link ClassLoader}
     */
    public static ClassLoader getCommonLoader() {

	Iterator<MetaData> iterator = MetaContainer.getBeanClasses();
	MetaData metaData;
	ClassLoader newLoader;
	ClassLoader oldLoader = null;
	ClassLoader commonLoader = null;
	while (iterator.hasNext()) {
	    metaData = iterator.next();
	    newLoader = metaData.getLoader();
	    if (ObjectUtils.notNull(oldLoader)
		    && ObjectUtils.notNull(newLoader)) {
		commonLoader = LibraryLoader.createCommon(newLoader, oldLoader);
	    }
	    oldLoader = newLoader;
	}

	return commonLoader;
    }

    public static void reload() {

	RestReloader reloader = RestReloader.get();
	RestConfig conf = RestConfig.get();
	if (ObjectUtils.notNull(conf) && ObjectUtils.notNull(reloader)) {
	    ClassLoader commonLoader = getCommonLoader();
	    if (ObjectUtils.notNull(commonLoader)) {
		conf.setClassLoader(commonLoader);
	    }
	    conf.registerPreResources();
	    reloader.reload(conf);
	}
    }
}
