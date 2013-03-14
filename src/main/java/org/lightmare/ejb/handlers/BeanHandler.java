package org.lightmare.ejb.handlers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.lightmare.ejb.meta.MetaData;
import org.lightmare.jpa.jta.BeanTransactions;
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

    private final MetaData metaData;

    public BeanHandler(final MetaData metaData, final Object bean) {

	this.bean = bean;
	this.emf = metaData.getEmf();
	this.connectionField = metaData.getConnectorField();
	this.transactionField = metaData.getTransactionField();
	this.unitField = metaData.getUnitField();
	this.metaData = metaData;
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

    /**
     * Creates and caches {@link UserTransaction} per thread
     * 
     * @param em
     * @return {@link UserTransaction}
     */
    private UserTransaction getTransaction(EntityManager em) {

	UserTransaction transaction = BeanTransactions.getTransaction(em);

	return transaction;
    }

    private void setTransactionField(EntityManager em) throws IOException {

	if (transactionField != null) {
	    UserTransaction transaction = getTransaction(em);
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
	    setConnection(em);
	}

	return em;
    }

    /**
     * Closes {@link EntityManager} if there is not
     * {@link javax.annotation.Resource} annotation in current bean
     * 
     * @param transaction
     * @param em
     */
    private void close(Method method) throws IOException {

	if (transactionField == null) {

	    BeanTransactions.commitTransaction(this, method);
	} else {

	    BeanTransactions.closeEntityManagers();
	}
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
    private Object invokeBeanMethod(final EntityManager em,
	    final Method method, Object[] arguments) throws IOException {

	if (transactionField == null) {
	    BeanTransactions.addTransaction(this, method, em);
	} else {
	    setTransactionField(em);
	}
	Object value = invoke(method, arguments);
	close(method);

	return value;
    }

    /**
     * Calls {@link BeanTransactions#rollbackTransaction(BeanHandler, Method))}
     * is case of {@link Throwable} is thrown at passed {@link Method} execution
     * time
     * 
     * @param method
     * @throws IOException
     */
    private void rollback(Method method) throws IOException {

	if (method != null) {
	    BeanTransactions.rollbackTransaction(this, method);
	}
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments)
	    throws Throwable {

	EntityManager em = createEntityManager();
	Method realMethod = null;

	try {
	    realMethod = bean.getClass().getDeclaredMethod(method.getName(),
		    method.getParameterTypes());
	    Object value;

	    value = invokeBeanMethod(em, realMethod, arguments);

	    return value;

	} catch (Throwable th) {
	    rollback(realMethod);
	    throw new Throwable(th);
	}
    }

    public MetaData getMetaData() {

	return metaData;
    }
}
