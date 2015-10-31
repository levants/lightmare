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
package org.lightmare.criteria.runorder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * Unit test class methods ordered execution provider
 * 
 * @author Levan Tsinadze
 *
 */
public class SortedRunner extends BlockJUnit4ClassRunner {

    private static final int MORE = -1;

    private static final int LESS = -1;

    public SortedRunner(Class<?> testClass) throws InitializationError {
	super(testClass);
    }

    private static int compare(FrameworkMethod method1, FrameworkMethod method2) {

	int comp;

	RunOrder order1 = method1.getAnnotation(RunOrder.class);
	RunOrder order2 = method2.getAnnotation(RunOrder.class);
	if (Objects.nonNull(order1) && order2 == null) {
	    comp = MORE;
	} else if (order1 == null && Objects.nonNull(order2)) {
	    comp = LESS;
	} else {
	    comp = Double.compare(order1.value(), order2.value());
	}

	return comp;
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {

	List<FrameworkMethod> list = super.computeTestMethods();
	Collections.sort(list, (o1, o2) -> compare(o1, o2));

	return list;
    }
}
