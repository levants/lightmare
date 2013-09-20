package org.lightmare.utils.beans;

import javax.ejb.Stateless;

import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.StringUtils;

/**
 * Utility class for EJB beans
 * 
 * @author levan
 * 
 */
public class BeanUtils {

    // Suffixes of local and remote interface names
    private static final String REMOTE_IDENT = "Remote";

    private static final String LOCAL_IDENT = "Local";

    /**
     * Retrieves bean name from class name
     * 
     * @param name
     * @return String
     */
    public static String parseName(String name) {

	String simpleName = name;

	int index = name.lastIndexOf(StringUtils.DOT);
	if (index > StringUtils.NOT_EXISTING_INDEX) {
	    index++;
	    simpleName = name.substring(index);
	}

	return simpleName;
    }

    /**
     * Removes <b>Remote</b> or <b>Local</b> part from bean interface name
     * 
     * @param interfaceClass
     * @return
     */
    public static String nameFromInterface(Class<?> interfaceClass) {

	String interfaceName = interfaceClass.getSimpleName();
	String beanName;

	int start;
	if (interfaceName.endsWith(REMOTE_IDENT)) {
	    start = interfaceName.lastIndexOf(REMOTE_IDENT);
	    beanName = interfaceName.substring(CollectionUtils.FIRST_INDEX,
		    start);
	} else if (interfaceName.endsWith(LOCAL_IDENT)) {
	    start = interfaceName.lastIndexOf(LOCAL_IDENT);
	    beanName = interfaceName.substring(CollectionUtils.FIRST_INDEX,
		    start);
	} else {
	    beanName = interfaceName;
	}

	return beanName;
    }

    public static String beanName(Class<?> beanClass) {

	Stateless annotation = beanClass.getAnnotation(Stateless.class);
	String beanEjbName = annotation.name();
	if (StringUtils.invalid(beanEjbName)) {
	    beanEjbName = beanClass.getSimpleName();
	}

	return beanEjbName;
    }
}
