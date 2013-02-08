package org.lightmare.ejb.meta;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManagerFactory;

public class ConnectionSemaphore {

	private AtomicBoolean inProgress = new AtomicBoolean();

	private String name;

	private EntityManagerFactory emf;

	public boolean isInProgress() {
		return inProgress.get();
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress.getAndSet(inProgress);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EntityManagerFactory getEmf() {
		return emf;
	}

	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}
}
