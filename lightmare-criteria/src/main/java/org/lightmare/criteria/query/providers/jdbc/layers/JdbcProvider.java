/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
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
package org.lightmare.criteria.query.providers.jdbc.layers;

import java.lang.reflect.Field;
import java.sql.Connection;

import org.lightmare.criteria.annotations.DBColumn;
import org.lightmare.criteria.annotations.DBTable;
import org.lightmare.criteria.config.Configuration.ColumnResolver;
import org.lightmare.criteria.config.DefaultConfiguration.DefaultResolver;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.layers.QueryLayer;
import org.lightmare.criteria.query.providers.jpa.JpaQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation for {@link org.lightmare.criteria.query.layers.LayerProvider}
 * JDBC queries
 * 
 * @author Levan Tsinadze
 *
 */
public class JdbcProvider implements LayerProvider {

    private final Connection connection;

    private final ColumnResolver resolver;

    private static final String SELECT_TYPE = ".*";

    private static final String COUNT_TYPE = "*";

    public JdbcProvider(final Connection connection, final ColumnResolver resolver) {
        this.connection = connection;
        this.resolver = resolver;
    }

    public JdbcProvider(final Connection connection) {
        this(connection, new DefaultResolver());
    }

    /**
     * Generates {@link org.lightmare.criteria.query.layers.QueryLayer} from SQL
     * {@link String} and {@link Class} entity type
     * 
     * @param sql
     * @param type
     * @return {@link org.lightmare.criteria.query.layers.QueryLayer} from SQL
     *         and entity type
     */
    public <T> QueryLayer<T> query(String sql, Class<T> type) {
        return new JdbcQueryLayer<T>(connection, sql.toString(), type);
    }

    /**
     * Generates {@link org.lightmare.criteria.query.layers.QueryLayer} from
     * {@link String} parameter
     * 
     * @param sql
     * @return {@link org.lightmare.criteria.query.layers.QueryLayer} from
     *         {@link String}
     */
    public QueryLayer<?> query(String sql) {
        return new JdbcQueryLayer<Void>(connection, sql.toString(), Void.class);
    }

    @Override
    public <T> QueryLayer<T> query(Class<T> type, Object... params) {

        QueryLayer<T> queryLayer;

        String sql = CollectionUtils.getFirstType(params);
        queryLayer = query(sql, type);

        return queryLayer;
    }

    @Override
    public QueryLayer<?> query(Object... params) {
        return query(Void.class, params);
    }

    public ColumnResolver getResolver() {
        return resolver;
    }

    @Override
    public String getTableName(Class<?> type) {
        return ObjectUtils.ifNull(() -> type.getAnnotation(DBTable.class), c -> type.getName(),
                c -> StringUtils.thisOrDefault(c.value(), type::getName));
    }

    /**
     * gets DB column name from {@link java.lang.reflect.Field} annotation
     * 
     * @param field
     * @return {@link String} DB column name
     */
    private static String getColumnName(Field field) {
        return ObjectUtils.ifNull(() -> field.getAnnotation(DBColumn.class), c -> field.getName(),
                c -> StringUtils.thisOrDefault(c.value(), field::getName));
    }

    @Override
    public String getColumnName(QueryTuple tuple) {
        return ObjectUtils.ifNonNull(tuple::getField, JdbcProvider::getColumnName, c -> tuple.getFieldName());
    }

    @Override
    public String getSelectType(String alias) {
        return StringUtils.concat(alias, SELECT_TYPE);
    }

    @Override
    public String getCountType(String alias) {
        return StringUtils.concat(alias, COUNT_TYPE);
    }

    @Override
    public String alias() {
        return JpaQueryStream.DEFAULT_ALIAS;
    }

    @Override
    public void close() {
        ObjectUtils.acceptQuietly(connection, Connection::close);
    }
}
