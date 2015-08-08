/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.cache;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManagerFactory;

/**
 * Container class for {@link EntityManagerFactory} with check if connection
 * configuration is in progress and user count
 *
 * @author Levan Tsinadze
 * @since 0.0.45
 * @see org.lightmare.deploy.BeanLoader#loadBean(org.lightmare.deploy.BeanLoader.BeanParameters)
 * @see org.lightmare.cache.ConnectionContainer#cacheSemaphore(String, String)
 * @see org.lightmare.cache.ConnectionContainer#getConnection(String)
 * @see org.lightmare.cache.ConnectionContainer#getEntityManagerFactory(String)
 * @see org.lightmare.cache.ConnectionContainer#getSemaphore(String)
 * @see org.lightmare.cache.ConnectionContainer#isInProgress(String)
 */
public class ConnectionSemaphore {

    // Flag if connection initialization is in progress
    private final AtomicBoolean inProgress = new AtomicBoolean();

    // Persistence unit name
    private String unitName;

    // JNDI name
    private String jndiName;

    // Checks if connection is already cached
    private boolean cached;

    // Check if connection is already bound to JNDI lookup
    private boolean bound;

    // EntityManagerFactory instance for appropriated persistence unit
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
