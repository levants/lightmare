package org.lightmare.ejb.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * Handler class to intercept bean method calls to provide database transactions
 * 
 * @author Levan
 * 
 */
public class BeanHandler implements InvocationHandler {

	private final Field field;

	private final Object bean;

	private final EntityManagerFactory emf;

	public BeanHandler(final Field field, final Object bean,
			final EntityManagerFactory emf) {
		this.field = field;
		this.bean = bean;
		this.emf = emf;
	}

	/**
	 * Sets {@link EntityManager} at beans's annotated field
	 * 
	 * @param em
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void setConnection(EntityManager em)
			throws IllegalArgumentException, IllegalAccessException {
		boolean access = field.isAccessible();
		if (!access) {
			field.setAccessible(true);
		}
		field.set(bean, em);
		field.setAccessible(access);
	}

	/**
	 * Creates {@link EntityManager} if passed {@link EntityManagerFactory} is
	 * not null
	 * 
	 * @return {@link EntityManager}
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private EntityManager createEntityManager()
			throws IllegalArgumentException, IllegalAccessException {
		EntityManager em = null;
		if (emf != null) {
			em = emf.createEntityManager();
			setConnection(em);
		}

		return em;
	}

	private void closeEntityManager(EntityManager em) {
		if (em != null && em.isOpen()) {
			em.close();
		}
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
			final Method method, Object[] arguments) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		Object value = method.invoke(bean, arguments);
		transaction.commit();
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
