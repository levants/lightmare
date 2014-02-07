/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.jpa.jta;

import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Factory class to initialize and create {@link UserTransaction} instance
 * 
 * @author Levan Tsinadze
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
