package org.lightmare.rest.providers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.server.model.Parameter;
import org.lightmare.utils.reflect.MetaUtils;

public class RestConvertor {

    private static final String VALUE_OF = "valueOf";

    private static Object convertPrimitives(Class<?> rawType, String param) {

	Object value;
	if (rawType.equals(byte.class)) {
	    value = Byte.valueOf(param);
	} else if (rawType.equals(short.class)) {
	    value = Short.valueOf(param);
	} else if (rawType.equals(char.class)) {
	    value = Character.valueOf(param.charAt(0));
	} else if (rawType.equals(boolean.class)) {
	    value = Boolean.valueOf(param);
	} else if (rawType.equals(int.class)) {
	    value = Integer.valueOf(param);
	} else if (rawType.equals(long.class)) {
	    value = Long.valueOf(param);
	} else if (rawType.equals(float.class)) {
	    value = Float.valueOf(param);
	} else if (rawType.equals(double.class)) {
	    value = Double.valueOf(param);
	} else {
	    value = param;
	}

	return value;
    }

    public static Object convertContructs(Class<?> rawType, String param)
	    throws IOException {

	Method method = MetaUtils.getDeclaredMethod(rawType, VALUE_OF,
		String.class);
	Object value = MetaUtils.invokeStatic(method);

	return value;
    }

    protected static String getParam(List<String> queryParams) {
	return queryParams.get(0);
    }

    protected static Collection<Object> convertCollection(
	    List<String> queryParams, Collection<Object> collection)
	    throws IOException {

	Object value;
	for (String param : queryParams) {
	    value = convertContructs(null, param);
	    collection.add(collection);
	}

	return collection;
    }

    protected static void convert(Class<?> rawType, String param)
	    throws IOException {

	Object value;
	Collection<?> collection;
	if (rawType.equals(List.class)) {
	    collection = Collections.emptyList();
	} else if (rawType.equals(Set.class)) {
	    collection = Collections.emptySet();
	} else if (rawType.equals(SortedSet.class)) {
	    collection = Collections.emptySet();
	} else {
	    value = convertContructs(rawType, param);
	}
    }

    public static void convert(List<Parameter> parameters, UriInfo uriInfo) {

	MultivaluedMap<String, String> queries = uriInfo.getQueryParameters();
	String name;
	Class<?> rawType;
	List<String> queryParams;
	String param;
	List<Object> params = new ArrayList<Object>();
	Object value;
	for (Parameter parameter : parameters) {
	    name = parameter.getSourceName();
	    queryParams = queries.get(name);
	    param = getParam(queryParams);
	    rawType = parameter.getRawType();
	    if (rawType.isPrimitive()) {
		value = convertPrimitives(rawType, param);
	    }
	}
    }
}
