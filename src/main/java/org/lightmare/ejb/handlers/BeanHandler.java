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

import org.lightmare.ejb.meta.MetaData;

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
		boolean access = field.isAccessible();
		if (!access) {
			field.setAccessible(true);
		}
		try {
			field.set(bean, value);
		} catch (IllegalArgumentException ex) {
			throw new IOException(ex);
		} catch (IllegalAccessException ex) {
			throw new IOException(ex);
		}
		field.setAccessible(access);
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

		Object value;
		try {
			value = method.invoke(bean, arguments);
		} catch (IllegalAccessException ex) {
			throw new IOException(ex);
		} catch (IllegalArgumentException ex) {
			throw new IOException(ex);
		} catch (InvocationTargetException ex) {
			throw new IOException(ex);
		}

		return value;
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
			setFieldValue(transactionField, entityTransaction);
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
	 * Creates {@link EntityTransaction} and begins if there is not
	 * {@link javax.annotation.Resource} annotation in current bean and returns
	 * this {@link EntityTransaction} or <code>null</code> in another case
	 * 
	 * @param em
	 * @return {@link EntityTransaction}
	 */
	private EntityTransaction beginTransaction(EntityManager em) {
		EntityTransaction transaction = null;
		if (transactionField == null) {
			transaction = em.getTransaction();
			transaction.begin();
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
	private void close(EntityTransaction transaction, EntityManager em) {

		if (transactionField == null) {
			transaction.commit();
		}

		closeEntityManager(em);
	}

	/**
	 * Invokes method surrounded with {@link EntityTransaction} begin and commit
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
		EntityTransaction transaction = beginTransaction(em);
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

			if (transaction != null) {
				value = invokeTransaction(em, realMethod, arguments);
			} else {
				value = realMethod.invoke(bean, arguments);
			}

			return value;

		} finally {
			closeEntityManager(em);
		}
	}
}
