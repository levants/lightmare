package org.lightmare.jpa.jta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.lightmare.ejb.handlers.BeanHandler;
import org.lightmare.jpa.JpaManager;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Implementation of {@link UserTransaction} interface for JNDI and EJB beans
 * 
 * @author levan
 * 
 */
public class UserTransactionImpl implements UserTransaction {

    // Caches EntityTransaction instances for immediate commit or join with
    // other transactions
    private Stack<EntityTransaction> transactions;

    // Caches EntityManager instances for clear up
    private Stack<EntityManager> ems;

    // Caches EntityTransaction instances for immediate commit
    private Stack<EntityTransaction> requareNews;

    // Caches EntityManager instances for immediate clean up
    private Stack<EntityManager> requareNewEms;

    private Stack<EntityManager> notSupportedEms;

    // Object which first called this (UserTransaction) instance
    private Object caller;

    // Denotes active transaction
    private static int ACTIVE = 1;

    // Denotes inactive transaction
    private static int INACTIVE = 0;

    private static final String TIMEOUT_NOT_SUPPORTED_ERROR = "Timeouts are not supported yet";

    protected UserTransactionImpl(EntityTransaction... transactions) {

	this.transactions = new Stack<EntityTransaction>();

	if (CollectionUtils.valid(transactions)) {
	    addTransactions(transactions);
	}
    }

    /**
     * Adds {@link EntityTransaction} to transactions {@link List} for further
     * processing
     * 
     * @param transaction
     */
    public void addTransaction(EntityTransaction transaction) {

	transactions.add(transaction);
    }

    /**
     * Adds {@link EntityTransaction}s to transactions {@link List} for further
     * processing
     * 
     * @param transactions
     */
    public void addTransactions(EntityTransaction... transactions) {

	Collections.addAll(this.transactions, transactions);
    }

    /**
     * Adds {@link EntityManager} to collection to close after transactions
     * processing
     * 
     * @param em
     */
    public void addEntityManager(EntityManager em) {

	if (ObjectUtils.notNull(em)) {
	    if (ems == null) {
		ems = new Stack<EntityManager>();
	    }

	    ems.push(em);
	}
    }

    /**
     * Adds {@link EntityManager}'s to collection to close after transactions
     * processing
     * 
     * @param em
     */
    public void addEntityManagers(Collection<EntityManager> ems) {

	if (CollectionUtils.valid(ems)) {
	    for (EntityManager em : ems) {
		addEntityManager(em);
	    }
	}
    }

    private Stack<EntityTransaction> getNews() {

	if (requareNews == null) {
	    requareNews = new Stack<EntityTransaction>();
	}

	return requareNews;
    }

    private Stack<EntityManager> getNewEms() {

	if (requareNewEms == null) {
	    requareNewEms = new Stack<EntityManager>();
	}

	return requareNewEms;
    }

    /**
     * Adds new {@link EntityTransaction} for
     * {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} annotated bean
     * methods
     * 
     * @param entityTransaction
     */
    public void pushReqNew(EntityTransaction entityTransaction) {

	getNews().push(entityTransaction);
    }

    /**
     * Adds {@link EntityManager} to collection to close after
     * {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} type transactions
     * processing
     * 
     * @param em
     */
    public void pushReqNewEm(EntityManager em) {

	getNewEms().push(em);
    }

    /**
     * Closes each of passed {@link EntityManager}s {@link Stack}
     * 
     * @param entityManagers
     */
    private void close(Stack<EntityManager> entityManagers) {

	if (CollectionUtils.valid(entityManagers)) {

	    EntityManager em;
	    while (CollectionUtils.notEmpty(entityManagers)) {
		em = entityManagers.pop();
		JpaManager.closeEntityManager(em);
	    }
	}
    }

    /**
     * Sets passed {@link EntityTransaction} in rollbackOnly mode
     * 
     * @param transaction
     * @throws IllegalStateException
     * @throws SystemException
     */
    private void setRollbackOnly(EntityTransaction transaction)
	    throws IllegalStateException, SystemException {

	if (transaction.isActive()) {
	    transaction.setRollbackOnly();
	}
    }

    /**
     * Sets each {@link EntityTransaction}'s from passed {@link Collection} in
     * rollbackOnly mode
     * 
     * @throws IllegalStateException
     * @throws SystemException
     */
    private void setRollbackOnly(
	    Collection<EntityTransaction> entityTransactions)
	    throws IllegalStateException, SystemException {

	if (CollectionUtils.valid(entityTransactions)) {
	    for (EntityTransaction entityTransaction : entityTransactions) {
		setRollbackOnly(entityTransaction);
	    }
	}
    }

    /**
     * Begins each of passed {@link EntityTransaction}s {@link Collection}
     * 
     * @param entityTransactions
     * @throws NotSupportedException
     * @throws SystemException
     */
    private void begin(Collection<EntityTransaction> entityTransactions)
	    throws NotSupportedException, SystemException {

	if (CollectionUtils.valid(entityTransactions))
	    for (EntityTransaction transaction : entityTransactions) {
		transaction.begin();
	    }
    }

