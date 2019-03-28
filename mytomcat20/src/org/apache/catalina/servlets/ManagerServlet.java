package org.apache.catalina.servlets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Deployer;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Session;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.util.StringManager;
import org.apache.naming.resources.ProxyDirContext;
import org.apache.naming.resources.WARDirContext;

public class ManagerServlet extends HttpServlet implements ContainerServlet {

	// ----------------------------------------------------- Instance Variables

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The Context container associated with our web application.
	 */
	protected Context context = null;

	/**
	 * The debugging detail level for this servlet.
	 */
	protected int debug = 1;

	/**
	 * File object representing the directory into which the deploy() command
	 * will store the WAR and context configuration files that have been
	 * uploaded.
	 */
	protected File deployed = null;

	/**
	 * The Deployer container that contains our own web application's Context,
	 * along with the associated Contexts for web applications that we are
	 * managing.
	 */
	protected Deployer deployer = null;

	/**
	 * The global JNDI <code>NamingContext</code> for this server, if available.
	 */
	protected javax.naming.Context global = null;

	/**
	 * The string manager for this package.
	 */
	protected static StringManager sm = StringManager.getManager(Constants.Package);

	/**
	 * The Wrapper container associated with this servlet.
	 */
	protected Wrapper wrapper = null;

	// ----------------------------------------------- ContainerServlet Methods

	/**
	 * Return the Wrapper with which we are associated.
	 */
	public Wrapper getWrapper() {
		return this.wrapper;
	}

	/**
	 * Set the Wrapper with which we are associated.
	 *
	 * @param wrapper
	 *            The new wrapper
	 */
	public void setWrapper(Wrapper wrapper) {
		System.out.println("setWrapper:" + wrapper.getName());
		this.wrapper = wrapper;
		context = (Context) wrapper.getParent();
		deployer = (Deployer) context.getParent();
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Finalize this servlet.
	 */
	public void destroy() {
		// No actions necessary
	}

	/**
	 * Process a GET request for the specified resource.
	 *
	 * @param request
	 *            The servlet request we are processing
	 * @param response
	 *            The servlet response we are creating
	 *
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet-specified error occurs
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Verify that we were not accessed using the invoker servlet
		if (request.getAttribute(Globals.INVOKED_ATTR) != null)
			throw new UnavailableException(sm.getString("managerServlet.cannotInvoke"));
		// Identify the request parameters that we need
		String command = request.getPathInfo();
		if (command == null)
			command = request.getServletPath();
		String config = request.getParameter("config");
		String path = request.getParameter("path");
		String type = request.getParameter("type");
		String war = request.getParameter("war");
		// Prepare our output writer to generate the response message
		response.setContentType("text/plain");
		Locale locale = Locale.getDefault();
		response.setLocale(locale);
		PrintWriter writer = response.getWriter();
		// Process the requested command (note - "/deploy" is not listed here)
		if (command == null) {
			writer.println(sm.getString("managerServlet.noCommand"));
		} else if (command.equals("/install")) {
			install(writer, config, path, war);
		} else if (command.equals("/list")) {
			list(writer);
		} else if (command.equals("/reload")) {
			reload(writer, path);
		} else if (command.equals("/remove")) {
			remove(writer, path);
		} else if (command.equals("/resources")) {
			resources(writer, type);
		} else if (command.equals("/roles")) {
			roles(writer);
		} else if (command.equals("/sessions")) {
			sessions(writer, path);
		} else if (command.equals("/start")) {
			start(writer, path);
		} else if (command.equals("/stop")) {
			stop(writer, path);
		} else if (command.equals("/undeploy")) {
			undeploy(writer, path);
		} else {
			writer.println(sm.getString("managerServlet.unknownCommand", command));
		}
		// Finish up the response
		writer.flush();
		writer.close();
	}

	/**
	 * Process a PUT request for the specified resource.
	 *
	 * @param request
	 *            The servlet request we are processing
	 * @param response
	 *            The servlet response we are creating
	 *
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet-specified error occurs
	 */
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Verify that we were not accessed using the invoker servlet
		if (request.getAttribute(Globals.INVOKED_ATTR) != null)
			throw new UnavailableException(sm.getString("managerServlet.cannotInvoke"));

		// Identify the request parameters that we need
		String command = request.getPathInfo();
		if (command == null)
			command = request.getServletPath();
		String path = request.getParameter("path");

		// Prepare our output writer to generate the response message
		response.setContentType("text/plain");
		Locale locale = Locale.getDefault();
		response.setLocale(locale);
		PrintWriter writer = response.getWriter();

		// Process the requested command
		if (command == null) {
			writer.println(sm.getString("managerServlet.noCommand"));
		} else if (command.equals("/deploy")) {
			deploy(writer, path, request);
		} else {
			writer.println(sm.getString("managerServlet.unknownCommand", command));
		}

		// Saving configuration
		Server server = ServerFactory.getServer();
		if ((server != null) && (server instanceof StandardServer)) {
			try {
				((StandardServer) server).store();
			} catch (Exception e) {
				writer.println(sm.getString("managerServlet.saveFail", e.getMessage()));
			}
		}

		// Finish up the response
		writer.flush();
		writer.close();

	}

