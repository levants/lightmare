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
package org.lightmare.jpa.jta;

import java.io.IOException;
import java.util.Collection;

import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.lightmare.cache.TransactionHolder;
import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Class to manager {@link UserTransaction} creation and closure
 *
 * @author Levan Tsinadze
 * @since 0.0.82-SNAPSHOT
 */
public class TransactionManager {

    // Error messages
    private static final String ISNANTIATING_ERROR = "Class TransactionManager can not be instntiate";

    private TransactionManager() {
        throw new InstantiationError(ISNANTIATING_ERROR);
    }

    /**
     * Checks if passed {@link TransactionAttributeType} is transaction scoped
     *
     * @param type
     * @return <code>boolean</code>
     */
    protected static boolean isTransactionalType(TransactionAttributeType type) {
        return type.equals(TransactionAttributeType.REQUIRED) || type.equals(TransactionAttributeType.MANDATORY)
                || type.equals(TransactionAttributeType.SUPPORTS);
    }

    /**
     * Checks if passed {@link TransactionAttributeType} is out of transaction
     * scope
     *
     * @param type
     * @return <code>boolean</code>
     */
    protected static boolean isFreeType(TransactionAttributeType type) {
        return type.equals(TransactionAttributeType.NOT_SUPPORTED) || type.equals(TransactionAttributeType.NEVER);
    }

    /**
     * Adds new {@link EntityTransaction} to existed transactions
     *
     * @param entityTransaction
     * @param transaction
     */
    private static void addTransaction(EntityTransaction entityTransaction, UserTransactionImpl transaction) {

        if (ObjectUtils.notNull(entityTransaction)) {
            transaction.addTransaction(entityTransaction);
        }
    }

    /**
     * Adds {@link EntityManager} to passed transaction
     *
     * @param em
     * @param transaction
     */
    private static void addEntityManager(EntityManager em, UserTransactionImpl transaction) {

        if (ObjectUtils.notNull(em)) {
            transaction.addEntityManager(em);
        }
    }

