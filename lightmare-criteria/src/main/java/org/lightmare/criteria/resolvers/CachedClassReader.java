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

    private static final ConcurrentMap<String, byte[]> CLASS_FILES = new ConcurrentHashMap<>();

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

	ClassReader classReader;

	byte[] buff = CLASS_FILES.get(name);
	if (buff == null) {
	    classReader = new CachedClassReader(name);
	    buff = classReader.b;
	    CLASS_FILES.putIfAbsent(name, buff);
	} else {
	    classReader = new CachedClassReader(buff);
	}

	return classReader;
    }
}
