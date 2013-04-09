package org.lightmare.rest;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;

/**
 * To describe beans and it's methods with parameters as json string
 * 
 * @author Levan
 * 
 */
public class RestPublisher {

    private static final String START_OBJECT = "{";

    private static final String END_OBJECT = "}";

    private static final String START_PARAMS = "(";

    private static final String END_PARAMS = ")";

    private static final String PARAMS_SEPR = ",";

    private static final String NEW_LINE = "\n";

    private static final String OBJECT_SEPAR = ":";

    private static final String BEAN_SEPAR = "bean";

    private static final String STR_MATCH = "\"";

    // private static final String RETURN_MATCH = "returns";

    private static final String SPACE = " ";

    private static final String TAB = "\t\t";

    private static final String VOID_RETURN = "\"";

    public String publish(Class<?> beanClass) {

	Method[] methods = beanClass.getDeclaredMethods();

	StringBuilder builder = new StringBuilder();
	Class<?>[] parameters;
	Class<?> returnType;
	builder.append(START_OBJECT);
	builder.append(STR_MATCH);
	builder.append(BEAN_SEPAR);
	builder.append(STR_MATCH);
	builder.append(OBJECT_SEPAR);
	builder.append(STR_MATCH);
	builder.append(beanClass.getSimpleName());
	int length = 0;
	int i;
	for (Method method : methods) {

	    if (Modifier.isPublic(method.getModifiers())) {
		builder.append(NEW_LINE);
		builder.append(TAB);
		parameters = method.getParameterTypes();
		returnType = method.getReturnType();

		builder.append(SPACE);
		if (returnType == null) {
		    builder.append(VOID_RETURN);
		} else {
		    builder.append(returnType.getSimpleName());
		}

		builder.append(SPACE);
		builder.append(method.getName());
		builder.append(START_PARAMS);

		length = parameters.length;
		i = 0;
		for (Class<?> parameter : parameters) {
		    i++;
		    builder.append(parameter.getSimpleName());
		    if (i < length) {
			builder.append(PARAMS_SEPR);
			builder.append(SPACE);
		    }
		}
		builder.append(END_PARAMS);
	    }
	}
	builder.append(STR_MATCH);
	builder.append(END_OBJECT);
	builder.append(NEW_LINE);

	return builder.toString();
    }

    public String publishAll() {

	Iterator<MetaData> iterator = MetaContainer.getBeanClasses();
	MetaData metaData;
	Class<?> beanClass;
	String beanDescripton;
	StringBuilder builder = new StringBuilder();
	while (iterator.hasNext()) {
	    metaData = iterator.next();
	    beanClass = metaData.getBeanClass();
	    beanDescripton = publish(beanClass);
	    builder.append(beanDescripton);
	}

	return builder.toString();
    }
}
