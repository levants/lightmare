package org.lightmare.jpa.jta;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.lightmare.cache.MetaData;
import org.lightmare.cache.TransactionHolder;
import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Class to manage {@link javax.transaction.UserTransaction} for
 * {@link javax.ejb.Stateless} bean {@link java.lang.reflect.Proxy} calls
 * 
 * @author levan
 * 
 */
public class BeanTransactions {

    // Error messages for inappropriate use of user transactions
    private static final String MANDATORY_ERROR = "TransactionAttributeType.MANDATORY must always be called within transaction";

    private static final String NEVER_ERROR = "TransactionAttributeType.NEVER is called within transaction";

    /**
     * Inner class to cache {@link EntityTransaction}s and {@link EntityManager}
     * s in one {@link Collection} for {@link UserTransaction} implementation
     * 
     * @author levan
     * 
     */
    protected static class TransactionData {

	EntityManager em;

	EntityTransaction entityTransaction;
    }

    /**
     * Creates new {@link TransactionData} object with passed
     * {@link EntityTransaction} and {@link EntityManager} instances
     * 
     * @param entityTransaction
     * @param em
     * @return {@link TransactionData}
     */
    private static TransactionData createTransactionData(
	    EntityTransaction entityTransaction, EntityManager em) {

	TransactionData transactionData = new TransactionData();

	transactionData.em = em;
	transactionData.entityTransaction = entityTransaction;

	return transactionData;
    }

    /**
     * Gets existing transaction from cache
     * 
     * @param entityTransactions
     * @return {@link UserTransaction}
     */
    public static UserTransaction getTransaction(
	    EntityTransaction... entityTransactions) {

	UserTransaction transaction = TransactionHolder.getTransaction();

	if (transaction == null) {
	    transaction = UserTransactionFactory.get(entityTransactions);
	    TransactionHolder.setTransaction(transaction);
	} else {

	    // If entityTransactions array is available then adds it to
	    // UserTransaction object
	    UserTransactionFactory.join(transaction, entityTransactions);
	}

	return transaction;
    }

