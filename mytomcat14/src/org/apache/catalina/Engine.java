package org.apache.catalina;

public interface Engine extends Container {

	// ------------------------------------------------------------- Properties

	/**
	 * Return the default hostname for this Engine.
	 */
	public String getDefaultHost();

	/**
	 * Set the default hostname for this Engine.
	 *
	 * @param defaultHost
	 *            The new default host
	 */
	public void setDefaultHost(String defaultHost);

	/**
	 * Retrieve the JvmRouteId for this engine.
	 */
	public String getJvmRoute();

	/**
	 * Set the JvmRouteId for this engine.
	 *
	 * @param jvmRouteId
	 *            the (new) JVM Route ID. Each Engine within a cluster must have
	 *            a unique JVM Route ID.
	 */
	public void setJvmRoute(String jvmRouteId);

	/**
	 * Return the <code>Service</code> with which we are associated (if any).
	 */
	public Service getService();

	/**
	 * Set the <code>Service</code> with which we are associated (if any).
	 *
	 * @param service
	 *            The service that owns this Engine
	 */
	public void setService(Service service);

	/**
	 * Set the DefaultContext for new web applications.
	 *
	 * @param defaultContext
	 *            The new DefaultContext
	 */
	public void addDefaultContext(DefaultContext defaultContext);

	/**
	 * Retrieve the DefaultContext for new web applications.
	 */
	public DefaultContext getDefaultContext();

	// --------------------------------------------------------- Public Methods

	/**
	 * Import the DefaultContext config into a web application context.
	 *
	 * @param context
	 *            web application context to import default context
	 */
	public void importDefaultContext(Context context);

}
