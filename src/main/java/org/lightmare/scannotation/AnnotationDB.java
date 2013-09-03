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

import org.apache.log4j.Logger;
import org.lightmare.utils.AbstractIOUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.scannotation.archiveiterator.Filter;
import org.scannotation.archiveiterator.IteratorFactory;
import org.scannotation.archiveiterator.StreamIterator;

/**
 * Extension of {@link org.scannotation.AnnotationDB} for saving Map<
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

    // To store which class in which URL is found
    protected Map<String, URL> classOwnersURLs = new HashMap<String, URL>();

    // To store which class in which File is found
    protected Map<String, String> classOwnersFiles = new HashMap<String, String>();

    private static final char FILE_EXTEWNTION_SELIM = '.';

    private static final char FILE_SEPARATOR_CHAR = '/';

    private static final Logger LOG = Logger.getLogger(AnnotationDB.class);

    private String getFileName(URL url) {

	String fileName = url.getFile();
	int lastIndex = fileName.lastIndexOf("/");
	if (lastIndex > -1) {
	    ++lastIndex;
	    fileName = fileName.substring(lastIndex);
	}

	return fileName;
    }

    private boolean ignoreScan(String intf) {

	String value;
	for (String ignored : ignoredPackages) {
	    value = StringUtils.concat(ignored, FILE_EXTEWNTION_SELIM);
	    if (intf.startsWith(value)) {
		return Boolean.TRUE;
	    }
	}
	return Boolean.FALSE;
    }

    protected void populate(Annotation[] annotations, String className, URL url) {

	if (annotations == null)
	    return;
	Set<String> classAnnotations = classIndex.get(className);
	String fileName;
	for (Annotation ann : annotations) {
	    Set<String> classes = annotationIndex.get(ann.getTypeName());
	    if (classes == null) {
		classes = new HashSet<String>();
		annotationIndex.put(ann.getTypeName(), classes);
	    }
	    classes.add(className);
	    if (!classOwnersURLs.containsKey(className)) {
		classOwnersURLs.put(className, url);
	    }
	    if (!classOwnersFiles.containsKey(className)) {
		fileName = getFileName(url);
		classOwnersFiles.put(className, fileName);
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

	if (ObjectUtils.notNull(visible)) {
	    populate(visible.getAnnotations(), className, url);
	}
	if (ObjectUtils.notNull(invisible)) {
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
	    if (ObjectUtils.notNull(cf.getInterfaces())) {
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
	LOG.info("Started scanning for archives on @Stateless annotation");
	for (URL url : urls) {
	    Filter filter = new Filter() {
		public boolean accepts(String subFileName) {
		    if (subFileName.endsWith(AbstractIOUtils.CLASS_FILE_EXT)) {
			if (subFileName
				.startsWith(AbstractIOUtils.FILE_SEPARATOR))
			    subFileName = subFileName.substring(1);
			if (!ignoreScan(subFileName.replace(
				FILE_SEPARATOR_CHAR, FILE_EXTEWNTION_SELIM)))
			    return Boolean.TRUE;
		    }
		    return Boolean.FALSE;
		}
	    };
	    LOG.info(String.format("Scanning URL %s ", url));

	    StreamIterator it = IteratorFactory.create(url, filter);

	    InputStream stream;
	    while (ObjectUtils.notNull((stream = it.next()))) {
		scanClass(stream, url);
	    }

	    LOG.info(String.format("Finished URL %s scanning", url));
	}

	LOG.info("Finished scanning for archives on @Stateless annotation");
    }

    public Map<String, URL> getClassOwnersURLs() {
	return classOwnersURLs;
    }

    public Map<String, String> getClassOwnersFiles() {
	return classOwnersFiles;
    }

}
