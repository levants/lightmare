package org.lightmare.utils.earfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.fs.FileType;

/**
 * Implementation of {@link DirUtils} for ear files
 * 
 * @author levan
 * 
 */
public class ExtUtils extends DirUtils {

    public static final FileType type = FileType.EAR;

    private File tmpFile;

    public ExtUtils(String path) {
	super(path);
    }

    public ExtUtils(File file) {
	super(file);
    }

    public ExtUtils(URL url) throws IOException {
	super(url);
    }

    @Override
    public FileType getType() {

	return type;
    }

    /**
     * Exctracts ear file into the temporary file
     * 
     * @throws IOException
     */
    protected void exctractEar() throws IOException {

	tmpFile = File.createTempFile(realFile.getName(),
		StringUtils.EMPTY_STRING);
	tmpFile.delete();
	tmpFile.mkdir();

	addTmpFile(tmpFile);
	ZipFile zipFile = getEarFile();
	Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();

	while (zipFileEntries.hasMoreElements()) {
	    ZipEntry entry = zipFileEntries.nextElement();
	    exctractFile(entry);
	}

    }

    protected void exctractFile(ZipEntry entry) throws IOException {

	InputStream extStream = null;
	OutputStream out = null;
	try {
	    extStream = getEarFile().getInputStream(entry);
	    File file = new File(tmpFile, entry.getName());
	    File parrent = file.getParentFile();
	    if (ObjectUtils.notTrue(parrent.exists())) {
		parrent.mkdirs();
		addTmpFile(parrent);
	    }
	    addTmpFile(file);
	    if (ObjectUtils.notTrue(entry.isDirectory())) {

		if (ObjectUtils.notTrue(file.exists())) {
		    file.createNewFile();
		}

		out = new FileOutputStream(file);

		byte[] buffer = new byte[1024];
		int len;
		while ((len = extStream.read(buffer)) != -1) {
		    out.write(buffer, 0, len);
		}
	    } else {
		file.mkdir();
	    }

	} finally {

	    ObjectUtils.close(extStream);
	    ObjectUtils.close(out);
	}
    }

    @Override
    protected void scanArchive(Object... args) throws IOException {

	exctractEar();
	super.realFile = tmpFile;
	super.path = tmpFile.getPath();
	super.isDirectory = tmpFile.isDirectory();
	super.scanArchive(args);
    }
}
