package org.lightmare.deploy.management;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

    private static final String END_TAGS = "</td><td><a href = \"DeployManager\">reload</a></td></tr>";

    private static final String UNDEPLOY_START = "<td><a name = \"";

    private static final String UNDEPLOY_END = "\" href=\"#\" onClick=\"sendRequest(this.name, 'undeploy')\">undeploy</a></td>";

    private static final String REDEPLOY_START = "<td><a name = \"";

    private static final String REDEPLOY_END = "\" href=\"#\" onClick=\"sendRequest(this.name, 'redeploy')\">redeploy</a></td>";

    private static final String REDEPLOY_PARAM_NAME = "file";

    private static final String TYPE_PARAM_NAME = "type";

    private static final String REDEPLOY_TYPE = "redeploy";

    private static final String UNDEPLOY_TYPE = "undeploy";

    private static final String BEGIN_PAGE = "<html>\n"
	    + "\t<head><script type=\"text/javascript\">\n"
	    + "/* <![CDATA[ */\n"
	    + "\t\tfunction sendRequest(redeploy, type){\n "
	    + "\t\t\tvar xmlhttp = new XMLHttpRequest();\n "
	    + "\t\t\txmlhttp.open(\"GET\",\"DeployManager?file=\" + redeploy + \"&type=\" + type, true);\n"
	    + "\t\t\txmlhttp.send();\n" + "}\n" + "/* ]]> */\n" + "</script>\n"
	    + "\t<title>Deployment management</title>" + "</head>\n"
	    + "\t<body>\n" + "\t<table>\n"
	    + "\t\t<tr><td><br><b>deployments</b></br></td></tr>\n";

    private static final String END_PAGE = "</body></table>\n" + "</html>";

    private static final String LOGIN_PAGE = "<html>\n"
	    + "\t\t<head>\n"
	    + "\t\t\t<title>Login</title>\n"
	    + "\t\t</head>\n"
	    + "\t\t<body>\n"
	    + "\t\t\t\t\t\t<br><form name = \"ManagementLogin\" method=\"post\">"
	    + "\t\t\t\t\t\t\t<br><input type=\"user\" name=\"user\"></br>"
	    + "\t\t\t\t\t\t\t<br><input type=\"password\" name=\"password\"></br>"
	    + "\t\t\t\t\t\t\t<br><input type=\"submit\" value=\"Submit\"></br>"
	    + "\t\t\t\t\t\t</form></br>\n";

    private static final String INCORRECT_MESSAGE = "<br><b>invalid user name / passowd</b></br>";

    private static final String END_LOGIN_PAGE = "</html>";

    private static final String USER_PARAMETER_NAME = "user";

    private static final String PASS_PARAMETER_NAME = "password";

    private static final String DEPLOY_PASS_KEY = "deploy_manager_pass";

    /**
     * Class to cache authenticated users for {@link DeployManager} servlet page
     * 
     * @author levan
     * 
     */
    private static class DeployPass implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userName;
    }

    private String getApplications() {

	List<File> files = Watcher.listDeployments();
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

    private String toLoginPage(boolean incorrect) {

	StringBuilder builder = new StringBuilder();
	builder.append(LOGIN_PAGE);
	if (incorrect) {
	    builder.append(INCORRECT_MESSAGE);
	}
	builder.append(END_LOGIN_PAGE);

	return builder.toString();
    }

    private boolean authenticate(String userName, String password,
	    HttpSession session) {

	boolean valid;

	DeployPass pass = new DeployPass();
	pass.userName = userName;
	session.setAttribute(DEPLOY_PASS_KEY, pass);
	valid = Boolean.TRUE;

	return valid;

    }

    private boolean check(HttpSession session) {

	boolean valid = ObjectUtils.notNull(session);
	if (valid) {
	    Object pass = session.getAttribute(DEPLOY_PASS_KEY);
	    valid = ObjectUtils.notNull(pass);
	    if (valid) {
		valid = (pass instanceof DeployPass)
			&& (ObjectUtils.available(((DeployPass) pass).userName));
	    }
	}

	return valid;
    }

    @Override
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {

	boolean check = check(request.getSession(Boolean.FALSE));
	String html;
	if (check) {

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
	    html = getApplications();
	} else {
	    html = toLoginPage(Boolean.FALSE);
	}

	Writer writer = response.getWriter();
	try {
	    writer.write(html);
	} finally {
	    writer.close();
	}
    }

    @Override
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {

	String userName = request.getParameter(USER_PARAMETER_NAME);
	String password = request.getParameter(PASS_PARAMETER_NAME);
	boolean valid = ObjectUtils.available(userName)
		&& ObjectUtils.available(password);
	if (valid) {
	    valid = authenticate(userName, password,
		    request.getSession(Boolean.TRUE));
	}
	if (valid) {
	    response.sendRedirect("DeployManager");
	} else {

	    String html = toLoginPage(Boolean.TRUE);
	    Writer writer = response.getWriter();
	    try {
		writer.write(html);
	    } finally {
		writer.close();
	    }
	}
    }
}
