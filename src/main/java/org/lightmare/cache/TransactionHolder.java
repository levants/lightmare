package org.lightmare.cache;

import javax.transaction.UserTransaction;

/**
 * Caches {@link javax.transaction.Transaction} objects for each method call in
 * {@link ThreadLocal} cache
 * 
 * @author Levan Tsinadze
 * @since 0.0.81-SNAPSHOT
 * @see org.lightmare.jpa.jta.UserTransactionImpl
 * @see org.lightmare.jpa.jta.BeanTransactions#getTransaction(java.util.Collection)
 * @see org.lightmare.jpa.jta.BeanTransactions#getTransaction(javax.persistence.EntityTransaction...)
 * @see org.lightmare.jpa.jta.BeanTransactions#remove(org.lightmare.ejb.handlers.BeanHandler,
 *      java.lang.reflect.Method)
 */
public class TransactionHolder {

    // Caches UserTransaction instance per thread
    private static final ThreadLocal<UserTransaction> TRANSACTION_HOLDER = new ThreadLocal<UserTransaction>();

    /**
     * Gets {@link UserTransaction} object from {@link ThreadLocal} per thread
     * 
     * @return {@link UserTransaction}
     */
    public static UserTransaction getTransaction() {

	UserTransaction transaction = TRANSACTION_HOLDER.get();

	return transaction;
    }

    /**
     * Caches {@link UserTransaction} object in {@link ThreadLocal} per thread
     * 
     * @param transaction
     */
    public static void setTransaction(UserTransaction transaction) {
	TRANSACTION_HOLDER.set(transaction);
    }

    /**
     * Removes {@link UserTransaction} object from {@link ThreadLocal} per
     * thread
     */
    public static void removeTransaction() {
	TRANSACTION_HOLDER.remove();
    }
}
