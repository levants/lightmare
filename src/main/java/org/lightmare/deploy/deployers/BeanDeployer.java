/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
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
package org.lightmare.deploy.deployers;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.Interceptors;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.log4j.Logger;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.ConnectionData;
import org.lightmare.cache.ConnectionSemaphore;
import org.lightmare.cache.DeployData;
import org.lightmare.cache.InjectionData;
import org.lightmare.cache.InterceptorData;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.config.Configuration;
import org.lightmare.deploy.BeanLoader.BeanParameters;
import org.lightmare.deploy.LoaderPoolManager;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.deploy.ORMCreator;
import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.rest.providers.RestProvider;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.beans.BeanUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.fs.WatchUtils;
import org.lightmare.utils.logging.LogUtils;
import org.lightmare.utils.namimg.NamingUtils;
import org.lightmare.utils.reflect.ClassUtils;
import org.lightmare.utils.rest.RestCheck;

/**
 * {@link Callable} implementation for deploying {@link javax.ejb.Stateless}
 * session beans and cache {@link MetaData} keyed by bean name
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 */
public class BeanDeployer implements Callable<String> {

    private MetaCreator creator;

    private String beanName;

    private String className;

    private ClassLoader loader;

    private List<File> tmpFiles;

    private MetaData metaData;

    private final CountDownLatch blocker;

    private boolean countedDown;

    private List<Field> unitFields;

    private DeployData deployData;

    private boolean chekcWatch;

    // Injection fields which should be accessible
    private List<AccessibleObject> accessibleFields;

    // Configuration instance for deployment
    private Configuration configuration;

    // Messages
    private static final String DEPLOYED_MESSAGE = "bean %s deployed";

    private static final String COULD_NOT_DEPLOY_MESSAGE = "Could not deploy bean %s cause %s";

    private static final Logger LOG = Logger.getLogger(BeanDeployer.class);

    /**
     * Constructor with {@link BeanParameters} deployments parameters
     * 
     * @param parameters
     */
    public BeanDeployer(BeanParameters parameters) {
	this.creator = parameters.creator;
	this.beanName = parameters.beanName;
	this.className = parameters.className;
	this.loader = parameters.loader;
	this.tmpFiles = parameters.tmpFiles;
	this.metaData = parameters.metaData;
	this.blocker = parameters.blocker;
	this.deployData = parameters.deployData;
	this.configuration = parameters.configuration;
    }

    /**
     * Adds {@link AccessibleObject} to cache to set accessible flag after
     * 
     * @param accessibleObject
     */
    private void addAccessibleField(AccessibleObject accessibleObject) {

	if (accessibleFields == null) {
	    accessibleFields = new ArrayList<AccessibleObject>();
	}

	accessibleFields.add(accessibleObject);
    }

    /**
     * Initializes {@link ORMCreator} instance and connection for EJB module
     * 
     * @param semaphore
     * @throws IOException
     */
    private void initOrmCreator(ConnectionSemaphore semaphore)
	    throws IOException {

	ORMCreator orm = new ORMCreator.Builder(creator)
		.setUnitName(semaphore.getUnitName()).setBeanName(beanName)
		.setClassLoader(loader).setConfiguration(configuration).build();
	orm.configureConnection();
    }

    /**
     * Locks {@link ConnectionSemaphore} if needed for connection processing
     * 
     * @param semaphore
     * @param unitName
     * @param jndiName
     * @throws IOException
     */
    private void lockSemaphore(ConnectionSemaphore semaphore)
	    throws IOException {

	synchronized (semaphore) {
	    if (ObjectUtils.notTrue(semaphore.isCheck())) {
		try {
		    initOrmCreator(semaphore);
		} finally {
		    semaphore.notifyAll();
		}
	    }
	}
    }

    /**
     * Increases {@link CountDownLatch} blocker if it is first time in current
     * thread
     */
    private void releaseBlocker() {

	if (ObjectUtils.notTrue(countedDown)) {
	    blocker.countDown();
	    countedDown = Boolean.TRUE;
	}
    }

    /**
     * Checks if bean {@link MetaData} with same name already cached if it is
     * increases {@link CountDownLatch} for connection and throws
     * {@link BeanInUseException} else caches meta data with associated name
     * 
     * @param beanEjbName
     * @throws BeanInUseException
     */
    private void checkAndSetBean(String beanEjbName) throws BeanInUseException {

	try {
	    MetaContainer.checkAndAddMetaData(beanEjbName, metaData);
	} catch (BeanInUseException ex) {
	    releaseBlocker();
	    throw ex;
	}
    }

    /**
     * Caches {@link Field}s with persistence injection
     * 
     * @param unitField
     */
    private void addUnitField(Field unitField) {

	if (unitFields == null) {
	    unitFields = new ArrayList<Field>();
	}

	unitFields.add(unitField);
    }

