package org.lightmare.linq.orm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.lightmare.linq.query.FullQueryStream;
import org.lightmare.utils.reflect.ClassLoaderUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Utility class to manage entities
 * 
 * @author Levan Tsinadze
 *
 */
public class EntityUtils {

    public static Object wrap(Object entity) {

	Object wrapper;

	ClassLoader loader = ClassLoaderUtils.getContextClassLoader();
	Class<?>[] interfaces = new Class<?>[] { entity.getClass(), FullQueryStream.class };
	InvocationHandler handler = (proxy, method, args) -> {
	    String name = method.getName();
	    System.out.println(name);
	    return ClassUtils.invoke(method, proxy, args);
	};
	wrapper = Proxy.newProxyInstance(loader, interfaces, handler);
	System.out.println(wrapper);

	return wrapper;
    }
}
