package org.lightmare.jpa.jta;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

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

    protected static void addEntityTransaction(UserTransaction transaction,
	    EntityTransaction entityTransaction, EntityManager em) {

	if (ObjectUtils.notNull(entityTransaction)) {
	    transaction.addTransaction(entityTransaction);
	}
	if (ObjectUtils.notNull(em)) {
	    transaction.addEntityManager(em);
	}
    }

    protected static void addEntityTransactions(UserTransaction transaction,
	    Collection<TransactionData> entityTransactions) {

	if (CollectionUtils.valid(entityTransactions)) {
	    for (TransactionData transactionData : entityTransactions) {
		addEntityTransaction(transaction,
			transactionData.entityTransaction, transactionData.em);
	    }
	}
    }
}
