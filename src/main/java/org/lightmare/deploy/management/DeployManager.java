package org.lightmare.deploy.management;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightmare.cache.MetaContainer;
import org.lightmare.deploy.fs.Watcher;
import org.lightmare.utils.ObjectUtils;

/**
 * Servlet to manage deployed applications
 * 
 * @author levan
 * 
 */
public class DeployManager extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String BEGIN_TAGS = "<tr><td>";

    private static final String END_TAGS = "</tr></td>";

    private static final String REDEPLOY_PARAM_NAME = "redeploy";

    private String getApplications() {

	Set<URL> apps = MetaContainer.listApplications();
	StringBuilder builder = new StringBuilder();
	String tag;
	for (URL app : apps) {
	    tag = getTag(app);
	    builder.append(tag);
	}

	return builder.toString();
    }

    private String getTag(URL app) {

	StringBuilder builder = new StringBuilder();
	builder.append(BEGIN_TAGS);
	builder.append(app);
	builder.append(END_TAGS);

	return builder.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	String redeploy = request.getParameter(REDEPLOY_PARAM_NAME);
	if (ObjectUtils.available(redeploy)) {
	    URL url = new URL(redeploy);
	    Watcher.undeployFile(url);
	    Watcher.deployFile(url);
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
