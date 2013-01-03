package org.lightmare.jpa;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

/**
 * To generate primary keys from table and bypass existing keys
 * 
 * @author levan
 * 
 */
public class TableGeneratorIml extends TableGenerator {

	private String selectQuery;

	private String updateQuery;

	private String insertQuery;

	private String segmentValue;

	private Type identifierType;

	private String tableName;

	private Optimizer optimizer;

	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
			CoreMessageLogger.class, TableGeneratorIml.class.getName());

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
			id = generateImpl(session, Long.valueOf(id.toString()));
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
	public Serializable generateImpl(final SessionImplementor session,
			final long currentValue) {
		final SqlStatementLogger statementLogger = session.getFactory()
				.getServiceRegistry().getService(JdbcServices.class)
				.getSqlStatementLogger();
		return optimizer.generate(new AccessCallback() {
			@Override
			public IntegralDataTypeHolder getNextValue() {
				return session
						.getTransactionCoordinator()
						.getTransaction()
						.createIsolationDelegate()
						.delegateWork(
								new AbstractReturningWork<IntegralDataTypeHolder>() {
									@Override
									public IntegralDataTypeHolder execute(
											Connection connection)
											throws SQLException {
										IntegralDataTypeHolder value = IdentifierGeneratorHelper
												.getIntegralDataTypeHolder(identifierType
														.getReturnedClass());
										int rows;
										do {
											statementLogger.logStatement(
													selectQuery,
													FormatStyle.BASIC
															.getFormatter());
											PreparedStatement selectPS = connection
													.prepareStatement(selectQuery);
											try {
												selectPS.setString(1,
														segmentValue);
												ResultSet selectRS = selectPS
														.executeQuery();
												if (!selectRS.next()) {
													value.initialize(currentValue);
													PreparedStatement insertPS = null;
													try {
														statementLogger
																.logStatement(
																		insertQuery,
																		FormatStyle.BASIC
																				.getFormatter());
														insertPS = connection
																.prepareStatement(insertQuery);
														insertPS.setString(1,
																segmentValue);
														value.bind(insertPS, 2);
														insertPS.execute();
													} finally {
														if (insertPS != null) {
															insertPS.close();
														}
													}
												} else {
													value.initialize(selectRS,
															1);
												}
												selectRS.close();
											} catch (SQLException e) {
												LOG.unableToReadOrInitHiValue(e);
												throw e;
											} finally {
												selectPS.close();
											}

											statementLogger.logStatement(
													updateQuery,
													FormatStyle.BASIC
															.getFormatter());
											PreparedStatement updatePS = connection
													.prepareStatement(updateQuery);
											try {
												final IntegralDataTypeHolder updateValue = value
														.copy().initialize(
																currentValue);
												// TODO check for incrementSize
												// increment options
												updateValue.increment();

												// gets existing value and
												// incremented current values as
												// long types to compare
												Long existing = value.copy()
														.makeValue()
														.longValue();
												Long current = updateValue
														.copy().makeValue()
														.longValue();
												// checks if incremented current
												// value is less then value and
												// puts incremented value
												// instead of incremented
												// currentvalue for
												// update
												if (existing > current) {
													updateValue.initialize(
															existing)
															.increment();
												}
												updateValue.bind(updatePS, 1);
												value.bind(updatePS, 2);
												updatePS.setString(3,
														segmentValue);
												rows = updatePS.executeUpdate();
												value.initialize(currentValue);
											} catch (SQLException e) {
												LOG.unableToUpdateQueryHiValue(
														tableName, e);
												throw e;
											} finally {
												updatePS.close();
											}
										} while (rows == 0);

										return value;
									}
								}, true);
			}
		});
	}
}
