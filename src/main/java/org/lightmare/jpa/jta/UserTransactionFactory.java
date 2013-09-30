package org.lightmare.jpa.jta;

import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Factory class to initialize and create {@link UserTransaction} instance
 * 
 * @author levan
 * @since 0.0.82-SNAPSHOT
 */
public abstract class UserTransactionFactory {

    // Inactive state of user transaction
    public static final int INACTIVE_TRANSACTION_STATE = 0;

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
    protected static void join(UserTransaction userTransaction,
	    EntityTransaction... entityTransactions) {

	if (userTransaction instanceof UserTransactionImpl
		&& CollectionUtils.valid(entityTransactions)) {

	    UserTransactionImpl transaction = ObjectUtils.cast(userTransaction,
		    UserTransactionImpl.class);
	    transaction.addTransactions(entityTransactions);
	}
    }
}