    /**
     * Gets existing transaction from cache
     * 
     * @param entityTransactions
     * @return {@link UserTransaction}
     */
    public static UserTransaction getTransaction(Collection<EntityManager> ems) {

	UserTransaction transaction = TransactionHolder.getTransaction();

	if (transaction == null) {
	    transaction = UserTransactionFactory.get();
	    TransactionHolder.setTransaction(transaction);
	}

	Collection<TransactionData> entityTransactions = getEntityTransactions(ems);
	TransactionManager.addEntityTransactions(transaction,
		entityTransactions);

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

	TransactionAttributeType type;

	if (method == null) {
	    type = null;
	} else {
	    TransactionAttributeType attrType = metaData
		    .getTransactionAttrType();
	    TransactionManagementType manType = metaData
		    .getTransactionManType();

	    TransactionAttribute attr = method
		    .getAnnotation(TransactionAttribute.class);
	    if (manType.equals(TransactionManagementType.CONTAINER)) {

		if (attr == null) {
		    type = attrType;
		} else {
		    type = attr.value();
		}

	    } else {
		type = null;
	    }
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
     * Checks if transaction is active and if it is not begins transaction
     * 
     * @param entityTransaction
     */
    private static void beginEntityTransaction(
	    EntityTransaction entityTransaction) {

	if (ObjectUtils.notTrue(entityTransaction.isActive())) {
	    entityTransaction.begin();
	}
    }

    /**
     * Gets {@link EntityTransaction} from passed {@link EntityManager} and
     * begins it
     * 
     * @param em
     * @return {@link EntityTransaction}
     */
    private static EntityTransaction getEntityTransaction(EntityManager em) {

	EntityTransaction entityTransaction;

	if (em == null) {
	    entityTransaction = null;
	} else {
	    entityTransaction = em.getTransaction();
	    beginEntityTransaction(entityTransaction);
	}

	return entityTransaction;
    }

    /**
     * Gets {@link EntityTransaction} for each {@link EntityManager} and begins
     * it
     * 
     * @param ems
     * @return {@link Collection}<EntityTransaction>
     */
    private static Collection<TransactionData> getEntityTransactions(
	    Collection<EntityManager> ems) {

	Collection<TransactionData> entityTransactions;

	if (CollectionUtils.valid(ems)) {
	    entityTransactions = new ArrayList<TransactionData>();
	    for (EntityManager em : ems) {
		EntityTransaction entityTransaction = getEntityTransaction(em);
		TransactionData transactionData = createTransactionData(
			entityTransaction, em);
		entityTransactions.add(transactionData);
	    }
	} else {
	    entityTransactions = null;
	}

	return entityTransactions;
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
	    TransactionAttributeType type, UserTransaction transaction,
	    Collection<EntityManager> ems) throws IOException {

	Collection<TransactionData> entityTransactions;
	TransactionManager.addCaller(transaction, handler);

	if (type.equals(TransactionAttributeType.NOT_SUPPORTED)) {
	    TransactionManager.addFreeEntityManagers(transaction, ems);
	} else if (type.equals(TransactionAttributeType.REQUIRED)) {

	    entityTransactions = getEntityTransactions(ems);
	    TransactionManager.addEntityTransactions(transaction,
		    entityTransactions);

	} else if (type.equals(TransactionAttributeType.REQUIRES_NEW)) {

	    entityTransactions = getEntityTransactions(ems);
	    TransactionManager.addReqNewTransactions(transaction,
		    entityTransactions);

	} else if (type.equals(TransactionAttributeType.MANDATORY)) {

	    int status = getStatus(transaction);
	    if (status == UserTransactionFactory.INACTIVE_TRANSACTION_STATE) {
		TransactionManager.addEntityManagers(transaction, ems);
		throw new EJBException(MANDATORY_ERROR);
	    } else {
		entityTransactions = getEntityTransactions(ems);
		TransactionManager.addEntityTransactions(transaction,
			entityTransactions);
	    }
	} else if (type.equals(TransactionAttributeType.NEVER)) {

	    try {
		int status = getStatus(transaction);
		if (status > UserTransactionFactory.INACTIVE_TRANSACTION_STATE) {
		    throw new EJBException(NEVER_ERROR);
		}
	    } finally {
		TransactionManager.addFreeEntityManagers(transaction, ems);
	    }
	} else if (type.equals(TransactionAttributeType.SUPPORTS)) {

	    entityTransactions = getEntityTransactions(ems);
	    TransactionManager.addEntityTransactions(transaction,
		    entityTransactions);
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
    public static TransactionAttributeType addTransaction(BeanHandler handler,
	    Method method, Collection<EntityManager> ems) throws IOException {

	TransactionAttributeType type;

	MetaData metaData = handler.getMetaData();
	type = getTransactionType(metaData, method);
	UserTransaction transaction = getTransaction();

	if (ObjectUtils.notNull(type)) {
	    addTransaction(handler, type, transaction, ems);
	} else {
	    TransactionManager.addEntityManagers(transaction, ems);
	}

	return type;
    }

    /**
     * Rollbacks passed {@link UserTransaction} by
     * {@link TransactionAttributeType} distinguishes only
     * {@link TransactionAttributeType#REQUIRES_NEW} case or uses standard
     * rollback for all other
     * 
     * @param type
     * @param handler
     * @throws IOException
     */
    private static void rollbackTransaction(TransactionAttributeType type,
	    BeanHandler handler) throws IOException {

	UserTransaction transaction = getTransaction();

	if (type.equals(TransactionAttributeType.REQUIRES_NEW)) {
	    TransactionManager.rollbackReqNew(transaction);
	} else if (TransactionManager.isFreeType(type)) {
	    TransactionManager.closeFreeEntityManagers(transaction);
	} else {
	    TransactionManager.rollback(transaction);
	}
    }

    /**
     * Decides which rollback method to call of {@link UserTransaction}
     * implementation by {@link TransactionAttribute} annotation
     * 
     * @param handler
     * @param method
     * @throws IOException
     */
    public static void rollbackTransaction(BeanHandler handler, Method method)
	    throws IOException {

	TransactionAttributeType type = getTransactionType(
		handler.getMetaData(), method);
	if (ObjectUtils.notNull(type)) {
	    rollbackTransaction(type, handler);
	} else {
	    closeEntityManagers();
	}
    }

    /**
     * Decides whether commit or not {@link UserTransaction} by
     * {@link TransactionAttribute} annotation
     * 
     * @param type
     * @param handler
     * @throws IOException
     */
    private static void commitTransaction(TransactionAttributeType type,
	    BeanHandler handler) throws IOException {

	UserTransaction transaction = getTransaction();

	if (TransactionManager.isTransactionalType(type)) {

	    boolean check = TransactionManager
		    .checkCaller(transaction, handler);
	    if (check) {
		TransactionManager.commit(transaction);
	    }
	} else if (type.equals(TransactionAttributeType.REQUIRES_NEW)) {
	    TransactionManager.commitReqNew(transaction);
	} else if (TransactionManager.isFreeType(type)) {
	    TransactionManager.closeFreeEntityManagers(transaction);
	} else {
	    TransactionManager.closeEntityManagers(transaction);
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
	if (ObjectUtils.notNull(type)) {
	    commitTransaction(type, handler);
	} else {
	    closeEntityManagers();
	}
    }

    /**
     * Closes cached {@link EntityManager}s after method call
     */
    public static void closeEntityManagers() {

	UserTransaction transaction = TransactionHolder.getTransaction();
	if (ObjectUtils.notNull(transaction)) {
	    TransactionManager.close(transaction);
	}
    }

    /**
     * Removes {@link UserTransaction} attribute from cache if passed
     * {@link BeanHandler} is first in EJB injection method chain
     * 
     * @param handler
     * @param type
     */
    private static void remove(BeanHandler handler,
	    TransactionAttributeType type) {

	UserTransaction transaction = TransactionHolder.getTransaction();

	if (type.equals(TransactionAttributeType.REQUIRES_NEW)) {
	    TransactionManager.remove(transaction);
	} else if (ObjectUtils.notNull(transaction)) {

	    boolean check = TransactionManager
		    .checkCaller(transaction, handler);
	    if (check) {
		TransactionManager.remove(transaction);
	    }
	} else {
	    TransactionHolder.removeTransaction();
	}
    }

    /**
     * Removes {@link UserTransaction} attribute from cache if
     * {@link TransactionAttributeType} is null or if passed {@link BeanHandler}
     * is first in EJB injection method chain
     * 
     * @param handler
     * @param method
     */
    public static void remove(BeanHandler handler, Method method) {

	TransactionAttributeType type = getTransactionType(
		handler.getMetaData(), method);
	if (ObjectUtils.notNull(type)) {
	    remove(handler, type);
	} else {
	    UserTransaction transaction = TransactionHolder.getTransaction();
	    if (ObjectUtils.notNull(transaction)) {
		TransactionManager.remove(transaction);
	    } else {
		TransactionHolder.removeTransaction();
	    }
	}
    }
}