    /**
     * Checks weather connection with passed unit or JNDI name already exists
     * 
     * @param unitName
     * @param jndiName
     * @return <code>boolean</code>
     */
    private boolean checkOnEmf(String unitName, String jndiName) {

	boolean checkForEmf = ConnectionContainer.checkForEmf(unitName);

	if (StringUtils.valid(jndiName)) {
	    jndiName = NamingUtils.createJpaJndiName(jndiName);
	    checkForEmf = checkForEmf
		    && ConnectionContainer.checkForEmf(jndiName);
	}

	return checkForEmf;
    }

    /**
     * Creates {@link ConnectionSemaphore} if such does not exists
     * 
     * @param context
     * @param field
     * @return <code>boolean</code>
     * @throws IOException
     */
    private void identifyConnections(PersistenceContext context,
	    Field connectionField) throws IOException {

	ConnectionData connection = new ConnectionData();
	connection.setConnectionField(connectionField);
	String unitName = context.unitName();
	String jndiName = context.name();
	connection.setUnitName(unitName);
	connection.setJndiName(jndiName);

	ConnectionSemaphore semaphore;
	boolean checkForEmf = checkOnEmf(unitName, jndiName);
	if (checkForEmf) {
	    releaseBlocker();
	    semaphore = ConnectionContainer.getSemaphore(unitName);
	    connection.setConnection(semaphore);
	} else {
	    // Sets connection semaphore for this connection
	    semaphore = ConnectionContainer.cacheSemaphore(unitName, jndiName);
	    connection.setConnection(semaphore);
	    releaseBlocker();
	    if (ObjectUtils.notNull(semaphore)) {
		lockSemaphore(semaphore);
	    }
	}
	metaData.addConnection(connection);
    }

    /**
     * Caches {@link EJB} annotated fields
     * 
     * @param beanClass
     */
    private void cacheInjectFields(Field field) {

	EJB ejb = field.getAnnotation(EJB.class);
	Class<?> interfaceClass = ejb.beanInterface();
	if (interfaceClass == null || interfaceClass.equals(Object.class)) {
	    interfaceClass = field.getType();
	}

	String name = ejb.beanName();
	if (name == null || name.isEmpty()) {
	    name = BeanUtils.nameFromInterface(interfaceClass);
	}

	String description = ejb.description();
	String mappedName = ejb.mappedName();
	Class<?>[] interfaceClasses = { interfaceClass };
	InjectionData injectionData = new InjectionData();
	injectionData.setField(field);
	injectionData.setInterfaceClasses(interfaceClasses);
	injectionData.setName(name);
	injectionData.setDescription(description);
	injectionData.setMappedName(mappedName);
	metaData.addInject(injectionData);
    }

    /**
     * Retrieves and caches {@link Field}s with injection
     * 
     * @param field
     * @throws IOException
     */
    private void retriveConnection(Field field) throws IOException {

	PersistenceContext context = field
		.getAnnotation(PersistenceContext.class);
	Resource resource = field.getAnnotation(Resource.class);
	PersistenceUnit unit = field.getAnnotation(PersistenceUnit.class);
	EJB ejbAnnot = field.getAnnotation(EJB.class);
	if (ObjectUtils.notNull(context)) {
	    identifyConnections(context, field);
	    addAccessibleField(field);
	} else if (ObjectUtils.notNull(resource)) {
	    metaData.setTransactionField(field);
	    addAccessibleField(field);
	} else if (ObjectUtils.notNull(unit)) {
	    addUnitField(field);
	    addAccessibleField(field);
	} else if (ObjectUtils.notNull(ejbAnnot)) {
	    // caches EJB annotated fields
	    cacheInjectFields(field);
	    addAccessibleField(field);
	}
    }

    /**
     * Finds and caches {@link PersistenceContext}, {@link PersistenceUnit} and
     * {@link Resource} annotated {@link Field}s in bean class and configures
     * connections and creates {@link ConnectionSemaphore}s if it does not
     * exists for {@link PersistenceContext#unitName()} object
     * 
     * @throws IOException
     */
    private void retrieveConnections() throws IOException {

	Class<?> beanClass = metaData.getBeanClass();
	Field[] fields = beanClass.getDeclaredFields();
	if (CollectionUtils.invalid(fields)) {
	    releaseBlocker();
	}

	for (Field field : fields) {
	    retriveConnection(field);
	}

	if (CollectionUtils.valid(unitFields)) {
	    metaData.addUnitFields(unitFields);
	}

	// Sets fields for injection (PersistenceContext, PersistenceUnit,
	// Resource, EJB) as accessible
	if (CollectionUtils.valid(accessibleFields)) {
	    AccessibleObject[] accessibleObjects = CollectionUtils.toArray(
		    accessibleFields, AccessibleObject.class);
	    AccessibleObject.setAccessible(accessibleObjects, Boolean.TRUE);
	}
    }