	/**
	 * Initialize this servlet.
	 */
	public void init() throws ServletException {

		System.out.println("init");
		// Ensure that our ContainerServlet properties have been set
		if ((wrapper == null) || (context == null))
			throw new UnavailableException(sm.getString("managerServlet.noWrapper"));

		// Verify that we were not accessed using the invoker servlet
		String servletName = getServletConfig().getServletName();
		if (servletName == null)
			servletName = "";
		if (servletName.startsWith("org.apache.catalina.INVOKER."))
			throw new UnavailableException(sm.getString("managerServlet.cannotInvoke"));

		// Set our properties from the initialization parameters
		String value = null;
		try {
			value = getServletConfig().getInitParameter("debug");
			debug = Integer.parseInt(value);
		} catch (Throwable t) {
			;
		}

		// Acquire global JNDI resources if available
		Server server = ServerFactory.getServer();
		if ((server != null) && (server instanceof StandardServer)) {
			global = ((StandardServer) server).getGlobalNamingContext();
		}

		// Calculate the directory into which we will be deploying applications
		deployed = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");

		// Log debugging messages as necessary
		if (debug >= 1) {
			log("init: Associated with Deployer '" + deployer.getName() + "'");
			if (global != null) {
				log("init: Global resources are available");
			}
		}

	}

	// -------------------------------------------------------- Private Methods

	/**
	 * Deploy a web application archive (included in the current request) at the
	 * specified context path.
	 *
	 * @param writer
	 *            Writer to render results to
	 * @param path
	 *            Context path of the application to be installed
	 * @param request
	 *            Servlet request we are processing
	 */
	protected synchronized void deploy(PrintWriter writer, String path, HttpServletRequest request) {

		if (debug >= 1) {
			log("deploy: Deploying web application at '" + path + "'");
		}

		// Validate the requested context path
		if ((path == null) || (!path.startsWith("/") && path.equals(""))) {
			writer.println(sm.getString("managerServlet.invalidPath", path));
			return;
		}
		String displayPath = path;
		if (path.equals("/"))
			path = "";
		String basename = null;
		if (path.equals("")) {
			basename = "_";
		} else {
			basename = path.substring(1);
		}
		if (deployer.findDeployedApp(path) != null) {
			writer.println(sm.getString("managerServlet.alreadyContext", displayPath));
			return;
		}

		// Upload the web application archive to a local WAR file
		File localWar = new File(deployed, basename + ".war");
		if (debug >= 2) {
			log("Uploading WAR file to " + localWar);
		}
		try {
			uploadWar(request, localWar);
		} catch (IOException e) {
			log("managerServlet.upload[" + displayPath + "]", e);
			writer.println(sm.getString("managerServlet.exception", e.toString()));
			return;
		}

		// Extract the nested context deployment file (if any)
		File localXml = new File(deployed, basename + ".xml");
		if (debug >= 2) {
			log("Extracting XML file to " + localXml);
		}
		try {
			extractXml(localWar, localXml);
		} catch (IOException e) {
			log("managerServlet.extract[" + displayPath + "]", e);
			writer.println(sm.getString("managerServlet.exception", e.toString()));
			return;
		}

		// Deploy this web application
		try {
			URL warURL = new URL("jar:file:" + localWar.getAbsolutePath() + "!/");
			URL xmlURL = null;
			if (localXml.exists()) {
				xmlURL = new URL("file:" + localXml.getAbsolutePath());
			}
			if (xmlURL != null) {
				deployer.install(xmlURL, warURL);
			} else {
				deployer.install(path, warURL);
			}
		} catch (Throwable t) {
			log("ManagerServlet.deploy[" + displayPath + "]", t);
			writer.println(sm.getString("managerServlet.exception", t.toString()));
			localWar.delete();
			localXml.delete();
			return;
		}

		// Acknowledge successful completion of this deploy command
		writer.println(sm.getString("managerServlet.installed", displayPath));

	}

