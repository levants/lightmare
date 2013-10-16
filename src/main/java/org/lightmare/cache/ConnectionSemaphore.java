package org.lightmare.cache;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManagerFactory;

/**
 * Container class for {@link EntityManagerFactory} with check if connection
 * configuration is in progress and user count
 * 
 * @author Levan
 * @since 0.0.45-SNAPSHOT
 */
public class ConnectionSemaphore {

    // Flag if connection initialization is in progress
    private AtomicBoolean inProgress = new AtomicBoolean();

    private String unitName;

    private String jndiName;

    private boolean cached;

    private boolean bound;

    private EntityManagerFactory emf;

    // Number of user using the same connection
    private final AtomicInteger users = new AtomicInteger();

    // Check if needs configure EntityManagerFactory
    private final AtomicBoolean check = new AtomicBoolean();

    // Default semaphore capacity
    public static final int MINIMAL_USERS = 1;

    public boolean isInProgress() {
	return inProgress.get();
    }

    public void setInProgress(boolean inProgress) {
	this.inProgress.getAndSet(inProgress);
    }

    public String getUnitName() {
	return unitName;
    }

    public void setUnitName(String unitName) {
	this.unitName = unitName;
    }

    public String getJndiName() {
	return jndiName;
    }

    public void setJndiName(String jndiName) {
	this.jndiName = jndiName;
    }

    public boolean isCached() {
	return cached;
    }

    public void setCached(boolean cached) {
	this.cached = cached;
    }

    public boolean isBound() {
	return bound;
    }

    public void setBound(boolean bound) {
	this.bound = bound;
    }

    public EntityManagerFactory getEmf() {
	return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
	this.emf = emf;
    }

    public int incrementUser() {
	return users.incrementAndGet();
    }

    public int decrementUser() {
	return users.decrementAndGet();
    }

    public int getUsers() {
	return users.get();
    }

    public boolean isCheck() {
	return check.getAndSet(Boolean.TRUE);
    }
}