    /**
     * Creates {@link MetaData} for bean class
     * 
     * @param beanClass
     * @throws ClassNotFoundException
     */
    private void createMeta(Class<?> beanClass) throws IOException {

	metaData.setBeanClass(beanClass);
	if (Configuration.isServer()) {
	    retrieveConnections();
	} else {
	    releaseBlocker();
	}
	metaData.setLoader(loader);
    }

    /**
     * Checks if bean class is annotated as {@link TransactionAttribute} and
     * {@link TransactionManagement} and caches
     * {@link TransactionAttribute#value()} and
     * {@link TransactionManagement#value()} in {@link MetaData} object
     * 
     * @param beanClass
     */
    private void checkOnTransactional(Class<?> beanClass) {

	TransactionAttribute transactionAttribute = beanClass
		.getAnnotation(TransactionAttribute.class);
	TransactionManagement transactionManagement = beanClass
		.getAnnotation(TransactionManagement.class);
	boolean transactional = Boolean.FALSE;
	TransactionAttributeType transactionAttrType;
	TransactionManagementType transactionManType;
	if (transactionAttribute == null) {
	    transactional = Boolean.TRUE;
	    transactionAttrType = TransactionAttributeType.REQUIRED;
	    transactionManType = TransactionManagementType.CONTAINER;
	} else if (transactionManagement == null) {
	    transactionAttrType = transactionAttribute.value();
	    transactionManType = TransactionManagementType.CONTAINER;
	} else {
	    transactionAttrType = transactionAttribute.value();
	    transactionManType = transactionManagement.value();
	}

	metaData.setTransactional(transactional);
	metaData.setTransactionAttrType(transactionAttrType);
	metaData.setTransactionManType(transactionManType);
    }