	/**
	 * Install an application for the specified path from the specified web
	 * application archive.
	 *
	 * @param writer
	 *            Writer to render results to
	 * @param config
	 *            URL of the context configuration file to be installed
	 * @param path
	 *            Context path of the application to be installed
	 * @param war
	 *            URL of the web application archive to be installed
	 */
	protected void install(PrintWriter writer, String config, String path, String war) {

		if (debug >= 1) {
			if (config != null) {
				if (war != null) {
					log("install: Installing context configuration at '" + config + "' from '" + war + "'");
				} else {
					log("install: Installing context configuration at '" + config + "'");
				}
			} else {
				log("install: Installing web application at '" + path + "' from '" + war + "'");
			}
		}

		if (config != null) {

			if ((war != null) && (!war.startsWith("file:") && !war.startsWith("jar:"))) {
				writer.println(sm.getString("managerServlet.invalidWar", war));
				return;
			}

			try {
				if (war == null) {
					deployer.install(new URL(config), null);
				} else {
					deployer.install(new URL(config), new URL(war));
				}
				writer.println(sm.getString("managerServlet.configured", config));
			} catch (Throwable t) {
				log("ManagerServlet.configure[" + config + "]", t);
				writer.println(sm.getString("managerServlet.exception", t.toString()));
			}

		} else {

			if ((path == null) || (!path.startsWith("/") && path.equals(""))) {
				writer.println(sm.getString("managerServlet.invalidPath", path));
				return;
			}
			String displayPath = path;
			if ("/".equals(path)) {
				path = "";
			}
			if ((war == null) || (!war.startsWith("file:") && !war.startsWith("jar:"))) {
				writer.println(sm.getString("managerServlet.invalidWar", war));
				return;
			}

			try {
				Context context = deployer.findDeployedApp(path);
				if (context != null) {
					writer.println(sm.getString("managerServlet.alreadyContext", displayPath));
					return;
				}
				deployer.install(path, new URL(war));
				writer.println(sm.getString("managerServlet.installed", displayPath));
			} catch (Throwable t) {
				log("ManagerServlet.install[" + displayPath + "]", t);
				writer.println(sm.getString("managerServlet.exception", t.toString()));
			}

		}

	}

	/**
	 * Render a list of the currently active Contexts in our virtual host.
	 *
	 * @param writer
	 *            Writer to render to
	 */
	protected void list(PrintWriter writer) {

		if (debug >= 1)
			log("list: Listing contexts for virtual host '" + deployer.getName() + "'");

		writer.println(sm.getString("managerServlet.listed", deployer.getName()));
		String contextPaths[] = deployer.findDeployedApps();
		for (int i = 0; i < contextPaths.length; i++) {
			Context context = deployer.findDeployedApp(contextPaths[i]);
			String displayPath = contextPaths[i];
			if (displayPath.equals(""))
				displayPath = "/";
			if (context != null) {
				if (context.getAvailable()) {
					writer.println(sm.getString("managerServlet.listitem", displayPath, "running",
							"" + context.getManager().findSessions().length, context.getDocBase()));
				} else {
					writer.println(
							sm.getString("managerServlet.listitem", displayPath, "stopped", "0", context.getDocBase()));
				}
			}
		}
	}

