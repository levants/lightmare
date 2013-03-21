package org.lightmare.jpa.jta;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.lightmare.utils.ObjectUtils;

/**
 * {@link UserTransaction} implementation for jndi and ejb beans
 * 
 * @author levan
 * 
 */
public class UserTransactionImpl implements UserTransaction {

    private List<EntityTransaction> transactions;

    private List<EntityManager> ems;

    private Stack<EntityTransaction> requareNews;

    private Stack<EntityManager> requareNewEms;

    private InvocationHandler caller;

    public UserTransactionImpl(EntityTransaction... transactions) {

	if (ObjectUtils.available(transactions)) {
	    this.transactions = new ArrayList<EntityTransaction>(
		    Arrays.asList(transactions));
	} else {
	    this.transactions = new ArrayList<EntityTransaction>();
	}
    }

    private void beginAll() throws NotSupportedException, SystemException {

	for (EntityTransaction transaction : transactions) {
	    transaction.begin();
	}
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {

	if (ObjectUtils.available(transactions)) {
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

	for (EntityTransaction transaction : transactions) {
	    commit(transaction);
	}
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SecurityException,
	    IllegalStateException, SystemException {

	try {
	    if (ObjectUtils.available(transactions)) {
		commitAll();
	    }
	} finally {
	    closeEntityManagers();
	}
    }

    @Override
    public int getStatus() throws SystemException {

	int active = 0;
	if (ObjectUtils.available(transactions)) {
	    for (EntityTransaction transaction : transactions) {
		boolean isActive = transaction.isActive();
		active += isActive ? 1 : 0;
	    }
	}

	if (ObjectUtils.available(requareNews)) {
	    for (EntityTransaction transaction : requareNews) {
		boolean isActive = transaction.isActive();
		active += isActive ? 1 : 0;
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

	for (EntityTransaction transaction : transactions) {
	    rollback(transaction);
	}
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException,
	    SystemException {

	try {
	    if (ObjectUtils.available(transactions)) {
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

	if (ObjectUtils.available(transactions)) {
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

	boolean notEmpty = ObjectUtils.available(requareNews);

	return notEmpty;
    }

    /**
     * Check if {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} type
     * transactions referenced {@link EntityManager} stack is empty
     * 
     * @return <code>boolean</code>
     */
    private boolean checkNewEms() {

	boolean notEmpty = ObjectUtils.available(requareNewEms);

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
	    if (em.isOpen()) {
		em.close();
	    }
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
		ems = new ArrayList<EntityManager>();
	    }

	    ems.add(em);
	}
    }

    private void closeEntityManager(EntityManager em) {

	if (em.isOpen()) {
	    em.close();
	}
    }

    private void closeAllEntityManagers() {

	for (EntityManager em : ems) {
	    closeEntityManager(em);
	}
    }

    /**
     * Closes all contained {@link EntityManager}s
     */
    public void closeEntityManagers() {

	if (ObjectUtils.available(ems)) {
	    closeAllEntityManagers();
	}
    }

    /**
     * Checks if this object was created by passed {@link InvocationHandler}
     * object
     * 
     * @param handler
     * @return <code>boolean</code>
     */
    public boolean checkCaller(InvocationHandler handler) {

	boolean check = ObjectUtils.notNull(caller);
	if (check) {
	    check = caller.equals(handler);
	}

	return check;
    }

    public void setCaller(InvocationHandler handler) {
	caller = handler;
    }

    public InvocationHandler getCaller() {

	return caller;
    }
}
