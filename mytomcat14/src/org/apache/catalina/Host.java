package org.apache.catalina;

public interface Host extends Container {

	// ----------------------------------------------------- Manifest Constants

	/**
	 * The ContainerEvent event type sent when a new alias is added by
	 * <code>addAlias()</code>.
	 */
	public static final String ADD_ALIAS_EVENT = "addAlias";

	/**
	 * The ContainerEvent event type sent when an old alias is removed by
	 * <code>removeAlias()</code>.
	 */
	public static final String REMOVE_ALIAS_EVENT = "removeAlias";

	// ------------------------------------------------------------- Properties

	/**
	 * Return the application root for this Host. This can be an absolute
	 * pathname, a relative pathname, or a URL.
	 */
	public String getAppBase();

	/**
	 * Set the application root for this Host. This can be an absolute pathname,
	 * a relative pathname, or a URL.
	 *
	 * @param appBase
	 *            The new application root
	 */
	public void setAppBase(String appBase);

	/**
	 * Return the value of the auto deploy flag. If true, it indicates that this
	 * host's child webapps should be discovred and automatically deployed.
	 */
	public boolean getAutoDeploy();

	/**
	 * Set the auto deploy flag value for this host.
	 * 
	 * @param autoDeploy
	 *            The new auto deploy flag
	 */
	public void setAutoDeploy(boolean autoDeploy);

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

	/**
	 * Return the canonical, fully qualified, name of the virtual host this
	 * Container represents.
	 */
	public String getName();

	/**
	 * Set the canonical, fully qualified, name of the virtual host this
	 * Container represents.
	 *
	 * @param name
	 *            Virtual host name
	 *
	 * @exception IllegalArgumentException
	 *                if name is null
	 */
	public void setName(String name);

	// --------------------------------------------------------- Public Methods

	/**
	 * Import the DefaultContext config into a web application context.
	 * 
	 * @param context
	 *            web application context to import default context
	 */
	public void importDefaultContext(Context context);

	/**
	 * Add an alias name that should be mapped to this same Host.
	 *
	 * @param alias
	 *            The alias to be added
	 */
	public void addAlias(String alias);

	/**
	 * Return the set of alias names for this Host. If none are defined, a zero
	 * length array is returned.
	 */
	public String[] findAliases();

	/**
	 * Return the Context that would be used to process the specified
	 * host-relative request URI, if any; otherwise return <code>null</code>.
	 * 返回一个用来引入的HTTP请求的Context容器的实例
	 * @param uri
	 *            Request URI to be mapped
	 */
	public Context map(String uri);

	/**
	 * Remove the specified alias name from the aliases for this Host.
	 * @param alias
	 *            Alias name to be removed
	 */
	public void removeAlias(String alias);

}
