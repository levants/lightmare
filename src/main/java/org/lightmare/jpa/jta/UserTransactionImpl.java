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

	if (transactions.length > 0) {
	    this.transactions = new ArrayList<EntityTransaction>(
		    Arrays.asList(transactions));
	} else {
	    this.transactions = new ArrayList<EntityTransaction>();
	}
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {

	for (EntityTransaction transaction : transactions) {
	    transaction.begin();
	}
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SecurityException,
	    IllegalStateException, SystemException {

	for (EntityTransaction transaction : transactions) {
	    if (transaction.isActive()) {
		transaction.commit();
	    }
	}

	closeEntityManagers();
    }

    @Override
    public int getStatus() throws SystemException {

	int active = 0;
	for (EntityTransaction transaction : transactions) {
	    boolean isActive = transaction.isActive();
	    active += isActive ? 1 : 0;
	}

	if (requareNews != null && checkNews()) {
	    for (EntityTransaction transaction : requareNews) {
		boolean isActive = transaction.isActive();
		active += isActive ? 1 : 0;
	    }
	}

	return active;
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException,
	    SystemException {

	for (EntityTransaction transaction : transactions) {
	    transaction.rollback();
	}
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {

	for (EntityTransaction transaction : transactions) {
	    transaction.setRollbackOnly();
	}
    }

    @Override
    public void setTransactionTimeout(int arg0) throws SystemException {

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
     * Check if requareNews transactions stack is empty
     * 
     * @return <code>boolean</code>
     */
    private boolean checkNews() {

	boolean notEmpty = !getNews().isEmpty();

	return notEmpty;
    }

    private boolean checkNewEms() {

	boolean notEmpty = !getNewEms().isEmpty();

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

    public void pushReqNewEm(EntityManager em) {

	getNewEms().push(em);
    }

    /**
     * Commits new {@link EntityTransaction} at the end of
     * {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} annotated bean
     * methods
     */
    public void commitReqNew() {

	if (checkNews()) {
	    EntityTransaction entityTransaction = getNews().pop();
	    entityTransaction.commit();
	}
	closeReqNew();
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

    public void addEntityManager(EntityManager em) {

	if (ems == null) {
	    ems = new ArrayList<EntityManager>();
	}

	ems.add(em);
    }

    private void closeEntityManagers() {

	for (EntityManager em : ems) {
	    if (em.isOpen()) {
		em.close();
	    }
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

	boolean check = (caller != null);
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
