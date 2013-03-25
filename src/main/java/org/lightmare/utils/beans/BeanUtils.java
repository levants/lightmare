package org.lightmare.utils.beans;

/**
 * Utility class for ejb beans
 * 
 * @author levan
 * 
 */
public class BeanUtils {

    private static final String REMOTE_IDENT = "Remote";

    private static final String LOCAL_IDENT = "Remote";

    /**
     * Retrieves bean name from class name
     * 
     * @param name
     * @return String
     */
    public static String parseName(String name) {

	String simpleName = name;

	int index = name.lastIndexOf('.');
	if (index > -1) {
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
	    beanName = interfaceName.substring(0, start);
	} else if (interfaceName.endsWith(LOCAL_IDENT)) {
	    start = interfaceName.lastIndexOf(LOCAL_IDENT);
	    beanName = interfaceName.substring(0, start);
	} else {
	    beanName = interfaceName;
	}

	return beanName;
    }
}
