package org.lightmare.criteria.cache;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Objects;

/**
 * Phantom reference for lambda generated class
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaReference extends PhantomReference<Class<?>> {

    public LambdaReference(Class<?> referent, ReferenceQueue<? super Class<?>> queue) {
	super(referent, queue);
    }

    /**
     * Clears {@link Class} from cache before dereference
     * 
     * @param lambdaType
     */
    public void removeFromCache(Class<?> lambdaType) {

	if (Objects.nonNull(lambdaType)) {
	    LambdaCache.remove(lambdaType);
	}
    }

    @Override
    public void clear() {

	try {
	    Class<?> lambdaType = get();
	    removeFromCache(lambdaType);
	} finally {
	    super.clear();
	}
    }
}
