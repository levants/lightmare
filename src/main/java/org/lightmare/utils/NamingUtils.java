package org.lightmare.utils;

import org.lightmare.config.Configuration;

/**
 * Utility class for JNDI names
 * 
 * @author levan
 * 
 */
public class NamingUtils {

    public static final String USER_TRANSACTION_NAME = "java:comp/UserTransaction";

    public static final String CONNECTION_NAME_PREF = "java:comp/env/";

    public static final String EJB_NAME_PREF = "ejb:";

    private static final String DS_JNDI_FREFIX = "java:/";

    /**
     * Descriptor class which contains EJB bean class name and its interface
     * class name
     * 
     * @author levan
     * 
     */
    public static class BeanDescriptor {

	private String beanName;

	private String interfaceName;

	public BeanDescriptor(String beanName, String interfaceName) {

	    this.beanName = beanName;
	    this.interfaceName = interfaceName;
	}

	public String getBeanName() {
	    return beanName;
	}

	public void setBeanName(String beanName) {
	    this.beanName = beanName;
	}

	public String getInterfaceName() {
	    return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
	    this.interfaceName = interfaceName;
	}
    }

    /**
     * Creates JNDI name prefixes for ejb objects
     * 
     * @param jndiName
     * @return
     */
    public static String createJpaJndiName(String jndiName) {

	return String.format("%s%s", Configuration.JPA_NAME, jndiName);
    }

    public static String formatJpaJndiName(String jndiName) {

	String name = jndiName.replace(Configuration.JPA_NAME, "");

	return name;
    }

    public static String createEjbJndiName(String jndiName) {

	return String.format("%s%s", Configuration.EJB_NAME, jndiName);
    }

    public static String formatEjbJndiName(String jndiName) {

	String name = jndiName.replace(Configuration.EJB_NAME, "");

	return name;
    }

    /**
     * Clears jndi prefix "java:/" for data source name
     * 
     * @param jndiName
     * @return {@link String}
     */
    public static String clearDataSourceName(String jndiName) {

	String clearName;
	if (ObjectUtils.available(jndiName)
		&& jndiName.startsWith(DS_JNDI_FREFIX)) {
	    clearName = jndiName.replace(DS_JNDI_FREFIX,
		    ObjectUtils.EMPTY_STRING);
	} else {
	    clearName = jndiName;
	}

	return clearName;
    }

    /**
     * Adds jndi prefix "java:/" to data source name
     * 
     * @param clearName
     * @return {@link String}
     */
    public static String toJndiDataSourceName(String clearName) {

	String jndiName;
	if (ObjectUtils.available(clearName)
		&& !clearName.contains(DS_JNDI_FREFIX)) {
	    jndiName = new StringBuilder().append(DS_JNDI_FREFIX)
		    .append(clearName).toString();
	} else {
	    jndiName = clearName;
	}

	return jndiName;
    }

    /**
     * Parses bean jndi name for lookup bean
     * 
     * @param jndiName
     * @return {@link BeanDescriptor}
     */
    public static BeanDescriptor parseEjbJndiName(String jndiName) {

	String pureName = jndiName.substring(Configuration.EJB_NAME_LENGTH);
	String[] formatedNames = pureName.split("\\");
	String beanNames = formatedNames[1];
	String[] beanDescriptors = beanNames.split("!");

	String interfaceName = beanDescriptors[0];
	String beanName = beanDescriptors[1];

	BeanDescriptor descriptor = new BeanDescriptor(beanName, interfaceName);

	return descriptor;

    }
}
