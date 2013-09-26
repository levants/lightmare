package org.lightmare.jpa.jta;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Class to manager {@link UserTransaction} creation and closure
 * 
 * @author levan
 * 
 */
public class TransactionManager {

    // Error messages
    private static final String ISNANTIATING_ERROR = "Class TransactionManager can not be instntiate";

    private TransactionManager() {
	throw new InstantiationError(ISNANTIATING_ERROR);
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

	if (userTransaction instanceof UserTransactionImpl
		&& CollectionUtils.valid(entityTransactions)) {

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

	if (userTransaction instanceof UserTransactionImpl
		&& ObjectUtils.notNull(em)) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);
	    transaction.addEntityManager(em);
	}
    }

    private static void addEntityManagers(UserTransaction userTransaction,
	    Collection<EntityManager> ems) {

	if (userTransaction instanceof UserTransactionImpl
		&& CollectionUtils.valid(ems)) {

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

	if (userTransaction instanceof UserTransactionImpl
		&& CollectionUtils.valid(entityTransactions)) {

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

	if (userTransaction instanceof UserTransactionImpl) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);

	    Object caller = transaction.getCaller();
	    if (caller == null) {
		transaction.setCaller(handler);
	    }
	}
    }
}
