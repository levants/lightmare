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

    @Override
    public void begin() throws NotSupportedException, SystemException {

	if (CollectionUtils.valid(transactions)) {
	    begin(transactions);
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

    /**
     * Rollbacks new {@link EntityTransaction} at the end of
     * {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} annotated bean
     * methods
     */
    public void rollbackReqNews() throws IllegalStateException,
	    SecurityException, SystemException {

	try {
	    if (checkNews()) {
		while (CollectionUtils.notEmpty(requareNews)) {
		    EntityTransaction entityTransaction = requareNews.pop();
		    rollback(entityTransaction);
		}
	    }
	} finally {
	    closeReqNew();
	}
    }

    /**
     * Rollbacks all cached {@link EntityTransaction} instances
     * 
     * @throws IllegalStateException
     * @throws SecurityException
     * @throws SystemException
     */
    private void rollbackAll() throws IllegalStateException, SecurityException,
	    SystemException {

	EntityTransaction transaction;
	while (CollectionUtils.notEmpty(transactions)) {
	    transaction = transactions.pop();
	    rollback(transaction);
	}
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException,
	    SystemException {

	try {
	    if (CollectionUtils.valid(transactions)) {
		rollbackAll();
	    }
	} finally {
	    closeEntityManagers();
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
     * Sets all cached {@link EntityTransaction} instances in rollbackOnly mode
     * 
     * @throws IllegalStateException
     * @throws SystemException
     */
    private void setRollbackOnlyAll() throws IllegalStateException,
	    SystemException {

	for (EntityTransaction transaction : transactions) {
	    setRollbackOnly(transaction);
	}
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {

	if (CollectionUtils.valid(transactions)) {
	    setRollbackOnlyAll();
	}
    }

    @Override
    public void setTransactionTimeout(int time) throws SystemException {

	throw new UnsupportedOperationException(TIMEOUT_NOT_SUPPORTED_ERROR);
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
     * Check if {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} type
     * transactions stack is empty
     * 
     * @return <code>boolean</code>
     */
    private boolean checkNews() {

	boolean notEmpty = CollectionUtils.valid(requareNews);

	return notEmpty;
    }

    /**
     * Check if {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} type
     * transactions referenced {@link EntityManager} stack is empty
     * 
     * @return <code>boolean</code>
     */
    private boolean checkNewEms() {

	boolean notEmpty = CollectionUtils.valid(requareNewEms);

	return notEmpty;
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

    /**
     * Closes all cached immediate {@link EntityManager} instances
     */
    private void closeReqNew() {

	close(requareNewEms);
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

    /**
     * Closes all cached {@link EntityManager} instances
     */
    private void closeAllEntityManagers() {

	EntityManager em;
	while (CollectionUtils.notEmpty(ems)) {
	    em = ems.pop();
	    JpaManager.closeEntityManager(em);
	}
    }

    /**
     * Closes all contained {@link EntityManager}s
     */
    public void closeEntityManagers() {

	if (CollectionUtils.valid(ems)) {
	    closeAllEntityManagers();
	}
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

    public void close() {

	closeEntityManagers();
	closeReqNew();
    }
}
