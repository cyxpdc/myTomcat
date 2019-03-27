package org.apache.catalina;

import java.io.IOException;
import java.net.URL;

/* public interface Deployer extends Container { */
public interface Deployer {

	// ----------------------------------------------------- Manifest Constants

	/**
	 * The ContainerEvent event type sent when a new application is being
	 * installed by <code>install()</code>, before it has been started.
	 */
	public static final String PRE_INSTALL_EVENT = "pre-install";

	/**
	 * The ContainerEvent event type sent when a new application is installed by
	 * <code>install()</code>, after it has been started.
	 */
	public static final String INSTALL_EVENT = "install";

	/**
	 * The ContainerEvent event type sent when an existing application is
	 * removed by <code>remove()</code>.
	 */
	public static final String REMOVE_EVENT = "remove";

	// --------------------------------------------------------- Public Methods

	/**
	 * Return the name of the Container with which this Deployer is associated.
	 */
	public String getName();

	/**
	 * Install a new web application, whose web application archive is at the
	 * specified URL, into this container with the specified context path. A
	 * context path of "" (the empty string) should be used for the root
	 * application for this container. Otherwise, the context path must start
	 * with a slash.
	 * <p>
	 * If this application is successfully installed, a ContainerEvent of type
	 * <code>INSTALL_EVENT</code> will be sent to all registered listeners, with
	 * the newly created <code>Context</code> as an argument.
	 *
	 * @param contextPath
	 *            The context path to which this application should be installed
	 *            (must be unique)
	 * @param war
	 *            A URL of type "jar:" that points to a WAR file, or type
	 *            "file:" that points to an unpacked directory structure
	 *            containing the web application to be installed
	 *
	 * @exception IllegalArgumentException
	 *                if the specified context path is malformed (it must be ""
	 *                or start with a slash)
	 * @exception IllegalStateException
	 *                if the specified context path is already attached to an
	 *                existing web application
	 * @exception IOException
	 *                if an input/output error was encountered during
	 *                installation
	 */
	public void install(String contextPath, URL war) throws IOException;

	/**
	 * <p>
	 * Install a new web application, whose context configuration file
	 * (consisting of a <code>&lt;Context&gt;</code> element) and web
	 * application archive are at the specified URLs.
	 * </p>
	 *
	 * <p>
	 * If this application is successfully installed, a ContainerEvent of type
	 * <code>INSTALL_EVENT</code> will be sent to all registered listeners, with
	 * the newly created <code>Context</code> as an argument.
	 * </p>
	 *
	 * @param config
	 *            A URL that points to the context configuration file to be used
	 *            for configuring the new Context
	 * @param war
	 *            A URL of type "jar:" that points to a WAR file, or type
	 *            "file:" that points to an unpacked directory structure
	 *            containing the web application to be installed
	 *
	 * @exception IllegalArgumentException
	 *                if one of the specified URLs is null
	 * @exception IllegalStateException
	 *                if the context path specified in the context configuration
	 *                file is already attached to an existing web application
	 * @exception IOException
	 *                if an input/output error was encountered during
	 *                installation
	 */
	public void install(URL config, URL war) throws IOException;

	/**
	 * Return the Context for the deployed application that is associated with
	 * the specified context path (if any); otherwise return <code>null</code>.
	 *
	 * @param contextPath
	 *            The context path of the requested web application
	 */
	public Context findDeployedApp(String contextPath);

	/**
	 * Return the context paths of all deployed web applications in this
	 * Container. If there are no deployed applications, a zero-length array is
	 * returned.
	 */
	public String[] findDeployedApps();

	/**
	 * Remove an existing web application, attached to the specified context
	 * path. If this application is successfully removed, a ContainerEvent of
	 * type <code>REMOVE_EVENT</code> will be sent to all registered listeners,
	 * with the removed <code>Context</code> as an argument.
	 *
	 * @param contextPath
	 *            The context path of the application to be removed
	 *
	 * @exception IllegalArgumentException
	 *                if the specified context path is malformed (it must be ""
	 *                or start with a slash)
	 * @exception IllegalArgumentException
	 *                if the specified context path does not identify a
	 *                currently installed web application
	 * @exception IOException
	 *                if an input/output error occurs during removal
	 */
	public void remove(String contextPath) throws IOException;

	/**
	 * Start an existing web application, attached to the specified context
	 * path. Only starts a web application if it is not running.
	 *
	 * @param contextPath
	 *            The context path of the application to be started
	 *
	 * @exception IllegalArgumentException
	 *                if the specified context path is malformed (it must be ""
	 *                or start with a slash)
	 * @exception IllegalArgumentException
	 *                if the specified context path does not identify a
	 *                currently installed web application
	 * @exception IOException
	 *                if an input/output error occurs during startup
	 */
	public void start(String contextPath) throws IOException;

	/**
	 * Stop an existing web application, attached to the specified context path.
	 * Only stops a web application if it is running.
	 *
	 * @param contextPath
	 *            The context path of the application to be stopped
	 *
	 * @exception IllegalArgumentException
	 *                if the specified context path is malformed (it must be ""
	 *                or start with a slash)
	 * @exception IllegalArgumentException
	 *                if the specified context path does not identify a
	 *                currently installed web application
	 * @exception IOException
	 *                if an input/output error occurs while stopping the web
	 *                application
	 */
	public void stop(String contextPath) throws IOException;

}
