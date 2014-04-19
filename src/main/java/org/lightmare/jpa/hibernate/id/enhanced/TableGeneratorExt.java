/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.jpa.hibernate.id.enhanced;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;
import org.lightmare.utils.ObjectUtils;

/**
 * To generate primary keys from table and bypasses existing keys
 * 
 * @author Levan Tsinadze
 * @see TableGenerator
 */
public class TableGeneratorExt extends TableGenerator {

    private String selectQuery;

    private String updateQuery;

    private String insertQuery;

    private String segmentValue;

    private Type identifierType;

    private String tableName;

    private Optimizer optimizer;

    // To use in annotations and avoid errors
    public static final String STRATEGY = "org.lightmare.jpa.hibernate.id.enhanced.TableGeneratorExt";

    // Column numbers
    private static final int FIRST_COLUMN = 1;

    private static final int SECOND_COLUMN = 2;

    private static final int THIRD_COLUMN = 3;

    private static final int ZERO_ROWS = 0;

    private static final CoreMessageLogger LOG = Logger.getMessageLogger(
	    CoreMessageLogger.class, TableGeneratorExt.class.getName());

    /**
     * Implementation for {@link AbstractReturningWork} for
     * {@link IntegralDataTypeHolder} for specific identifier generator
     * 
     * @author Levan Tsinadze
     * @since 0.1.0
     */
    protected class ReturningWork extends
	    AbstractReturningWork<IntegralDataTypeHolder> {

	private final SqlStatementLogger statementLogger;

	private final long currentValue;

	public ReturningWork(SqlStatementLogger statementLogger,
		long currentValue) {
	    this.statementLogger = statementLogger;
	    this.currentValue = currentValue;
	}

	private void close(Statement closeable) throws SQLException {

	    if (ObjectUtils.notNull(closeable)) {
		closeable.close();
	    }
	}

	/**
	 * Makes insert of last value of identifier
	 * 
	 * @param connection
	 * @param value
	 * @throws SQLException
	 */
	private void insert(Connection connection, IntegralDataTypeHolder value)
		throws SQLException {

	    PreparedStatement insertPS = null;
	    try {
		statementLogger.logStatement(insertQuery,
			FormatStyle.BASIC.getFormatter());
		insertPS = connection.prepareStatement(insertQuery);
		insertPS.setString(FIRST_COLUMN, segmentValue);
		value.bind(insertPS, SECOND_COLUMN);
		insertPS.execute();
	    } finally {
		close(insertPS);
	    }
	}

	private void onSelect(Connection connection,
		IntegralDataTypeHolder value) throws SQLException {

	    PreparedStatement selectPS = connection
		    .prepareStatement(selectQuery);
	    try {
		selectPS.setString(FIRST_COLUMN, segmentValue);
		ResultSet selectRS = selectPS.executeQuery();
		if (selectRS.next()) {
		    value.initialize(selectRS, FIRST_COLUMN);
		} else {
		    value.initialize(currentValue);
		    insert(connection, value);
		}
		selectRS.close();
	    } catch (SQLException ex) {
		LOG.unableToReadOrInitHiValue(ex);
		throw ex;
	    } finally {
		selectPS.close();
	    }
	}

	private int onUpdate(Connection connection, IntegralDataTypeHolder value)
		throws SQLException {

	    int rows;

	    PreparedStatement updatePS = connection
		    .prepareStatement(updateQuery);
	    try {
		final IntegralDataTypeHolder updateValue = value.copy()
			.initialize(currentValue);
		// TODO check for incrementSize
		// increment options
		updateValue.increment();

		// gets existing value and
		// incremented current values as
		// long types to compare
		Long existing = value.copy().makeValue().longValue();
		Long current = updateValue.copy().makeValue().longValue();
		// checks if incremented current
		// value is less then value and
		// puts incremented value
		// instead of incremented
		// current value for
		// update
		if (existing > current) {
		    updateValue.initialize(existing).increment();
		}
		updateValue.bind(updatePS, FIRST_COLUMN);
		value.bind(updatePS, SECOND_COLUMN);
		updatePS.setString(THIRD_COLUMN, segmentValue);
		rows = updatePS.executeUpdate();
		value.initialize(currentValue);
	    } catch (SQLException ex) {
		LOG.unableToUpdateQueryHiValue(tableName, ex);
		throw ex;
	    } finally {
		updatePS.close();
	    }

	    return rows;
	}

	@Override
	public IntegralDataTypeHolder execute(Connection connection)
		throws SQLException {
	    IntegralDataTypeHolder value = IdentifierGeneratorHelper
		    .getIntegralDataTypeHolder(identifierType
			    .getReturnedClass());
	    int rows;
	    do {
		statementLogger.logStatement(selectQuery,
			FormatStyle.BASIC.getFormatter());
		onSelect(connection, value);
		statementLogger.logStatement(updateQuery,
			FormatStyle.BASIC.getFormatter());
		rows = onUpdate(connection, value);
	    } while (rows == ZERO_ROWS);

	    return value;
	}
    }

