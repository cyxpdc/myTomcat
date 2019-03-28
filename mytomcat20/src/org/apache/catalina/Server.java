package org.apache.catalina;

import org.apache.catalina.deploy.NamingResources;

public interface Server {

	// ------------------------------------------------------------- Properties

	/**
	 * Return descriptive information about this Server implementation and the
	 * corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo();

	/**
	 * Return the global naming resources.
	 */
	public NamingResources getGlobalNamingResources();

	/**
	 * Set the global naming resources.
	 * 
	 * @param namingResources
	 *            The new global naming resources
	 */
	public void setGlobalNamingResources(NamingResources globalNamingResources);

	/**
	 * Return the port number we listen to for shutdown commands.
	 */
	public int getPort();

	/**
	 * Set the port number we listen to for shutdown commands.
	 *
	 * @param port
	 *            The new port number
	 */
	public void setPort(int port);

	/**
	 * Return the shutdown command string we are waiting for.
	 */
	public String getShutdown();

	/**
	 * Set the shutdown command we are waiting for.
	 *
	 * @param shutdown
	 *            The new shutdown command
	 */
	public void setShutdown(String shutdown);

	// --------------------------------------------------------- Public Methods

	/**
	 * Add a new Service to the set of defined Services.
	 *
	 * @param service
	 *            The Service to be added
	 */
	public void addService(Service service);

	/**
	 * Wait until a proper shutdown command is received, then return.
	 */
	public void await();

	/**
	 * Return the specified Service (if it exists); otherwise return
	 * <code>null</code>.
	 *
	 * @param name
	 *            Name of the Service to be returned
	 */
	public Service findService(String name);

	/**
	 * Return the set of Services defined within this Server.
	 */
	public Service[] findServices();

	/**
	 * Remove the specified Service from the set associated from this Server.
	 *
	 * @param service
	 *            The Service to be removed
	 */
	public void removeService(Service service);

	/**
	 * Invoke a pre-startup initialization. This is used to allow connectors to
	 * bind to restricted ports under Unix operating environments.
	 *
	 * @exception LifecycleException
	 *                If this server was already initialized.
	 */
	public void initialize() throws LifecycleException;
}
