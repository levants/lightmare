package org.lightmare.ejb.meta;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManagerFactory;

public class ConnectionSemaphore {

	private AtomicBoolean inProgress = new AtomicBoolean();

	private String unitName;

	private String jndiName;

	private boolean cached;

	private boolean bound;

	private EntityManagerFactory emf;

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
}
