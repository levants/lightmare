package org.lightmare.jpa.jta;

import javax.persistence.EntityTransaction;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * User trasnaction for jndi and ejb beans
 * 
 * @author levan
 * 
 */
public class UserTransactionImpl implements UserTransaction {

    private EntityTransaction transaction;

    public UserTransactionImpl(EntityTransaction transaction) {

	this.transaction = transaction;
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {

	transaction.begin();
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException,
	    HeuristicRollbackException, SecurityException,
	    IllegalStateException, SystemException {

	transaction.commit();
    }

    @Override
    public int getStatus() throws SystemException {

	boolean isActive = transaction.isActive();
	return isActive ? 1 : 0;
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException,
	    SystemException {

	transaction.rollback();
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {

	transaction.setRollbackOnly();
    }

    @Override
    public void setTransactionTimeout(int arg0) throws SystemException {

	throw new UnsupportedOperationException(
		"Timeouts are not supported yet");
    }

}
