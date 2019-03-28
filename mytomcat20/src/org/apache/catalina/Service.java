package org.apache.catalina;

public interface Service {

	// ------------------------------------------------------------- Properties

	/**
	 * Return the <code>Container</code> that handles requests for all
	 * <code>Connectors</code> associated with this Service.
	 */
	public Container getContainer();

	/**
	 * Set the <code>Container</code> that handles requests for all
	 * <code>Connectors</code> associated with this Service.
	 *
	 * @param container
	 *            The new Container
	 */
	public void setContainer(Container container);

	/**
	 * Return descriptive information about this Service implementation and the
	 * corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo();

	/**
	 * Return the name of this Service.
	 */
	public String getName();

	/**
	 * Set the name of this Service.
	 *
	 * @param name
	 *            The new service name
	 */
	public void setName(String name);

	/**
	 * Return the <code>Server</code> with which we are associated (if any).
	 */
	public Server getServer();

	/**
	 * Set the <code>Server</code> with which we are associated (if any).
	 *
	 * @param server
	 *            The server that owns this Service
	 */
	public void setServer(Server server);

	// --------------------------------------------------------- Public Methods

	/**
	 * Add a new Connector to the set of defined Connectors, and associate it
	 * with this Service's Container.
	 *
	 * @param connector
	 *            The Connector to be added
	 */
	public void addConnector(Connector connector);

	/**
	 * Find and return the set of Connectors associated with this Service.
	 */
	public Connector[] findConnectors();

	/**
	 * Remove the specified Connector from the set associated from this Service.
	 * The removed Connector will also be disassociated from our Container.
	 *
	 * @param connector
	 *            The Connector to be removed
	 */
	public void removeConnector(Connector connector);

	/**
	 * Invoke a pre-startup initialization. This is used to allow connectors to
	 * bind to restricted ports under Unix operating environments.
	 *
	 * @exception LifecycleException
	 *                If this server was already initialized.
	 */
	public void initialize() throws LifecycleException;

}
