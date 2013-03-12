package org.lightmare.jpa.jta;

import java.util.Stack;

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

    private EntityTransaction[] transactions;

    private Stack<EntityTransaction> requareNews;

    public UserTransactionImpl(EntityTransaction... transactions) {

	this.transactions = transactions;
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
	    transaction.commit();
	}
    }

    @Override
    public int getStatus() throws SystemException {

	int active = 0;
	for (EntityTransaction transaction : transactions) {
	    boolean isActive = transaction.isActive();
	    active += isActive ? 1 : 0;
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

    /**
     * Check if requareNews transactions stack is empty
     * 
     * @return <code>boolean</code>
     */
    private boolean checkNews() {

	boolean notEmpty = !getNews().isEmpty();

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
     * Commits new {@link EntityTransaction} at the end of
     * {@link javax.ejb.TransactionAttributeType#REQUIRES_NEW} annotated bean
     * methods
     */
    public void commitReqNew() {

	if (checkNews()) {
	    EntityTransaction entityTransaction = getNews().pop();
	    entityTransaction.commit();
	}
    }

}
