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
package org.lightmare.criteria.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract utility class for annotated members
 * 
 * @author Levan Tsinadze
 *
 */
abstract class AbstractAnnotationUtils extends AbstractMemberUtils {

    /**
     * Gets {@link List} of all {@link Method}s from passed class annotated with
     * specified annotation
     *
     * @param type
     * @param annotationType
     * @return {@link List}<Method>
     * @throws IOException
     */
    public static List<Method> getAnnotatedMethods(Class<?> type, Class<? extends Annotation> annotationType)
            throws IOException {

        List<Method> methods = new ArrayList<Method>();

        Method[] allMethods = getDeclaredMethods(type);
        methods = filterByAnnotation(allMethods, annotationType);

        return methods;
    }

    /**
     * Gets {@link List} of all {@link Field}s from passed class annotated with
     * specified annotation
     *
     * @param type
     * @param annotationType
     * @return {@link List}<Field>
     * @throws IOException
     */
    public static List<Field> getAnnotatedFields(Class<?> type, Class<? extends Annotation> annotationType)
            throws IOException {

        List<Field> fields = new ArrayList<Field>();

        Field[] allFields = type.getDeclaredFields();
        fields = filterByAnnotation(allFields, annotationType);

        return fields;
    }
}