    /**
     * Caches {@link Interceptors} annotation defined data
     * 
     * @param beanClass
     * @param interceptorClasses
     * @throws IOException
     */
    private void cacheInterceptors(Class<?> beanClass,
	    Class<?>[] interceptorClasses, Method beanMethod)
	    throws IOException {

	int length = interceptorClasses.length;
	Class<?> interceptorClass;
	List<Method> interceptorMethods;
	Method interceptorMethod;
	for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
	    interceptorClass = interceptorClasses[i];
	    interceptorMethods = ClassUtils.getAnnotatedMethods(beanClass,
		    AroundInvoke.class);
	    interceptorMethod = CollectionUtils.getFirst(interceptorMethods);
	    InterceptorData data = new InterceptorData();
	    data.setBeanClass(beanClass);
	    data.setBeanMethod(beanMethod);
	    data.setInterceptorClass(interceptorClass);
	    data.setInterceptorMethod(interceptorMethod);
	    metaData.addInterceptor(data);
	}
    }

    /**
     * Caches {@link Interceptor}, bean {@link Class} and {@link Method}s
     * parameters
     * 
     * @param interceptors
     * @param beanClass
     * @param beanMethods
     * @throws IOException
     */
    private void cacheInterceptors(Interceptors interceptors,
	    Class<?> beanClass, Method... beanMethods) throws IOException {

	Class<?>[] interceptorClasses = interceptors.value();
	if (CollectionUtils.valid(interceptorClasses)) {
	    Method beanMethod = CollectionUtils.getFirst(beanMethods);
	    cacheInterceptors(beanClass, interceptorClasses, beanMethod);
	}
    }

    /**
     * Identifies and caches {@link Interceptors} annotation data
     * 
     * @throws IOException
     */
    private void identifyInterceptors(Class<?> beanClass) throws IOException {

	Interceptors interceptors = beanClass.getAnnotation(Interceptors.class);
	if (ObjectUtils.notNull(interceptors)) {
	    cacheInterceptors(interceptors, beanClass);
	}

	List<Method> beanMethods = ClassUtils.getAnnotatedMethods(beanClass,
		Interceptors.class);
	if (CollectionUtils.valid(beanMethods)) {
	    for (Method beanMethod : beanMethods) {
		interceptors = beanMethod.getAnnotation(Interceptors.class);
		cacheInterceptors(interceptors, beanClass, beanMethod);
	    }
	}
    }

    private Class<?>[] toArray(Set<Class<?>> interfacesSet) {
	return interfacesSet.toArray(new Class<?>[interfacesSet.size()]);
    }

    /**
     * Retrieves interface classes from passed {@link Local} or {@link Remote}
     * annotation
     * 
     * @param annotation
     * @return {@link Class}[] array of interface classes
     */
    private Class<?>[] ejbInterfaces(Annotation annotation) {

	Class<?>[] interfaces;

	if (annotation instanceof Remote) {
	    interfaces = ObjectUtils.cast(annotation, Remote.class).value();
	} else if (annotation instanceof Local) {
	    interfaces = ObjectUtils.cast(annotation, Local.class).value();
	} else {
	    interfaces = null;
	}

	return interfaces;
    }

    /**
     * Identifies and caches EJB bean's {@link Local} and / or {@link Remote}
     * annotated interfaces
     * 
     * @param beanClass
     */
    private void initInterfaces(Class<?> beanClass,
	    Class<? extends Annotation> type) {

	Class<?>[] interfaces = null;
	Set<Class<?>> interfacesSet = new HashSet<Class<?>>();
	Annotation ejbAnnotation = beanClass.getAnnotation(type);
	Class<?>[] ejbInterfaces = beanClass.getInterfaces();

	if (ObjectUtils.notNull(ejbAnnotation)) {
	    interfaces = ejbInterfaces(ejbAnnotation);
	    if (CollectionUtils.valid(interfaces)) {
		interfacesSet.addAll(Arrays.asList(interfaces));
	    }
	}

	for (Class<?> interfaceClass : ejbInterfaces) {
	    if (interfaceClass.isAnnotationPresent(type))
		interfacesSet.add(interfaceClass);
	}

	if (CollectionUtils.valid(interfacesSet)) {
	    interfaces = toArray(interfacesSet);
	    if (ejbAnnotation instanceof Local) {
		metaData.setRemoteInterfaces(interfaces);
	    } else if (ejbAnnotation instanceof Remote) {
		metaData.setRemoteInterfaces(interfaces);
	    }
	}
    }

    /**
     * Identifies and caches EJB bean interfaces
     * 
     * @param beanClass
     */
    private void indentifyInterfaces(Class<?> beanClass) {
	initInterfaces(beanClass, Local.class);
	initInterfaces(beanClass, Remote.class);
    }

    /**
     * Loads and caches bean {@link Class} by name
     * 
     * @return
     * @throws IOException
     */
    private String createBeanClass() throws IOException {

	String beanEjbName;

	try {
	    Class<?> beanClass = ClassUtils.classForName(className,
		    Boolean.FALSE, loader);
	    checkOnTransactional(beanClass);
	    beanEjbName = BeanUtils.beanName(beanClass);
	    checkAndSetBean(beanEjbName);
	    if (RestCheck.check(beanClass)) {
		RestProvider.add(beanClass);
	    }

	    createMeta(beanClass);
	    indentifyInterfaces(beanClass);
	    identifyInterceptors(beanClass);
	    metaData.setInProgress(Boolean.FALSE);
	} catch (IOException ex) {
	    releaseBlocker();
	    throw ex;
	}

	return beanEjbName;
    }

    /**
     * Deploys EJB application from file
     * 
     * @return {@link String}
     */
    private String deployFile() {

	String deployed = beanName;

	ClassLoader currentLoader = LoaderPoolManager.getCurrent();
	try {
	    LibraryLoader.loadCurrentLibraries(loader);
	    deployed = createBeanClass();
	    chekcWatch = WatchUtils.checkForWatch(deployData);
	    if (chekcWatch) {
		URL url = deployData.getUrl();
		url = WatchUtils.clearURL(url);
		MetaContainer.addBeanName(url, deployed);
	    }
	    LogUtils.info(LOG, DEPLOYED_MESSAGE, beanName);
	} catch (IOException ex) {
	    LogUtils.error(LOG, ex, COULD_NOT_DEPLOY_MESSAGE, beanName,
		    ex.getMessage());
	} finally {
	    LibraryLoader.loadCurrentLibraries(currentLoader);
	}

	return deployed;
    }

    /**
     * Deploys EJB bean from extracted EAR or JAR file
     * 
     * @return {@link String} EJB bean name
     */
    private String deployExtracted() {

	String deployed;

	synchronized (tmpFiles) {
	    try {
		deployed = deployFile();
	    } finally {
		tmpFiles.notifyAll();
	    }
	}

	return deployed;
    }

    /**
     * Deploys EJB bean from extracted EAR file or JAR file
     * 
     * @return {@link String} EJB bean name
     */
    private String deployBean() {

	String deployed;

	if (ObjectUtils.notNull(tmpFiles)) {
	    deployed = deployExtracted();
	} else {
	    deployed = deployFile();
	}

	return deployed;
    }

    /**
     * Deploys EJB bean from extracted EAR file or JAR file
     * 
     * @return {@link String} EJB bean name
     */
    private String deploy() {

	String deployed;

	synchronized (metaData) {
	    try {
		deployed = deployBean();
	    } catch (Exception ex) {
		LOG.error(ex.getMessage(), ex);
		deployed = null;
	    } finally {
		releaseBlocker();
		metaData.notifyAll();
	    }
	}

	return deployed;
    }

    @Override
    public String call() throws Exception {
	return deploy();
    }
}
