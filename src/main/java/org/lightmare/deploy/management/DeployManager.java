package org.lightmare.deploy.management;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.lightmare.cache.MetaContainer;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.deploy.fs.Watcher;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.fs.WatchUtils;

/**
 * {@link Servlet} to manage deployed applications
 * 
 * @author levan
 * 
 */
public class DeployManager extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String BEGIN_TAGS = "<tr><td><a name = \"";

    private static final String NAME_OF_TAGS = "\" href=\"#\"\">";

    private static final String END_NAME_TAGS = "</a></td>\n";

    private static final String END_TAGS = "</td></tr>";

    private static final String UNDEPLOY_START = "<td><a name = \"";

    private static final String UNDEPLOY_END = "\" href=\"#\" onClick=\"sendRequest(this.name, 'undeploy')\">undeploy</a></td>";

    private static final String REDEPLOY_START = "<td><a name = \"";

    private static final String REDEPLOY_END = "\" href=\"#\" onClick=\"sendRequest(this.name, 'redeploy')\">redeploy</a></td>";

    private static final String REDEPLOY_PARAM_NAME = "redeploy";

    private static final String TYPE_PARAM_NAME = "type";

    private static final String REDEPLOY_TYPE = "redeploy";

    private static final String UNDEPLOY_TYPE = "undeploy";

    private static final String BEGIN_PAGE = "<html><head><script type=\"text/javascript\">\n"
	    + "/* <![CDATA[ */\n"
	    + "function sendRequest(redeploy, type){\n "
	    + "\t\tvar xmlhttp = new XMLHttpRequest();\n "
	    + "\t\txmlhttp.open(\"GET\",\"DeployManager?redeploy=\" + redeploy + \"&type=\" + type, true);\n"
	    + "\t\txmlhttp.send();\n"
	    + "}\n"
	    + "/* ]]> */\n"
	    + "</script>\n"
	    + "<title>Deployment management</title>"
	    + "</head>\n"
	    + "<body>\n<table>\n";

    private static final String END_PAGE = "</body></table>\n</html>";

    private static final Logger LOG = Logger.getLogger(DeployManager.class);

    /**
     * To filter only deployed sub files from directory
     * 
     * @author levan
     * 
     */
    private static class DeployFiletr implements FileFilter {

	@Override
	public boolean accept(File file) {

	    boolean accept;
	    try {
		URL url = file.toURI().toURL();
		url = WatchUtils.clearURL(url);
		accept = MetaContainer.chackDeployment(url);
	    } catch (MalformedURLException ex) {
		LOG.error(ex.getMessage(), ex);
		accept = false;
	    } catch (IOException ex) {
		LOG.error(ex.getMessage(), ex);
		accept = false;
	    }

	    return accept;
	}

    }

    private List<File> listDeployments() {

	Set<String> paths = MetaCreator.CONFIG.getDeploymentPath();
	File[] files;
	List<File> list = new ArrayList<File>();
	for (String path : paths) {
	    files = new File(path).listFiles(new DeployFiletr());
	    for (File file : files) {
		list.add(file);
	    }
	}

	return list;
    }

    private String getApplications() {

	List<File> files = listDeployments();
	StringBuilder builder = new StringBuilder();
	builder.append(BEGIN_PAGE);
	String tag;
	for (File app : files) {
	    tag = getTag(app.getPath());
	    builder.append(tag);
	}
	builder.append(END_PAGE);

	return builder.toString();
    }

    private String getTag(String app) {

	StringBuilder builder = new StringBuilder();
	builder.append(BEGIN_TAGS);
	builder.append(app);
	builder.append(NAME_OF_TAGS);
	builder.append(app);
	builder.append(END_NAME_TAGS);
	builder.append(UNDEPLOY_START);
	builder.append(app);
	builder.append(UNDEPLOY_END);
	builder.append(REDEPLOY_START);
	builder.append(app);
	builder.append(REDEPLOY_END);
	builder.append(END_TAGS);

	return builder.toString();
    }

    private URL getURL(String fileName) throws IOException {

	URL url = new File(fileName).toURI().toURL();
	url = WatchUtils.clearURL(url);

	return url;
    }

    private void redeploy(URL url) throws IOException {

	Watcher.undeployFile(url);
	Watcher.deployFile(url);
    }

    private void undeploy(URL url) throws IOException {

	Watcher.undeployFile(url);
    }

    @Override
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	String fileName = request.getParameter(REDEPLOY_PARAM_NAME);
	String type = request.getParameter(TYPE_PARAM_NAME);
	if (ObjectUtils.available(fileName)) {
	    URL url = getURL(fileName);
	    if (type == null || REDEPLOY_TYPE.equals(type)) {
		redeploy(url);
	    } else if (UNDEPLOY_TYPE.equals(type)) {
		undeploy(url);
	    }
	}
	String html = getApplications();

	Writer writer = response.getWriter();
	try {
	    writer.write(html);
	} finally {
	    writer.close();
	}
    }
}
