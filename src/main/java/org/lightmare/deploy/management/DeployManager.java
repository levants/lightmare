package org.lightmare.deploy.management;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.lightmare.deploy.fs.Watcher;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * {@link Servlet} to manage deployed applications
 * 
 * @author levan
 * 
 */
@WebServlet("/DeployManager")
public class DeployManager extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String DEPLOY_MANAGER_DEFAULT_NAME = "/DeployManager";

    // html tags
    private static final String BEGIN_TAGS = "<tr><td><a name = \"";

    private static final String NAME_OF_TAGS = "\" href=\"#\"\">";

    private static final String END_NAME_TAGS = "</a></td>\n";

    private static final String END_TAGS = "</td><td><a href = \"DeployManager\">reload</a></td></tr>";

    private static final String REDEPLOY_START_TAG = "<td><a name = \"";

    private static final String REDEPLOY_TYPE_TAG = "\" href=\"#\" onClick=\"sendRequest(this.name, '";

    private static final String REDEPLOY_FILE_TYPE_TAG = "', '";

    private static final String REDEPLOY_NAME_TAG = "')\">";

    private static final String REDEPLOY_END_TAG = "</a></td>";

    private static final String BEGIN_PAGE = StringUtils
	    .concat("<html>\n",
		    "\t<head><script type=\"text/javascript\">\n",
		    "/* <![CDATA[ */\n",
		    "\t\tfunction sendRequest(redeploy, type, fileType){\n ",
		    "\t\t\tvar xmlhttp = new XMLHttpRequest();\n ",
		    "\t\t\tvar reqUrl = \"DeployManager?file=\" + redeploy + \"&type=\" + type + \"&fileType=\" + fileType;\n",
		    "\t\t\txmlhttp.open(\"GET\", reqUrl, true);\n",
		    "\t\t\txmlhttp.send();\n", "}\n", "/* ]]> */\n",
		    "</script>\n", "\t<title>Deployment management</title>",
		    "</head>\n", "\t<body>\n", "\t<table>\n");

    private static final String TYPE_TAG = "\t\t<tr><td><br><b>";

    private static final String END_TYPE_TAG = "</b></br></td></tr>\n";

    private static final String END_PAGE = "</body></table>\n </html>";

    private static final String LOGIN_PAGE = StringUtils
	    .concat("<html>\n",
		    "\t\t<head>\n",
		    "\t\t\t<title>Login</title>\n",
		    "\t\t</head>\n",
		    "\t\t<body>\n",
		    "\t\t\t\t\t\t<br><form name = \"ManagementLogin\" method=\"post\">",
		    "\t\t\t\t\t\t\t<br><input type=\"user\" name=\"user\"></br>",
		    "\t\t\t\t\t\t\t<br><input type=\"password\" name=\"password\"></br>",
		    "\t\t\t\t\t\t\t<br><input type=\"submit\" value=\"Submit\"></br>",
		    "\t\t\t\t\t\t</form></br>\n");

    private static final String INCORRECT_MESSAGE = "<br><b>invalid user name / passowd</b></br>";

    private static final String END_LOGIN_PAGE = "</html>";

    private static final String DEPLOYMENTS = "deployments";

    private static final String DATA_SOURCES = "datasources";

    // HTTP parameters
    private static final String REDEPLOY_PARAM_NAME = "file";

    private static final String TYPE_PARAM_NAME = "type";

    private static final String REDEPLOY_TYPE = "redeploy";

    private static final String UNDEPLOY_TYPE = "undeploy";

    protected static final String FILE_TYPE_PARAMETER_NAME = "fileType";

    private static final String APP_DEPLOYMENT_TYPE = "application";

    private static final String DTS_DEPLOYMENT_TYPE = "datasource";

    private static final String USER_PARAMETER_NAME = "user";

    private static final String PASS_PARAMETER_NAME = "password";

    // Security for deploy management
    private Security security;

    /**
     * Class to cache authenticated users for {@link DeployManager} java
     * {@link javax.servlet.http.HttpServlet} page
     * 
     * @author levan
     * 
     */
    private static class DeployPass implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userName;
    }

    private String getApplications() {

	List<File> apps = Watcher.listDeployments();
	List<File> dss = Watcher.listDataSources();
	StringBuilder builder = new StringBuilder();
	builder.append(BEGIN_PAGE);
	builder.append(TYPE_TAG);
	builder.append(DEPLOYMENTS);
	builder.append(END_TYPE_TAG);
	String tag;
	if (CollectionUtils.available(apps)) {
	    for (File app : apps) {
		tag = getTag(app.getPath(), APP_DEPLOYMENT_TYPE);
		builder.append(tag);
	    }
	}

	builder.append(BEGIN_PAGE);
	builder.append(TYPE_TAG);
	builder.append(DATA_SOURCES);
	builder.append(END_TYPE_TAG);

	if (ObjectUtils.available(dss)) {
	    for (File ds : dss) {
		tag = getTag(ds.getPath(), DTS_DEPLOYMENT_TYPE);
		builder.append(tag);
	    }
	}

	builder.append(END_PAGE);

	return builder.toString();
    }

    private void fillDeployType(StringBuilder builder, String app, String type,
	    String fileType) {

	builder.append(REDEPLOY_START_TAG);
	builder.append(app);
	builder.append(REDEPLOY_TYPE_TAG);
	builder.append(type);
	builder.append(REDEPLOY_FILE_TYPE_TAG);
	builder.append(fileType);
	builder.append(REDEPLOY_NAME_TAG);
	builder.append(type);
	builder.append(REDEPLOY_END_TAG);
    }

    private String getTag(String app, String fileType) {

	StringBuilder builder = new StringBuilder();
	builder.append(BEGIN_TAGS);
	builder.append(app);
	builder.append(NAME_OF_TAGS);
	builder.append(app);
	builder.append(END_NAME_TAGS);

	fillDeployType(builder, app, UNDEPLOY_TYPE, fileType);
	fillDeployType(builder, app, REDEPLOY_TYPE, fileType);

	builder.append(END_TAGS);

	return builder.toString();
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

	boolean valid = security.authenticate(userName, password);

	if (valid) {
	    DeployPass pass = new DeployPass();
	    pass.userName = userName;
	    session.setAttribute(Security.DEPLOY_PASS_KEY, pass);
	}

	return valid;

    }

    private boolean check(HttpSession session) {

	boolean valid = ObjectUtils.notNull(session);
	if (valid) {
	    Object pass = session.getAttribute(Security.DEPLOY_PASS_KEY);
	    valid = ObjectUtils.notNull(pass);
	    if (valid) {
		valid = (pass instanceof DeployPass)
			&& (ObjectUtils.available(((DeployPass) pass).userName));
	    } else {
		valid = security.check();
	    }
	}

	return valid;
    }

    @Override
    public void init() throws ServletException {

	try {
	    security = new Security();
	} catch (IOException ex) {

	}

	super.init();
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
		if (type == null || REDEPLOY_TYPE.equals(type)) {
		    Watcher.redeployFile(fileName);
		} else if (UNDEPLOY_TYPE.equals(type)) {
		    Watcher.undeployFile(fileName);
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
