package org.lightmare.ejb.handlers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.lightmare.ejb.meta.MetaData;
import org.lightmare.jpa.jta.UserTransactionImpl;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Handler class to intercept bean method calls to provide database transactions
 * 
 * @author Levan
 * 
 */
public class BeanHandler implements InvocationHandler {

    private final Object bean;

    private final EntityManagerFactory emf;

    private final Field connectionField;

    private final Field transactionField;

    private final Field unitField;

    public BeanHandler(final MetaData metaData, final Object bean) {

	this.bean = bean;
	this.emf = metaData.getEmf();
	this.connectionField = metaData.getConnectorField();
	this.transactionField = metaData.getTransactionField();
	this.unitField = metaData.getUnitField();
    }

    /**
     * Sets passed value to beans {@link Field}
     * 
     * @param field
     * @param value
     * @throws IOException
     */
    private void setFieldValue(Field field, Object value) throws IOException {
	MetaUtils.setFieldValue(field, bean, value);
    }

    /**
     * Invokes passed bean {@link Method}
     * 
     * @param method
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    private Object invoke(Method method, Object... arguments)
	    throws IOException {
	return MetaUtils.invoke(method, bean, arguments);
    }

    /**
     * Sets {@link EntityManager} at beans's annotated field
     * 
     * @param em
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void setConnection(EntityManager em) throws IOException {
	setFieldValue(connectionField, em);
    }

    private void setTransactionField(EntityManager em) throws IOException {

	setConnection(em);
	if (transactionField != null) {
	    EntityTransaction entityTransaction = em.getTransaction();
	    UserTransaction transaction = new UserTransactionImpl(
		    entityTransaction);
	    setFieldValue(transactionField, transaction);
	}
    }

    /**
     * Creates {@link EntityManager} if passed {@link EntityManagerFactory} is
     * not null
     * 
     * @return {@link EntityManager}
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private EntityManager createEntityManager() throws IOException {
	EntityManager em = null;
	if (emf != null) {
	    em = emf.createEntityManager();
	    if (unitField != null) {
		setFieldValue(unitField, emf);
	    }
	    setTransactionField(em);
	}

	return em;
    }

    /**
     * Creates {@link EntityTransaction} and {@link UserTransaction} and begins
     * if there is not {@link javax.annotation.Resource} annotation in current
     * bean and returns this {@link EntityTransaction} or <code>null</code> in
     * another case
     * 
     * @param em
     * @return {@link UserTransaction}
     */
    private UserTransaction beginTransaction(EntityManager em)
	    throws IOException {
	UserTransaction transaction = null;
	if (transactionField == null) {
	    EntityTransaction entityTransaction = em.getTransaction();
	    transaction = new UserTransactionImpl(entityTransaction);
	    try {
		transaction.begin();
	    } catch (NotSupportedException ex) {
		throw new IOException(ex);
	    } catch (SystemException ex) {
		throw new IOException(ex);
	    }
	}

	return transaction;
    }

    /**
     * Closes {@link EntityManager} if it open
     * 
     * @param em
     */
    private void closeEntityManager(EntityManager em) {
	if (em != null && em.isOpen()) {
	    em.close();
	}
    }

    /**
     * Closes {@link EntityManager} if there is not
     * {@link javax.annotation.Resource} annotation in current bean
     * 
     * @param transaction
     * @param em
     */
    private void close(UserTransaction transaction, EntityManager em)
	    throws IOException {

	if (transactionField == null) {
	    try {
		transaction.commit();
	    } catch (SecurityException ex) {
		throw new IOException(ex);
	    } catch (IllegalStateException ex) {
		throw new IOException(ex);
	    } catch (RollbackException ex) {
		throw new IOException(ex);
	    } catch (HeuristicMixedException ex) {
		throw new IOException(ex);
	    } catch (HeuristicRollbackException ex) {
		throw new IOException(ex);
	    } catch (SystemException ex) {
		throw new IOException(ex);
	    }
	}

	closeEntityManager(em);
    }

    /**
     * Invokes method surrounded with {@link UserTransaction} begin and commit
     * 
     * @param em
     * @param method
     * @param arguments
     * @return Object
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private Object invokeTransaction(final EntityManager em,
	    final Method method, Object[] arguments) throws IOException {
	UserTransaction transaction = beginTransaction(em);
	Object value = invoke(method, arguments);
	close(transaction, em);

	return value;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments)
	    throws Throwable {

	EntityManager em = createEntityManager();

	try {
	    Method realMethod = bean.getClass().getDeclaredMethod(
		    method.getName(), method.getParameterTypes());
	    TransactionAttribute transaction = realMethod
		    .getAnnotation(TransactionAttribute.class);
	    Object value;

	    if (transaction == null) {
		value = realMethod.invoke(bean, arguments);
	    } else {
		value = invokeTransaction(em, realMethod, arguments);
	    }

	    return value;

	} finally {
	    closeEntityManager(em);
	}
    }
}
