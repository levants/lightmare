package ge.gov.mia.lightmare.unitorder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class SortedRunner extends BlockJUnit4ClassRunner {

	public SortedRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> list = super.computeTestMethods();
		Collections.sort(list, new Comparator<FrameworkMethod>() {
			@Override
			public int compare(FrameworkMethod method1, FrameworkMethod method2) {
				RunOrder order1 = method1.getAnnotation(RunOrder.class);
				RunOrder order2 = method2.getAnnotation(RunOrder.class);

				if (order1 == null || order2 == null) {
					return -1;
				} else {
					return order1.value() >= order2.value() ? 1 : -1;
				}
			}
		});
		return list;
	}

}
