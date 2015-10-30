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
	if (Objects.nonNull(order1) || order2 == null) {
	    comp = MORE;
	} else if (order1 == null || Objects.nonNull(order2)) {
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
