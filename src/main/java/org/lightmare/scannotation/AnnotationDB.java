package org.lightmare.scannotation;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import org.scannotation.archiveiterator.Filter;
import org.scannotation.archiveiterator.IteratorFactory;
import org.scannotation.archiveiterator.StreamIterator;

/**
 * Extention of {@link org.scannotation.AnnotationDB} for saving Map<
 * {@link String}, {@link URL}> of class name and {@link URL} for its archive
 * 
 * @author levan
 * 
 */
public class AnnotationDB extends org.scannotation.AnnotationDB {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Map<String, URL> classOwnerships = new HashMap<String, URL>();

	private boolean ignoreScan(String intf) {
		for (String ignored : ignoredPackages) {
			if (intf.startsWith(ignored + ".")) {
				return true;
			} else {
				// System.out.println("NOT IGNORING: " + intf);
			}
		}
		return false;
	}

	protected void populate(Annotation[] annotations, String className, URL url) {
		if (annotations == null)
			return;
		Set<String> classAnnotations = classIndex.get(className);
		for (Annotation ann : annotations) {
			Set<String> classes = annotationIndex.get(ann.getTypeName());
			if (classes == null) {
				classes = new HashSet<String>();
				annotationIndex.put(ann.getTypeName(), classes);
			}
			classes.add(className);
			if (!classOwnerships.containsKey(className)) {
				classOwnerships.put(className, url);
			}
			classAnnotations.add(ann.getTypeName());
		}
	}

	protected void scanClass(ClassFile cf, URL url) {
		String className = cf.getName();
		AnnotationsAttribute visible = (AnnotationsAttribute) cf
				.getAttribute(AnnotationsAttribute.visibleTag);
		AnnotationsAttribute invisible = (AnnotationsAttribute) cf
				.getAttribute(AnnotationsAttribute.invisibleTag);

		if (visible != null) {
			populate(visible.getAnnotations(), className, url);
		}
		if (invisible != null) {
			populate(invisible.getAnnotations(), className, url);
		}
	}

	public void scanClass(InputStream bits, URL url) throws IOException {
		DataInputStream dstream = new DataInputStream(new BufferedInputStream(
				bits));
		ClassFile cf = null;
		try {
			cf = new ClassFile(dstream);
			classIndex.put(cf.getName(), new HashSet<String>());
			if (scanClassAnnotations) {
				scanClass(cf, url);
			}
			if (scanMethodAnnotations || scanParameterAnnotations) {
				scanMethods(cf);
			}
			if (scanFieldAnnotations) {
				scanFields(cf);
			}

			// create an index of interfaces the class implements
			if (cf.getInterfaces() != null) {
				Set<String> intfs = new HashSet<String>();
				for (String intf : cf.getInterfaces()) {
					intfs.add(intf);
				}
				implementsIndex.put(cf.getName(), intfs);
			}

		} finally {
			dstream.close();
			bits.close();
		}
	}

	@Override
	public void scanArchives(URL... urls) throws IOException {
		for (URL url : urls) {
			Filter filter = new Filter() {
				public boolean accepts(String filename) {
					if (filename.endsWith(".class")) {
						if (filename.startsWith("/"))
							filename = filename.substring(1);
						if (!ignoreScan(filename.replace('/', '.')))
							return true;
						// System.out.println("IGNORED: " + filename);
					}
					return false;
				}
			};

			StreamIterator it = IteratorFactory.create(url, filter);

			InputStream stream;
			while ((stream = it.next()) != null) {
				scanClass(stream, url);
			}
		}
	}

	public Map<String, URL> getClassOwnerships() {
		return classOwnerships;
	}

}