	/**
	 * Reload the web application at the specified context path.
	 *
	 * @param writer
	 *            Writer to render to
	 * @param path
	 *            Context path of the application to be restarted
	 */
	protected void reload(PrintWriter writer, String path) {

		if (debug >= 1)
			log("restart: Reloading web application at '" + path + "'");

		if ((path == null) || (!path.startsWith("/") && path.equals(""))) {
			writer.println(sm.getString("managerServlet.invalidPath", path));
			return;
		}
		String displayPath = path;
		if (path.equals("/"))
			path = "";

		try {
			Context context = deployer.findDeployedApp(path);
			if (context == null) {
				writer.println(sm.getString("managerServlet.noContext", displayPath));
				return;
			}
			DirContext resources = context.getResources();
			if (resources instanceof ProxyDirContext) {
				resources = ((ProxyDirContext) resources).getDirContext();
			}
			if (resources instanceof WARDirContext) {
				writer.println(sm.getString("managerServlet.noReload", displayPath));
				return;
			}
			// It isn't possible for the manager to reload itself
			if (context.getPath().equals(this.context.getPath())) {
				writer.println(sm.getString("managerServlet.noSelf"));
				return;
			}
			context.reload();
			writer.println(sm.getString("managerServlet.reloaded", displayPath));
		} catch (Throwable t) {
			log("ManagerServlet.reload[" + displayPath + "]", t);
			writer.println(sm.getString("managerServlet.exception", t.toString()));
		}

	}

	/**
	 * Remove the web application at the specified context path.
	 *
	 * @param writer
	 *            Writer to render to
	 * @param path
	 *            Context path of the application to be removed
	 */
	protected void remove(PrintWriter writer, String path) {

		if (debug >= 1)
			log("remove: Removing web application at '" + path + "'");

		if ((path == null) || (!path.startsWith("/") && path.equals(""))) {
			writer.println(sm.getString("managerServlet.invalidPath", path));
			return;
		}
		String displayPath = path;
		if (path.equals("/"))
			path = "";

		try {
			Context context = deployer.findDeployedApp(path);
			if (context == null) {
				writer.println(sm.getString("managerServlet.noContext", displayPath));
				return;
			}
			// It isn't possible for the manager to remove itself
			if (context.getPath().equals(this.context.getPath())) {
				writer.println(sm.getString("managerServlet.noSelf"));
				return;
			}
			deployer.remove(path);
			writer.println(sm.getString("managerServlet.removed", displayPath));
		} catch (Throwable t) {
			log("ManagerServlet.remove[" + displayPath + "]", t);
			writer.println(sm.getString("managerServlet.exception", t.toString()));
		}

	}

	/**
	 * Render a list of available global JNDI resources.
	 *
	 * @param type
	 *            Fully qualified class name of the resource type of interest,
	 *            or <code>null</code> to list resources of all types
	 */
	protected void resources(PrintWriter writer, String type) {

		if (debug >= 1) {
			if (type != null) {
				log("resources:  Listing resources of type " + type);
			} else {
				log("resources:  Listing resources of all types");
			}
		}

		// Is the global JNDI resources context available?
		if (global == null) {
			writer.println(sm.getString("managerServlet.noGlobal"));
			return;
		}

		// Enumerate the global JNDI resources of the requested type
		if (type != null) {
			writer.println(sm.getString("managerServlet.resourcesType", type));
		} else {
			writer.println(sm.getString("managerServlet.resourcesAll"));
		}

		Class clazz = null;
		try {
			if (type != null) {
				clazz = Class.forName(type);
			}
		} catch (Throwable t) {
			log("ManagerServlet.resources[" + type + "]", t);
			writer.println(sm.getString("managerServlet.exception", t.toString()));
			return;
		}

		printResources(writer, "", global, type, clazz);

	}

	/**
	 * List the resources of the given context.
	 */
	protected void printResources(PrintWriter writer, String prefix, javax.naming.Context namingContext, String type,
			Class clazz) {

		try {
			NamingEnumeration items = namingContext.listBindings("");
			while (items.hasMore()) {
				Binding item = (Binding) items.next();
				if (item.getObject() instanceof javax.naming.Context) {
					printResources(writer, prefix + item.getName() + "/", (javax.naming.Context) item.getObject(), type,
							clazz);
				} else {
					if ((clazz != null) && (!(clazz.isInstance(item.getObject())))) {
						continue;
					}
					writer.print(prefix + item.getName());
					writer.print(':');
					writer.print(item.getClassName());
					// Do we want a description if available?
					writer.println();
				}
			}
		} catch (Throwable t) {
			log("ManagerServlet.resources[" + type + "]", t);
			writer.println(sm.getString("managerServlet.exception", t.toString()));
		}

	}

