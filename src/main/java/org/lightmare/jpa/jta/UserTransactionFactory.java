package org.lightmare.jpa.jta;

import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

/**
 * Factory class to initialize and create {@link UserTransaction} instance
 * 
 * @author levan
 * 
 */
public abstract class UserTransactionFactory {

    public static UserTransaction get(EntityTransaction... transactions) {

	return new UserTransactionImpl(transactions);
    }

    protected static void join(UserTransaction transaction,
	    EntityTransaction... entityTransactions) {

    }
}
