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
package org.lightmare.criteria.resolvers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lightmare.criteria.utils.ClassLoaderUtils;
import org.lightmare.criteria.utils.IOUtils;
import org.lightmare.criteria.utils.StringUtils;
import org.objectweb.asm.ClassReader;

/**
 * Caches read classes for multiply and parallel uses sake
 * 
 * @author Levan Tsinadze
 *
 */
public class CachedClassReader extends ClassReader {

    private static final ConcurrentMap<String, ClassReader> CLASS_FILES = new ConcurrentHashMap<>();

    private static final String CLASS = ".class";

    public CachedClassReader(byte[] buff) {
	super(buff);
    }

    public CachedClassReader(InputStream is) throws IOException {
	super(is);
    }

    /**
     * Generates class file name from class name
     * 
     * @param name
     * @return {@link String} class file name
     */
    private static String getResource(String name) {
	return name.replace(StringUtils.DOT, File.separatorChar).concat(CLASS);
    }

    /**
     * Initializes {@link org.objectweb.asm.ClassReader} from class name
     * 
     * @param name
     * @return {@link org.objectweb.asm.ClassReader} by class name
     * @throws IOException
     */
    private static ClassReader initClassReader(String name) throws IOException {

	ClassReader classReader;

	String resource = getResource(name);
	InputStream is = ClassLoaderUtils.getResourceAsStream(resource);
	try {
	    classReader = new CachedClassReader(is);
	} finally {
	    IOUtils.close(is);
	}

	return classReader;
    }

    /**
     * Gets {@link ClassReader} from cache or initializes new instance
     * 
     * @param name
     * @return {@link ClassReader} from cache
     * @throws IOException
     */
    public static ClassReader get(String name) throws IOException {

	ClassReader classReader = CLASS_FILES.get(name);

	if (classReader == null) {
	    classReader = initClassReader(name);
	    CLASS_FILES.putIfAbsent(name, classReader);
	}

	return classReader;
    }
}
