package org.lightmare.jpa.jta;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Factory class to initialize and create {@link UserTransaction} instance
 * 
 * @author levan
 * 
 */
public abstract class UserTransactionFactory {

    // Error messages
    private static final String ISNANTIATING_ERROR = "Class UserTransactionFactory can not be instntiate";

    private UserTransactionFactory() {
	throw new InstantiationError(ISNANTIATING_ERROR);
    }

    public static UserTransaction get(EntityTransaction... transactions) {

	return new UserTransactionImpl(transactions);
    }

    /**
     * Joins passed {@link EntityTransaction} array to associated
     * {@link UserTransaction} instance
     * 
     * @param transaction
     * @param entityTransactions
     */
    protected static void join(UserTransaction transaction,
	    EntityTransaction... entityTransactions) {

	if (transaction instanceof UserTransactionImpl
		&& CollectionUtils.valid(entityTransactions)) {

	    UserTransactionImpl userTransactionImpl = ObjectUtils.cast(
		    transaction, UserTransactionImpl.class);
	    userTransactionImpl.addTransactions(entityTransactions);
	}
    }

    protected static void addEntityTransaction(UserTransaction userTransaction,
	    EntityTransaction entityTransaction, EntityManager em) {

	if (userTransaction instanceof UserTransactionImpl) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);

	    if (ObjectUtils.notNull(entityTransaction)) {
		transaction.addTransaction(entityTransaction);
	    }

	    if (ObjectUtils.notNull(em)) {
		transaction.addEntityManager(em);
	    }

	}
    }

    protected static void addEntityTransactions(
	    UserTransaction userTransaction,
	    Collection<BeanTransactions.TransactionData> entityTransactions) {

	if (CollectionUtils.valid(entityTransactions)
		&& userTransaction instanceof UserTransactionImpl) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);

	    for (BeanTransactions.TransactionData transactionData : entityTransactions) {
		addEntityTransaction(transaction,
			transactionData.entityTransaction, transactionData.em);
	    }
	}
    }

    protected static void addEntityManager(UserTransaction userTransaction,
	    EntityManager em) {

	if (ObjectUtils.notNull(em)
		&& userTransaction instanceof UserTransactionImpl) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);
	    transaction.addEntityManager(em);
	}
    }

    private static void addEntityManagers(UserTransaction userTransaction,
	    Collection<EntityManager> ems) {

	if (CollectionUtils.valid(ems)
		&& userTransaction instanceof UserTransactionImpl) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);
	    for (EntityManager em : ems) {
		addEntityManager(transaction, em);
	    }
	}
    }

    private static void addReqNewTransaction(UserTransaction userTransaction,
	    EntityTransaction entityTransaction, EntityManager em) {

	if (userTransaction instanceof UserTransactionImpl) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);

	    if (ObjectUtils.notNull(entityTransaction)) {
		transaction.pushReqNew(entityTransaction);
	    }

	    if (ObjectUtils.notNull(em)) {
		transaction.pushReqNewEm(em);
	    }
	}
    }

    private static void addReqNewTransactions(UserTransaction userTransaction,
	    Collection<BeanTransactions.TransactionData> entityTransactions) {

	if (CollectionUtils.valid(entityTransactions)
		&& userTransaction instanceof UserTransactionImpl) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);

	    for (BeanTransactions.TransactionData transactionData : entityTransactions) {
		addReqNewTransaction(transaction,
			transactionData.entityTransaction, transactionData.em);
	    }
	}
    }

    private static void addCaller(UserTransaction userTransaction,
	    BeanHandler handler) {

	Object caller = transaction.getCaller();
	if (caller == null) {
	    transaction.setCaller(handler);
	}
    }
}
