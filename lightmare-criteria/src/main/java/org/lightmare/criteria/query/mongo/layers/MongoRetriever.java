package org.lightmare.criteria.query.mongo.layers;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bson.Document;
import org.lightmare.criteria.annotations.DBColumn;
import org.lightmare.criteria.annotations.DBTransient;
import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Retrieves data from BSON document and converst to entity
 * 
 * @author Levan Tsinadze
 *
 */
public class MongoRetriever {

    private static final ConcurrentMap<Class<?>, List<FieldType>> COLUMNS = new ConcurrentHashMap<>();

    public static class FieldType {

        final Field field;

        final String name;

        final Class<?> type;

        public FieldType(final Field field) {

            this.field = field;

            this.name = ObjectUtils.ifIsNull(field.getAnnotation(DBColumn.class), c -> field.getName(),
                    DBColumn::value);
            this.type = field.getType();
        }

        public void set(Document document, Object result) {
            Object value = document.get(name, type);
            ClassUtils.set(field, result, value);
        }
    }

    private static FieldType getColumnName(Field field) {
        return ObjectUtils.ifIsValid(field, c -> ClassUtils.notAnnotated(c, DBTransient.class), FieldType::new);
    }

    private static List<FieldType> getColumns(Field[] fields) {
        return CollectionUtils.toList(fields, MongoRetriever::getColumnName);
    }

    private static List<FieldType> put(Class<?> type) {

        List<FieldType> columns = ObjectUtils.ifNonNull(type::getDeclaredFields, MongoRetriever::getColumns,
                c -> Collections.emptyList());
        COLUMNS.putIfAbsent(type, columns);

        return columns;
    }

    public static List<FieldType> getColumns(Class<?> type) {
        return ObjectUtils.getOrInit(() -> COLUMNS.get(type), () -> put(type));
    }
}
