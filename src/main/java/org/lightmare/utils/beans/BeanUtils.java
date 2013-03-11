package org.lightmare.utils.beans;

/**
 * Utility class for ejb beans
 * 
 * @author levan
 * 
 */
public class BeanUtils {

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
}