    /**
     * Adds {@link EntityTransaction} to passed {@link UserTransaction} instance
     *
     * @param userTransaction
     * @param entityTransaction
     * @param em
     */
    protected static void addEntityTransaction(UserTransaction userTransaction, EntityTransaction entityTransaction,
            EntityManager em) {

        if (userTransaction instanceof UserTransactionImpl) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            addTransaction(entityTransaction, transaction);
            addEntityManager(em, transaction);
        }
    }

    /**
     * Adds {@link EntityTransaction} for each
     * {@link BeanTransactions.TransactionData} to passed
     * {@link UserTransaction} instance
     *
     * @param userTransaction
     * @param entityTransactions
     */
    protected static void addEntityTransactions(UserTransaction userTransaction,
            Collection<BeanTransactions.TransactionData> entityTransactions) {

        if (userTransaction instanceof UserTransactionImpl && CollectionUtils.valid(entityTransactions)) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            for (BeanTransactions.TransactionData transactionData : entityTransactions) {
                addEntityTransaction(transaction, transactionData.entityTransaction, transactionData.em);
            }
        }
    }

    /**
     * Adds {@link EntityManager} to passed {@link UserTransaction} instance
     *
     * @param userTransaction
     * @param em
     */
    protected static void addEntityManager(UserTransaction userTransaction, EntityManager em) {

        if (userTransaction instanceof UserTransactionImpl && ObjectUtils.notNull(em)) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            transaction.addEntityManager(em);
        }
    }

    /**
     * Adds each {@link EntityManager} from {@link Collection} to passed
     * {@link UserTransaction} instance
     *
     * @param userTransaction
     * @param ems
     */
    protected static void addEntityManagers(UserTransaction userTransaction, Collection<EntityManager> ems) {

        if (userTransaction instanceof UserTransactionImpl && CollectionUtils.valid(ems)) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            for (EntityManager em : ems) {
                addEntityManager(transaction, em);
            }
        }
    }

    /**
     * Adds {@link EntityManager}s without transaction scope
     *
     * @param userTransaction
     * @param ems
     */
    protected static void addFreeEntityManagers(UserTransaction userTransaction, Collection<EntityManager> ems) {

        if (userTransaction instanceof UserTransactionImpl && CollectionUtils.valid(ems)) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            for (EntityManager em : ems) {
                transaction.pushFreeEntityManager(em);
            }
        }

    }

    /**
     * Checks on null and adds {@link EntityManager} to transaction
     *
     * @param transaction
     * @param em
     */
    private static void checkAndAddEntityManager(UserTransactionImpl transaction, EntityManager em) {

        if (ObjectUtils.notNull(em)) {
            transaction.pushReqNewEm(em);
        }
    }

    /**
     * Checks on null and adds {@link UserTransactionImpl} to passed
     * {@link EntityTransaction} instance
     *
     * @param transaction
     * @param entityTransaction
     */
    private static void checkAndAddTransactiuon(UserTransactionImpl transaction, EntityTransaction entityTransaction) {

        if (ObjectUtils.notNull(entityTransaction)) {
            transaction.pushReqNew(entityTransaction);
        }
    }

    /**
     * Adds {@link EntityTransaction} to requires new stack in passed
     * {@link UserTransaction} instance
     *
     * @param userTransaction
     * @param entityTransaction
     * @param em
     */
    private static void addReqNewTransaction(UserTransactionImpl transaction, EntityTransaction entityTransaction,
            EntityManager em) {
        checkAndAddTransactiuon(transaction, entityTransaction);
        checkAndAddEntityManager(transaction, em);
    }

    /**
     * Adds {@link EntityTransaction} for each
     * {@link BeanTransactions.TransactionData} to requires new stack in passed
     * {@link UserTransaction} instance
     *
     * @param userTransaction
     * @param entityTransactions
     */
    protected static void addReqNewTransactions(UserTransaction userTransaction,
            Collection<BeanTransactions.TransactionData> entityTransactions) {

        if (userTransaction instanceof UserTransactionImpl && CollectionUtils.valid(entityTransactions)) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            for (BeanTransactions.TransactionData transactionData : entityTransactions) {
                addReqNewTransaction(transaction, transactionData.entityTransaction, transactionData.em);
            }
        }
    }

    /**
     * Adds caller to passed {@link UserTransaction} instance
     *
     * @param userTransaction
     * @param handler
     */
    protected static void addCaller(UserTransaction userTransaction, BeanHandler handler) {

        if (userTransaction instanceof UserTransactionImpl) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            Object caller = transaction.getCaller();
            if (caller == null) {
                transaction.setCaller(handler);
            }
        }
    }

    /**
     * Checks if passed {@link BeanHandler} is first caller / beginner of passed
     * {@link UserTransaction} instance
     *
     * @param userTransaction
     * @param handler
     * @return <code>boolean</code>
     */
    protected static boolean checkCaller(UserTransaction userTransaction, BeanHandler handler) {

        boolean check = (userTransaction instanceof UserTransactionImpl);

        if (check) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            check = transaction.checkCaller(handler);
        }

        return check;
    }

    /**
     * Commits passed {@link UserTransaction} with {@link IOException} throw
     *
     * @param transaction
     * @throws IOException
     */
    protected static void commit(UserTransaction transaction) throws IOException {

        try {
            transaction.commit();
        } catch (SecurityException ex) {
            throw new IOException(ex);
        } catch (IllegalStateException ex) {
            throw new IOException(ex);
        } catch (RollbackException ex) {
            throw new IOException(ex);
        } catch (HeuristicMixedException ex) {
            throw new IOException(ex);
        } catch (HeuristicRollbackException ex) {
            throw new IOException(ex);
        } catch (SystemException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Commits all {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW}
     * transactions from requires new stack in passed {@link UserTransaction}
     * with {@link IOException} throw
     *
     * @param transaction
     * @throws IOException
     */
    protected static void commitReqNew(UserTransaction userTransaction) throws IOException {

        if (userTransaction instanceof UserTransactionImpl) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            try {
                transaction.commitReqNew();
            } catch (SecurityException ex) {
                throw new IOException(ex);
            } catch (IllegalStateException ex) {
                throw new IOException(ex);
            } catch (RollbackException ex) {
                throw new IOException(ex);
            } catch (HeuristicMixedException ex) {
                throw new IOException(ex);
            } catch (HeuristicRollbackException ex) {
                throw new IOException(ex);
            } catch (SystemException ex) {
                throw new IOException(ex);
            }
        }
    }

    /**
     * Rollbacks passed {@link UserTransaction} with {@link IOException} throw
     *
     * @param transaction
     * @throws IOException
     */
    protected static void rollback(UserTransaction transaction) throws IOException {

        try {
            transaction.rollback();
        } catch (IllegalStateException ex) {
            throw new IOException(ex);
        } catch (SecurityException ex) {
            throw new IOException(ex);
        } catch (SystemException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Rollbacks all {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW}
     * transactions from requires new stack in method of passed
     * {@link UserTransaction} with {@link IOException} throw
     *
     * @param transaction
     * @throws IOException
     */
    protected static void rollbackReqNew(UserTransaction userTransaction) throws IOException {

        if (userTransaction instanceof UserTransactionImpl) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            try {
                transaction.rollbackReqNews();
            } catch (IllegalStateException ex) {
                throw new IOException(ex);
            } catch (SecurityException ex) {
                throw new IOException(ex);
            } catch (SystemException ex) {
                throw new IOException(ex);
            }
        }
    }

    protected static void closeReqNewEntityManagers(UserTransaction userTransaction) {

        if (userTransaction instanceof UserTransactionImpl) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            transaction.closeReqNew();
        }
    }

    /**
     * Closes all cached {@link EntityManager}s from stack in passed
     * {@link UserTransaction} instance
     *
     * @param userTransaction
     */
    protected static void closeEntityManagers(UserTransaction userTransaction) {

        if (userTransaction instanceof UserTransactionImpl) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            transaction.closeEntityManagers();
        }
    }

    /**
     * Closes all cached {@link EntityManager}s which are not in transaction
     * scope
     *
     * @param userTransaction
     */
    protected static void closeFreeEntityManagers(UserTransaction userTransaction) {

        if (userTransaction instanceof UserTransactionImpl) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            transaction.closeFreeEntityManagers();
        }
    }

    /**
     * Closes all cached {@link EntityManager} instances
     *
     * @param userTransaction
     */
    protected static void close(UserTransaction userTransaction) {

        if (userTransaction instanceof UserTransactionImpl) {
            UserTransactionImpl transaction = ObjectUtils.cast(userTransaction, UserTransactionImpl.class);
            transaction.close();
        }
    }

    /**
     * Removes transaction from {@link Thread} cache
     *
     * @param transaction
     */
    protected static void remove(UserTransaction transaction) {

        try {
            close(transaction);
        } finally {
            TransactionHolder.removeTransaction();
        }
    }
}
