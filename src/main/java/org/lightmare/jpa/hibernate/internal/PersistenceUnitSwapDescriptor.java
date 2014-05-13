/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2014, Levan Tsinadze, or third-party contributors as
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
package org.lightmare.jpa.hibernate.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.lightmare.utils.ObjectUtils;

/**
 * Extension of {@link PersistenceUnitInfoDescriptor} with data source
 * transaction type and managed classes properties modification capabilities
 * 
 * @author Levan Tsinadze
 * @since 0.1.2
 */
public class PersistenceUnitSwapDescriptor extends
	PersistenceUnitInfoDescriptor implements PersistenceUnitDescriptor {

    // Editable JPA configuration parameters
    private PersistenceUnitTransactionType transactionType;

    private DataSource jtaDataSource;

    private DataSource nonJtaDataSource;

    private List<String> managedClassNames;

    /**
     * Constructor with {@link PersistenceUnitInfoDescriptor} to set editable
     * properties
     * 
     * @param persistenceUnitInfo
     */
    public PersistenceUnitSwapDescriptor(PersistenceUnitInfo persistenceUnitInfo) {
	super(persistenceUnitInfo);
	this.nonJtaDataSource = persistenceUnitInfo.getNonJtaDataSource();
	this.jtaDataSource = persistenceUnitInfo.getJtaDataSource();
	this.managedClassNames = persistenceUnitInfo.getManagedClassNames();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
	return transactionType;
    }

    public void setTransactionType(
	    PersistenceUnitTransactionType transactionType) {
	this.transactionType = transactionType;
    }

    @Override
    public DataSource getJtaDataSource() {
	return jtaDataSource;
    }

    public void setJtaDataSource(DataSource jtaDataSource) {
	this.jtaDataSource = jtaDataSource;
    }

    @Override
    public DataSource getNonJtaDataSource() {
	return nonJtaDataSource;
    }

    public void setNonJtaDataSource(Object nonJtaDataSource) {

	if (nonJtaDataSource == null) {
	    this.nonJtaDataSource = null;
	} else if (nonJtaDataSource instanceof DataSource) {
	    this.nonJtaDataSource = ObjectUtils.cast(nonJtaDataSource,
		    DataSource.class);
	}
    }

    @Override
    public List<String> getManagedClassNames() {
	return managedClassNames;
    }

    public void setManagedClassNames(List<String> managedClassNames) {
	this.managedClassNames = managedClassNames;
    }

    public void addClasses(Collection<String> classes) {

	if (managedClassNames == null) {
	    managedClassNames = new ArrayList<String>();
	}
	managedClassNames.addAll(classes);
    }
}
