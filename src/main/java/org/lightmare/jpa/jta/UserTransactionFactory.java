package org.lightmare.jpa.jta;

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

    private UserTransactionFactory() {
	throw new InstantiationError();
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
}
