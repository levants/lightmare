/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.cache;

import javax.transaction.UserTransaction;

/**
 * Caches {@link javax.transaction.Transaction} objects for each method call in
 * {@link ThreadLocal} cache
 *
 * @author Levan Tsinadze
 * @since 0.0.81
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
     * Gets {@link UserTransaction} object from {@link ThreadLocal} cache
     *
     * @return {@link UserTransaction}
     */
    public static UserTransaction getTransaction() {
	UserTransaction transaction = TRANSACTION_HOLDER.get();
	return transaction;
    }

    /**
     * Caches {@link UserTransaction} object in {@link ThreadLocal} cache
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