	/**
	 * Render a list of security role names (and corresponding descriptions)
	 * from the <code>org.apache.catalina.UserDatabase</code> resource that is
	 * connected to the <code>users</code> resource reference. Typically, this
	 * will be the global user database, but can be adjusted if you have
	 * different user databases for different virtual hosts.
	 *
	 * @param writer
	 *            Writer to render to
	 */
	protected void roles(PrintWriter writer) {

		if (debug >= 1) {
			log("roles:  List security roles from user database");
		}

		// Look up the UserDatabase instance we should use
		UserDatabase database = null;
		try {
			InitialContext ic = new InitialContext();
			database = (UserDatabase) ic.lookup("java:comp/env/users");
		} catch (NamingException e) {
			writer.println(sm.getString("managerServlet.userDatabaseError"));
			log("java:comp/env/users", e);
			return;
		}
		if (database == null) {
			writer.println(sm.getString("managerServlet.userDatabaseMissing"));
			return;
		}

		// Enumerate the available roles
		writer.println(sm.getString("managerServlet.rolesList"));
		Iterator roles = database.getRoles();
		if (roles != null) {
			while (roles.hasNext()) {
				Role role = (Role) roles.next();
				writer.print(role.getRolename());
				writer.print(':');
				if (role.getDescription() != null) {
					writer.print(role.getDescription());
				}
				writer.println();
			}
		}

	}

	/**
	 * Session information for the web application at the specified context
	 * path. Displays a profile of session MaxInactiveInterval timeouts listing
	 * number of sessions for each 10 minute timeout interval up to 10 hours.
	 *
	 * @param writer
	 *            Writer to render to
	 * @param path
	 *            Context path of the application to list session information
	 *            for
	 */
	protected void sessions(PrintWriter writer, String path) {

		if (debug >= 1)
			log("sessions: Session information for web application at '" + path + "'");

		if ((path == null) || (!path.startsWith("/") && path.equals(""))) {
			writer.println(sm.getString("managerServlet.invalidPath", path));
			return;
		}
		String displayPath = path;
		if (path.equals("/"))
			path = "";
		try {
			Context context = deployer.findDeployedApp(path);
			if (context == null) {
				writer.println(sm.getString("managerServlet.noContext", displayPath));
				return;
			}
			writer.println(sm.getString("managerServlet.sessions", displayPath));
			writer.println(sm.getString("managerServlet.sessiondefaultmax",
					"" + context.getManager().getMaxInactiveInterval() / 60));
			Session[] sessions = context.getManager().findSessions();
			int[] timeout = new int[60];
			int notimeout = 0;
			for (int i = 0; i < sessions.length; i++) {
				int time = sessions[i].getMaxInactiveInterval() / (10 * 60);
				if (time < 0)
					notimeout++;
				else if (time >= timeout.length)
					timeout[timeout.length - 1]++;
				else
					timeout[time]++;
			}
			if (timeout[0] > 0)
				writer.println(sm.getString("managerServlet.sessiontimeout", "<10" + timeout[0]));
			for (int i = 1; i < timeout.length - 1; i++) {
				if (timeout[i] > 0)
					writer.println(sm.getString("managerServlet.sessiontimeout", "" + (i) * 10 + " - <" + (i + 1) * 10,
							"" + timeout[i]));
			}
			if (timeout[timeout.length - 1] > 0)
				writer.println(sm.getString("managerServlet.sessiontimeout", ">=" + timeout.length * 10,
						"" + timeout[timeout.length - 1]));
			if (notimeout > 0)
				writer.println(sm.getString("managerServlet.sessiontimeout", "unlimited", "" + notimeout));
		} catch (Throwable t) {
			log("ManagerServlet.sessions[" + displayPath + "]", t);
			writer.println(sm.getString("managerServlet.exception", t.toString()));
		}

	}

