package org.lightmare.utils;

/**
 * Utility class for JNDI names
 * 
 * @author levan
 * @since 0.0.60-SNAPSHOT
 */
public class NamingUtils {

    // User transaction JNDI name
    public static final String USER_TRANSACTION_NAME = "java:comp/UserTransaction";

    // String prefixes for EJB JNDI names
    public static final String JPA_NAME_PREF = "java:comp/env/";

    public static final String EJB_NAME_PREF = "ejb:";

    private static final String DS_JNDI_FREFIX = "java:/";

    private static final String EJB_NAME_DELIM = "\\";

    private static final String EJB_APP_DELIM = "!";

    // Digital values for naming utilities
    public static final int EJB_NAME_LENGTH = 4;

    private static final int BEAN_NAMES_INDEX = 1;

    private static final int INTERFACE_IDEX = 0;

    private static final int BEAN_INDEX = 1;

    // Error messages
    public static final String COULD_NOT_UNBIND_NAME_ERROR = "Could not unbind jndi name %s cause %s";

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
     * Creates JNDI name prefixes for JPA objects
     * 
     * @param jndiName
     * @return
     */
    public static String createJpaJndiName(String jndiName) {

	return StringUtils.concat(JPA_NAME_PREF, jndiName);
    }

    /**
     * Converts passed JNDI name to JPA name
     * 
     * @param jndiName
     * @return {@link String}
     */
    public static String formatJpaJndiName(String jndiName) {

	String name = jndiName.replace(JPA_NAME_PREF, StringUtils.EMPTY_STRING);

	return name;
    }

    /**
     * Creates EJB names from passed JNDI name
     * 
     * @param jndiName
     * @return {@link String}
     */
    public static String createEjbJndiName(String jndiName) {

	return StringUtils.concat(EJB_NAME_PREF, jndiName);
    }

    /**
     * Converts passed JNDI name to bean name
     * 
     * @param jndiName
     * @return {@link String}
     */
    public static String formatEjbJndiName(String jndiName) {

	String name = jndiName.replace(EJB_NAME_PREF, StringUtils.EMPTY_STRING);

	return name;
    }

    /**
     * Clears JNDI prefix "java:/" for data source name
     * 
     * @param jndiName
     * @return {@link String}
     */
    public static String clearDataSourceName(String jndiName) {

	String clearName;
	if (StringUtils.valid(jndiName) && jndiName.startsWith(DS_JNDI_FREFIX)) {
	    clearName = jndiName.replace(DS_JNDI_FREFIX,
		    StringUtils.EMPTY_STRING);
	} else {
	    clearName = jndiName;
	}

	return clearName;
    }

    /**
     * Adds JNDI prefix "java:/" to data source name
     * 
     * @param clearName
     * @return {@link String}
     */
    public static String toJndiDataSourceName(String clearName) {

	String jndiName;

	if (StringUtils.valid(clearName)
		&& StringUtils.notContains(clearName, DS_JNDI_FREFIX)) {
	    jndiName = StringUtils.concat(DS_JNDI_FREFIX, clearName);
	} else {
	    jndiName = clearName;
	}

	return jndiName;
    }

    /**
     * Parses bean JNDI name for lookup bean
     * 
     * @param jndiName
     * @return {@link BeanDescriptor}
     */
    public static BeanDescriptor parseEjbJndiName(String jndiName) {
	
BeanDescriptor descriptor;
	String pureName = jndiName.substring(EJB_NAME_LENGTH);
	String[] formatedNames = pureName.split(EJB_NAME_DELIM);
	String beanNames = formatedNames[BEAN_NAMES_INDEX];
	String[] beanDescriptors = beanNames.split(EJB_APP_DELIM);

	String interfaceName = beanDescriptors[INTERFACE_IDEX];
	String beanName = beanDescriptors[BEAN_INDEX];

	descriptor = new BeanDescriptor(beanName, interfaceName);

	return descriptor;
    }
}
