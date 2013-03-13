package org.lightmare.jpa.jta;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.ejb.EJBException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.hibernate.cfg.NotYetImplementedException;
import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.ejb.meta.MetaContainer;
import org.lightmare.ejb.meta.MetaData;

/**
 * Class to manage {@link javax.transaction.UserTransaction} for
 * {@link javax.ejb.Stateless} bean {@link java.lang.reflect.Proxy} calls
 * 
 * @author levan
 * 
 */
public class BeanTransactions {

    private static final String MANDATORY_ERROR = "TransactionAttributeType.MANDATORY must always be called within transaction";

    private static final String NEVER_ERROR = "TransactionAttributeType.NEVER is called within transaction";

    private static final String SUPPORTS_ERROR = "TransactionAttributeType.SUPPORTS is not yet implemented";

    /**
     * Gets existing transaction from cache
     * 
     * @param entityTransactions
     * @return {@link UserTransaction}
     */
    public static UserTransaction getTransaction(
	    EntityTransaction... entityTransactions) {

	UserTransaction transaction = MetaContainer.getTransaction();
	if (transaction == null) {
	    transaction = new UserTransactionImpl(entityTransactions);
	    MetaContainer.setTransaction(transaction);
	}

	return transaction;
    }

    /**
     * Gets appropriated {@link TransactionAttributeType} for instant
     * {@link Method} of {@link javax.ejb.Stateless} bean
     * 
     * @param metaData
     * @param method
     * @return {@link TransactionAttributeType}
     */
    public static TransactionAttributeType getTransactionType(
	    MetaData metaData, Method method) {

	TransactionAttributeType attrType = metaData.getTransactionAttrType();
	TransactionManagementType manType = metaData.getTransactionManType();

	TransactionAttribute attr = method
		.getAnnotation(TransactionAttribute.class);

	TransactionAttributeType type;
	if (manType.equals(TransactionManagementType.CONTAINER)) {

	    if (attr == null) {
		type = attrType;
	    } else {
		type = attr.value();
	    }
	} else {
	    type = null;
	}

	return type;
    }

    /**
     * Gets status of passed transaction by {@link UserTransaction#getStatus()}
     * method call
     * 
     * @param transaction
     * @return <code>int</code>
     * @throws IOException
     */
    private static int getStatus(UserTransaction transaction)
	    throws IOException {

	int status;
	try {
	    status = transaction.getStatus();
	} catch (SystemException ex) {
	    throw new IOException(ex);
	}

	return status;
    }

    /**
     * Decides whether create or join {@link UserTransaction} by
     * {@link TransactionAttribute} annotation
     * 
     * @param handler
     * @param type
     * @param transaction
     * @param em
     * @throws IOException
     */
    private static void addTransaction(BeanHandler handler,
	    TransactionAttributeType type, UserTransactionImpl transaction,
	    EntityManager em) throws IOException {

	EntityTransaction entityTransaction;
	if (type.equals(TransactionAttributeType.REQUIRED)) {

	    Object caller = transaction.getCaller();
	    if (caller == null) {
		transaction.setCaller(handler);
	    }

	    entityTransaction = em.getTransaction();
	    entityTransaction.begin();
	    transaction.addTransaction(entityTransaction);

	} else if (type.equals(TransactionAttributeType.REQUIRES_NEW)) {

	    entityTransaction = em.getTransaction();
	    entityTransaction.begin();
	    transaction.pushReqNew(entityTransaction);

	} else if (type.equals(TransactionAttributeType.MANDATORY)) {

	    int status = getStatus(transaction);
	    if (status == 0) {
		throw new EJBException(MANDATORY_ERROR);
	    } else {
		entityTransaction = em.getTransaction();
		entityTransaction.begin();
		transaction.addTransaction(entityTransaction);
	    }
	} else if (type.equals(TransactionAttributeType.NEVER)) {

	    int status = getStatus(transaction);
	    if (status > 0) {
		throw new EJBException(NEVER_ERROR);
	    }

	} else if (type.equals(TransactionAttributeType.SUPPORTS)) {

	    throw new NotYetImplementedException(SUPPORTS_ERROR);
	}
    }

    /**
     * Defines which {@link TransactionAttribute} is used on bean {@link Class}
     * and decides whether create or join {@link UserTransaction} by this
     * annotation
     * 
     * @param handler
     * @param method
     * @param entityTransaction
     * @throws IOException
     */
    public static void addTransaction(BeanHandler handler, Method method,
	    EntityManager em) throws IOException {

	MetaData metaData = handler.getMetaData();
	TransactionAttributeType type = getTransactionType(metaData, method);
	UserTransactionImpl transaction = (UserTransactionImpl) getTransaction();
	if (type != null) {
	    addTransaction(handler, type, transaction, em);
	}

    }

    /**
     * Commits passed {@link UserTransaction} with {@link IOException} throw
     * 
     * @param transaction
     * @throws IOException
     */
    private static void commit(UserTransaction transaction) throws IOException {

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

    /**
     * Decides whether commit or not {@link UserTransaction} by
     * {@link TransactionAttribute} annotation
     * 
     * @param handler
     * @param method
     * @throws IOException
     */
    public static void commitTransaction(BeanHandler handler, Method method)
	    throws IOException {

	TransactionAttributeType type = getTransactionType(
		handler.getMetaData(), method);
	UserTransactionImpl transaction = (UserTransactionImpl) getTransaction();

	if (type.equals(TransactionAttributeType.REQUIRED)) {

	    boolean check = transaction.checkCaller(handler);
	    if (check) {
		commit(transaction);
	    }
	} else if (type.equals(TransactionAttributeType.REQUIRES_NEW)) {

	    transaction.commitReqNew();
	}
    }
}