	/**
	 * Start the web application at the specified context path.
	 *
	 * @param writer
	 *            Writer to render to
	 * @param path
	 *            Context path of the application to be started
	 */
	protected void start(PrintWriter writer, String path) {
		if (debug >= 1)
			log("start: Starting web application at '" + path + "'");
		if ((path == null) || (!path.startsWith("/") && path.equals(""))) {
			writer.println(sm.getString("managerServlet.invalidPath", path));
			return;
		}
		
		String displayPath = path;
		if (path.equals("/"))
			path = "";
		
		try {
			Context context = deployer.findDeployedApp(path);
			if (context == null) {
				writer.println(sm.getString("managerServlet.noContext", displayPath));
				return;
			}
			deployer.start(path);
			if (context.getAvailable())
				writer.println(sm.getString("managerServlet.started", displayPath));
			else
				writer.println(sm.getString("managerServlet.startFailed", displayPath));
		} catch (Throwable t) {
			getServletContext().log(sm.getString("managerServlet.startFailed", displayPath), t);
			writer.println(sm.getString("managerServlet.startFailed", displayPath));
			writer.println(sm.getString("managerServlet.exception", t.toString()));
		}

	}

	/**
	 * Stop the web application at the specified context path.
	 *
	 * @param writer
	 *            Writer to render to
	 * @param path
	 *            Context path of the application to be stopped
	 */
	protected void stop(PrintWriter writer, String path) {

		if (debug >= 1)
			log("stop: Stopping web application at '" + path + "'");

		if ((path == null) || (!path.startsWith("/") && path.equals(""))) {
			writer.println(sm.getString("managerServlet.invalidPath", path));
			return;
		}
		String displayPath = path;
		if (path.equals("/"))
			path = "";

		try {
			Context context = deployer.findDeployedApp(path);
			if (context == null) {
				writer.println(sm.getString("managerServlet.noContext", displayPath));
				return;
			}
			// It isn't possible for the manager to stop itself
			if (context.getPath().equals(this.context.getPath())) {
				writer.println(sm.getString("managerServlet.noSelf"));
				return;
			}
			deployer.stop(path);
			writer.println(sm.getString("managerServlet.stopped", displayPath));
		} catch (Throwable t) {
			log("ManagerServlet.stop[" + displayPath + "]", t);
			writer.println(sm.getString("managerServlet.exception", t.toString()));
		}

	}

	/**
	 * Undeploy the web application at the specified context path.
	 *
	 * @param writer
	 *            Writer to render to
	 * @param path
	 *            Context path of the application to be removed
	 */
	protected void undeploy(PrintWriter writer, String path) {

		if (debug >= 1)
			log("undeploy: Undeploying web application at '" + path + "'");

		if ((path == null) || (!path.startsWith("/") && path.equals(""))) {
			writer.println(sm.getString("managerServlet.invalidPath", path));
			return;
		}
		String displayPath = path;
		if (path.equals("/"))
			path = "";

		try {

			// Validate the Context of the specified application
			Context context = deployer.findDeployedApp(path);
			if (context == null) {
				writer.println(sm.getString("managerServlet.noContext", displayPath));
				return;
			}

			// Identify the appBase of the owning Host of this Context (if any)
			String appBase = null;
			File appBaseDir = null;
			String appBasePath = null;
			if (context.getParent() instanceof Host) {
				appBase = ((Host) context.getParent()).getAppBase();
				appBaseDir = new File(appBase);
				if (!appBaseDir.isAbsolute()) {
					appBaseDir = new File(System.getProperty("catalina.base"), appBase);
				}
				appBasePath = appBaseDir.getCanonicalPath();
			}

			// Validate the docBase path of this application
			String deployedPath = deployed.getCanonicalPath();
			String docBase = context.getDocBase();
			File docBaseDir = new File(docBase);
			if (!docBaseDir.isAbsolute()) {
				docBaseDir = new File(appBaseDir, docBase);
			}
			String docBasePath = docBaseDir.getCanonicalPath();
			if (!docBasePath.startsWith(deployedPath)) {
				writer.println(sm.getString("managerServlet.noDocBase", displayPath));
				return;
			}

			// Remove this web application and its associated docBase
			if (debug >= 2) {
				log("Undeploying document base " + docBasePath);
			}
			// It isn't possible for the manager to undeploy itself
			if (context.getPath().equals(this.context.getPath())) {
				writer.println(sm.getString("managerServlet.noSelf"));
				return;
			}
			deployer.remove(path);
			if (docBaseDir.isDirectory()) {
				undeployDir(docBaseDir);
			} else {
				docBaseDir.delete(); // Delete the WAR file
			}
			String docBaseXmlPath = docBasePath.substring(0, docBasePath.length() - 4) + ".xml";
			File docBaseXml = new File(docBaseXmlPath);
			docBaseXml.delete();
			writer.println(sm.getString("managerServlet.undeployed", displayPath));

		} catch (Throwable t) {
			log("ManagerServlet.undeploy[" + displayPath + "]", t);
			writer.println(sm.getString("managerServlet.exception", t.toString()));
		}

		// Saving configuration
		Server server = ServerFactory.getServer();
		if ((server != null) && (server instanceof StandardServer)) {
			try {
				((StandardServer) server).store();
			} catch (Exception e) {
				writer.println(sm.getString("managerServlet.saveFail", e.getMessage()));
			}
		}

	}