    /**
     * Commits if passed transaction is active
     * 
     * @param transaction
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException
     * @throws SecurityException
     * @throws IllegalStateException
     * @throws SystemException
     */
    private void commit(EntityTransaction transaction)
	    throws RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SecurityException,
	    IllegalStateException, SystemException {

	if (transaction.isActive()) {
	    transaction.commit();
	}
    }

    /**
     * Commits all {@link UserTransaction} cache
     * 
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException
     * @throws SecurityException
     * @throws IllegalStateException
     * @throws SystemException
     */
    private void commit(Stack<EntityTransaction> entityTransactions)
	    throws SecurityException, IllegalStateException, RollbackException,
	    HeuristicMixedException, HeuristicRollbackException,
	    SystemException {

	if (CollectionUtils.valid(entityTransactions)) {

	    EntityTransaction entityTransaction;
	    while (CollectionUtils.notEmpty(entityTransactions)) {
		entityTransaction = entityTransactions.pop();
		commit(entityTransaction);
	    }
	}
    }

    /**
     * Rollbacks passed {@link EntityTransaction} if it is active
     * 
     * @param transaction
     */
    private void rollback(EntityTransaction transaction) {

	if (transaction.isActive()) {
	    transaction.rollback();
	}
    }

    /**
     * Rollbacks each of {@link EntityTransaction}s {@link Stack}
     * 
     * @param entityTransactions
     */
    private void rollback(Stack<EntityTransaction> entityTransactions) {

	if (CollectionUtils.valid(entityTransactions)) {
	    EntityTransaction entityTransaction;
	    while (CollectionUtils.notEmpty(entityTransactions)) {
		entityTransaction = entityTransactions.pop();
		rollback(entityTransaction);
	    }
	}
    }

    /**
     * Closes all cached immediate {@link EntityManager} instances
     */
    private void closeReqNew() {

	close(requareNewEms);
    }

    /**
     * Closes all contained {@link EntityManager}s
     */
    public void closeEntityManagers() {

	close(ems);
    }

    /**
     * Begins all require new transactions
     * 
     * @throws NotSupportedException
     * @throws SystemException
     */
    public void beginReqNews() throws NotSupportedException, SystemException {

	if (CollectionUtils.valid(requareNews)) {
	    begin(requareNews);
	}
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {

	if (CollectionUtils.valid(transactions)) {
	    begin(transactions);
	}
    }

    /**
     * Commits new {@link EntityTransaction} at the end of
     * {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} annotated bean
     * methods
     * 
     * @throws SystemException
     * @throws HeuristicRollbackException
     * @throws HeuristicMixedException
     * @throws RollbackException
     * @throws IllegalStateException
     * @throws SecurityException
     */
    public void commitReqNew() throws SecurityException, IllegalStateException,
	    RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SystemException {

	try {
	    commit(requareNews);
	} finally {
	    closeReqNew();
	}
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SecurityException,
	    IllegalStateException, SystemException {

	try {
	    if (CollectionUtils.valid(transactions)) {
		commit(transactions);
	    }
	} finally {
	    closeEntityManagers();
	}
    }

    /**
     * Rollbacks new {@link EntityTransaction} at the end of
     * {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} annotated bean
     * methods
     */
    public void rollbackReqNews() throws IllegalStateException,
	    SecurityException, SystemException {

	try {
	    rollback(requareNews);
	} finally {
	    closeReqNew();
	}
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException,
	    SystemException {

	try {
	    rollback(transactions);
	} finally {
	    closeEntityManagers();
	}
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {

	setRollbackOnly(transactions);
    }

    @Override
    public int getStatus() throws SystemException {

	int active = INACTIVE;

	if (CollectionUtils.valid(transactions)) {
	    for (EntityTransaction transaction : transactions) {
		boolean isActive = transaction.isActive();
		active += isActive ? ACTIVE : INACTIVE;
	    }
	}

	if (CollectionUtils.valid(requareNews)) {
	    for (EntityTransaction transaction : requareNews) {
		boolean isActive = transaction.isActive();
		active += isActive ? ACTIVE : INACTIVE;
	    }
	}

	return active;
    }

    @Override
    public void setTransactionTimeout(int time) throws SystemException {

	throw new UnsupportedOperationException(TIMEOUT_NOT_SUPPORTED_ERROR);
    }

    /**
     * Checks if this object was created by passed {@link BeanHandler} object
     * 
     * @param handler
     * @return <code>boolean</code>
     */
    public boolean checkCaller(BeanHandler handler) {

	boolean check = ObjectUtils.notNull(caller);

	if (check) {
	    check = caller.equals(handler.getBean());
	}

	return check;
    }

    public void setCaller(BeanHandler handler) {
	caller = handler.getBean();
    }

    public Object getCaller() {

	return caller;
    }

    /**
     * Closes all cached {@link EntityManager} isntances
     */
    public void close() {

	closeEntityManagers();
	closeReqNew();
    }
}
