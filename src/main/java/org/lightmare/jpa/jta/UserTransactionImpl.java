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
 * {@link UserTransaction} implementation for JNDI and EJB beans
 * 
 * @author levan
 * 
 */
public class UserTransactionImpl implements UserTransaction {

    private Stack<EntityTransaction> transactions;

    // Caches EntityManager instances for clear up
    private Stack<EntityManager> ems;

    // Caches EntityTransaction instances
    private Stack<EntityTransaction> requareNews;

    private Stack<EntityManager> requareNewEms;

    private Object caller;

    // Denotes active transaction
    private static int ACTIVE = 1;

    // Denotes inactive transaction
    private static int INACTIVE = 0;

    public UserTransactionImpl(EntityTransaction... transactions) {

	this.transactions = new Stack<EntityTransaction>();

	if (CollectionUtils.valid(transactions)) {
	    addTransactions(transactions);
	}
    }

    private void beginAll() throws NotSupportedException, SystemException {

	for (EntityTransaction transaction : transactions) {
	    transaction.begin();
	}
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {

	if (CollectionUtils.valid(transactions)) {
	    beginAll();
	}
    }

    private void commit(EntityTransaction transaction)
	    throws RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SecurityException,
	    IllegalStateException, SystemException {

	if (transaction.isActive()) {
	    transaction.commit();
	}
    }

    private void commitAll() throws RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SecurityException,
	    IllegalStateException, SystemException {

	EntityTransaction transaction;
	while (CollectionUtils.notEmpty(transactions)) {
	    transaction = transactions.pop();
	    commit(transaction);
	}
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SecurityException,
	    IllegalStateException, SystemException {

	try {
	    if (CollectionUtils.valid(transactions)) {
		commitAll();
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

    private void rollback(EntityTransaction transaction) {

	if (transaction.isActive()) {
	    transaction.rollback();
	}
    }

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

    private void setRollbackOnly(EntityTransaction transaction)
	    throws IllegalStateException, SystemException {

	if (transaction.isActive()) {
	    transaction.setRollbackOnly();
	}
    }

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

	throw new UnsupportedOperationException(
		"Timeouts are not supported yet");
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
	    if (checkNews()) {
		EntityTransaction entityTransaction = getNews().pop();
		commit(entityTransaction);
	    }
	} finally {
	    closeReqNew();
	}
    }

    private void closeReqNew() {

	if (checkNewEms()) {
	    EntityManager em = getNewEms().pop();
	    JpaManager.closeEntityManager(em);
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

    private void closeEntityManager(EntityManager em) {

	JpaManager.closeEntityManager(em);
    }

    private void closeAllEntityManagers() {

	EntityManager em;
	while (CollectionUtils.notEmpty(ems)) {
	    em = ems.pop();
	    closeEntityManager(em);
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
}