	// -------------------------------------------------------- Support Methods

	/**
	 * Extract the context configuration file from the specified WAR, if it is
	 * present. If it is not present, ensure that the corresponding file does
	 * not exist.
	 *
	 * @param war
	 *            File object representing the WAR
	 * @param xml
	 *            File object representing where to store the extracted context
	 *            configuration file (if it exists)
	 *
	 * @exception IOException
	 *                if an i/o error occurs
	 */
	protected void extractXml(File war, File xml) throws IOException {

		xml.delete();
		JarFile jar = null;
		JarEntry entry = null;
		InputStream istream = null;
		BufferedOutputStream ostream = null;
		try {
			jar = new JarFile(war);
			entry = jar.getJarEntry("META-INF/context.xml");
			if (entry == null) {
				return;
			}
			istream = jar.getInputStream(entry);
			ostream = new BufferedOutputStream(new FileOutputStream(xml), 1024);
			byte buffer[] = new byte[1024];
			while (true) {
				int n = istream.read(buffer);
				if (n < 0) {
					break;
				}
				ostream.write(buffer, 0, n);
			}
			ostream.flush();
			ostream.close();
			ostream = null;
			istream.close();
			istream = null;
			entry = null;
			jar.close();
			jar = null;
		} catch (IOException e) {
			xml.delete();
			throw e;
		} finally {
			if (ostream != null) {
				try {
					ostream.close();
				} catch (Throwable t) {
					;
				}
				ostream = null;
			}
			if (istream != null) {
				try {
					istream.close();
				} catch (Throwable t) {
					;
				}
				istream = null;
			}
			entry = null;
			if (jar != null) {
				try {
					jar.close();
				} catch (Throwable t) {
					;
				}
				jar = null;
			}
		}

	}

	/**
	 * Delete the specified directory, including all of its contents and
	 * subdirectories recursively.
	 *
	 * @param dir
	 *            File object representing the directory to be deleted
	 */
	protected void undeployDir(File dir) {

		String files[] = dir.list();
		if (files == null) {
			files = new String[0];
		}
		for (int i = 0; i < files.length; i++) {
			File file = new File(dir, files[i]);
			if (file.isDirectory()) {
				undeployDir(file);
			} else {
				file.delete();
			}
		}
		dir.delete();

	}

	/**
	 * Upload the WAR file included in this request, and store it at the
	 * specified file location.
	 *
	 * @param request
	 *            The servlet request we are processing
	 * @param file
	 *            The file into which we should store the uploaded WAR
	 *
	 * @exception IOException
	 *                if an I/O error occurs during processing
	 */
	protected void uploadWar(HttpServletRequest request, File war) throws IOException {

		war.delete();
		ServletInputStream istream = null;
		BufferedOutputStream ostream = null;
		try {
			istream = request.getInputStream();
			ostream = new BufferedOutputStream(new FileOutputStream(war), 1024);
			byte buffer[] = new byte[1024];
			while (true) {
				int n = istream.read(buffer);
				if (n < 0) {
					break;
				}
				ostream.write(buffer, 0, n);
			}
			ostream.flush();
			ostream.close();
			ostream = null;
			istream.close();
			istream = null;
		} catch (IOException e) {
			war.delete();
			throw e;
		} finally {
			if (ostream != null) {
				try {
					ostream.close();
				} catch (Throwable t) {
					;
				}
				ostream = null;
			}
			if (istream != null) {
				try {
					istream.close();
				} catch (Throwable t) {
					;
				}
				istream = null;
			}
		}

	}

}