    /**
     * Implementation of {@link AccessCallback} for specific identifier
     * generator
     * 
     * @author Levan Tsinadze
     * 
     */
    protected class AccessCallbackImpl implements AccessCallback {

	private final SessionImplementor session;

	private final long currentValue;

	private final SqlStatementLogger statementLogger;

	public AccessCallbackImpl(SessionImplementor session,
		long currentValue, SqlStatementLogger statementLogger) {
	    this.session = session;
	    this.currentValue = currentValue;
	    this.statementLogger = statementLogger;
	}

	@Override
	public IntegralDataTypeHolder getNextValue() {
	    ReturningWork returnWork = new ReturningWork(statementLogger,
		    currentValue);
	    return session.getTransactionCoordinator().getTransaction()
		    .createIsolationDelegate()
		    .delegateWork(returnWork, Boolean.TRUE);
	}

	@Override
	public String getTenantIdentifier() {
	    return session.getTenantIdentifier();
	}
    }

    /**
     * Overrides configure method to get selectQuery, updateQuery and
     * insertQuery
     * 
     * @param type
     * @param params
     * @param dialect
     * 
     */
    @Override
    public void configure(Type type, Properties params, Dialect dialect)
	    throws MappingException {

	super.configure(type, params, dialect);

	this.selectQuery = super.buildSelectQuery(dialect);
	this.updateQuery = super.buildUpdateQuery();
	this.insertQuery = super.buildInsertQuery();
	this.segmentValue = super.getSegmentValue();
	this.identifierType = super.getIdentifierType();
	this.tableName = super.getTableName();
	this.optimizer = super.getOptimizer();
    }

    /**
     * Generates entity id if it is null else bypasses generation stage and
     * updates table field with existing value
     * 
     * @param session
     * @param entity
     * 
     */
    @Override
    public synchronized Serializable generate(SessionImplementor session,
	    Object entity) {

	Serializable id;

	id = session.getEntityPersister(null, entity).getClassMetadata()
		.getIdentifier(entity, session);
	if (id == null) {
	    id = super.generate(session, entity);
	} else {
	    id = generateIncrementally(session, Long.valueOf(id.toString()));
	}

	return id;
    }

    /**
     * Updates (or inserts) table column value with existing id
     * 
     * @param session
     * @param currentValue
     * @return {@link Serializable}
     */
    public Serializable generateIncrementally(final SessionImplementor session,
	    final long currentValue) {

	final SqlStatementLogger statementLogger = session.getFactory()
		.getServiceRegistry().getService(JdbcServices.class)
		.getSqlStatementLogger();
	final AccessCallback callback = new AccessCallbackImpl(session,
		currentValue, statementLogger);

	return optimizer.generate(callback);
    }
}
