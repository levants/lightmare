package org.lightmare.scannotation;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import org.apache.log4j.Logger;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.IOUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.codecs.ArchiveUtils;
import org.scannotation.archiveiterator.Filter;
import org.scannotation.archiveiterator.IteratorFactory;
import org.scannotation.archiveiterator.StreamIterator;

/**
 * Extension of {@link org.scannotation.AnnotationDB} for saving Map<
 * {@link String}, {@link URL}> of class name and {@link URL} for its archive
 * 
 * @author levan
 * @since 0.0.18-SNAPSHOT
 */
public class AnnotationDB extends org.scannotation.AnnotationDB {

    private static final long serialVersionUID = 1L;

    // To store which class in which URL is found
    protected Map<String, URL> classOwnersURLs = new WeakHashMap<String, URL>();

    // To store which class in which File is found
    protected Map<String, String> classOwnersFiles = new WeakHashMap<String, String>();

    // File separator and extension characters
    private static final char FILE_EXTEWNTION_SELIM = '.';

    private static final char FILE_SEPARATOR_CHAR = '/';

    // Log messages
    private static String SCANNING_STARTED_MESSAGE = "Started scanning for archives on @Stateless annotation";
    private static String SCANNING_FINISHED_MESSAGE = "Finished scanning for archives on @Stateless annotation";
    private static final String SCANNING_URL_MESSAGE = "Scanning URL ";
    private static final String FINISHED_URL_MESSAGE = "Finished URL scanning ";

    private static final Logger LOG = Logger.getLogger(AnnotationDB.class);

    /**
     * Filters java archive files
     * 
     * @author levan
     * @since 0.0.84-SNAPSHOT
     */
    protected class ArchiveFilter implements Filter {

	@Override
	public boolean accepts(String subFileName) {

	    boolean valid;

	    if (subFileName.endsWith(ArchiveUtils.CLASS_FILE_EXT)) {

		if (subFileName.startsWith(ArchiveUtils.FILE_SEPARATOR)) {
		    subFileName = subFileName
			    .substring(CollectionUtils.SECOND_INDEX);
		}

		String fileNameForCheck = subFileName.replace(
			FILE_SEPARATOR_CHAR, FILE_EXTEWNTION_SELIM);
		valid = !ignoreScan(fileNameForCheck);
	    } else {
		valid = Boolean.FALSE;
	    }

	    return valid;
	}
    }

    /**
     * Gets file name from passed {@link URL} instance
     * 
     * @param url
     * @return {@link String}
     */
    private String getFileName(URL url) {

	String fileName = url.getFile();

	int lastIndex = fileName.lastIndexOf(ArchiveUtils.FILE_SEPARATOR);
	if (lastIndex > StringUtils.NOT_EXISTING_INDEX) {
	    ++lastIndex;
	    fileName = fileName.substring(lastIndex);
	}

	return fileName;
    }

    /**
     * Checks file name should be or not ignored from scanning file list
     * 
     * @param intf
     * @return <code>boolean</code>
     */
    private boolean ignoreScan(String intf) {

	boolean valid = Boolean.FALSE;

	String value;
	String ignored;
	int length = ignoredPackages.length;
	for (int i = CollectionUtils.FIRST_INDEX; ObjectUtils.notTrue(valid)
		&& i < length; i++) {
	    ignored = ignoredPackages[i];
	    value = StringUtils.concat(ignored, FILE_EXTEWNTION_SELIM);
	    if (intf.startsWith(value)) {
		valid = Boolean.TRUE;
	    }
	}

	return valid;
    }

    /**
     * caches scanned file information
     * 
     * @param annotations
     * @param className
     * @param url
     */
    protected void populate(Annotation[] annotations, String className, URL url) {

	if (ObjectUtils.notNull(annotations)) {
	    Set<String> classAnnotations = classIndex.get(className);
	    String fileName;
	    boolean contained;
	    for (Annotation ann : annotations) {

		Set<String> classes = annotationIndex.get(ann.getTypeName());
		if (classes == null) {
		    classes = new HashSet<String>();
		    annotationIndex.put(ann.getTypeName(), classes);
		}

		classes.add(className);

		CollectionUtils.putIfAbscent(classOwnersURLs, className, url);

		contained = classOwnersFiles.containsKey(className);
		if (ObjectUtils.notTrue(contained)) {
		    fileName = getFileName(url);
		    classOwnersFiles.put(className, fileName);
		}

		classAnnotations.add(ann.getTypeName());
	    }
	}
    }

    /**
     * Scans passed {@link ClassFile} instance for specific annotations
     * 
     * @param cf
     * @param url
     */
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

    /**
     * Scans passed {@link InputStream} instance for specific annotations
     * 
     * @param bits
     * @param url
     * @throws IOException
     */
    public void scanClass(InputStream bits, URL url) throws IOException {

	DataInputStream dstream = new DataInputStream(new BufferedInputStream(
		bits));
	ClassFile cf = null;

	try {
	    cf = new ClassFile(dstream);
	    String classFileName = cf.getName();
	    classIndex.put(classFileName, new HashSet<String>());

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
	    String[] interfaces = cf.getInterfaces();
	    if (ObjectUtils.notNull(interfaces)) {
		Set<String> intfs = new HashSet<String>();
		for (String intf : interfaces) {
		    intfs.add(intf);
		}

		implementsIndex.put(classFileName, intfs);
	    }
	} finally {
	    IOUtils.closeAll(dstream, bits);
	}
    }

    @Override
    public void scanArchives(URL... urls) throws IOException {

	LOG.info(SCANNING_STARTED_MESSAGE);
	for (URL url : urls) {

	    Filter filter = new ArchiveFilter();
	    LOG.info(StringUtils.concat(SCANNING_URL_MESSAGE, url));

	    StreamIterator it = IteratorFactory.create(url, filter);
	    InputStream stream = it.next();

	    while (ObjectUtils.notNull(stream)) {
		scanClass(stream, url);
		stream = it.next();
	    }

	    LOG.info(StringUtils.concat(FINISHED_URL_MESSAGE, url));
	}

	LOG.info(SCANNING_FINISHED_MESSAGE);
    }

    public Map<String, URL> getClassOwnersURLs() {
	return classOwnersURLs;
    }

    public Map<String, String> getClassOwnersFiles() {
	return classOwnersFiles;
    }
}
