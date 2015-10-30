package org.lightmare.criteria.resolvers;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.objectweb.asm.ClassReader;

/**
 * Caches read classes for synchronization sake
 * 
 * @author Levan Tsinadze
 *
 */
public class CachedClassReader extends ClassReader {

    private static final ConcurrentMap<String, ClassReader> CLASS_FILES = new ConcurrentHashMap<>();

    public CachedClassReader(byte[] buff) {
	super(buff);
    }

    public CachedClassReader(String name) throws IOException {
	super(name);
    }

    /**
     * Gets {@link ClassReader} from cache
     * 
     * @param name
     * @return
     * @throws IOException
     */
    public static ClassReader get(String name) throws IOException {

	ClassReader classReader = CLASS_FILES.get(name);

	if (classReader == null) {
	    classReader = new CachedClassReader(name);
	    CLASS_FILES.putIfAbsent(name, classReader);
	}

	return classReader;
    }
}
