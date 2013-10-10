package org.lightmare.cache;

import javax.transaction.UserTransaction;

/**
 * Caches {@link javax.transaction.Transaction} objects for each method call in
 * {@link ThreadLocal} cache
 * 
 * @author levan
 * @since 0.0.81-SNAPSHOT
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
