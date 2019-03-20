package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;

public interface Manager {

	// ------------------------------------------------------------- Properties

	/**
	 * Return the Container with which this Manager is associated.
	 */
	public Container getContainer();

	/**
	 * Set the Container with which this Manager is associated.
	 *
	 * @param container
	 *            The newly associated Container
	 */
	public void setContainer(Container container);

	/**
	 * Return the DefaultContext with which this Manager is associated.
	 */
	public DefaultContext getDefaultContext();

	/**
	 * Set the DefaultContext with which this Manager is associated.
	 *
	 * @param defaultContext
	 *            The newly associated DefaultContext
	 */
	public void setDefaultContext(DefaultContext defaultContext);

	/**
	 * Return the distributable flag for the sessions supported by this Manager.
	 */
	public boolean getDistributable();

	/**
	 * Set the distributable flag for the sessions supported by this Manager. If
	 * this flag is set, all user data objects added to sessions associated with
	 * this manager must implement Serializable.
	 *
	 * @param distributable
	 *            The new distributable flag
	 */
	public void setDistributable(boolean distributable);

	/**
	 * Return descriptive information about this Manager implementation and the
	 * corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo();

	/**
	 * Return the default maximum inactive interval (in seconds) for Sessions
	 * created by this Manager.
	 */
	public int getMaxInactiveInterval();

	/**
	 * Set the default maximum inactive interval (in seconds) for Sessions
	 * created by this Manager.
	 *
	 * @param interval
	 *            The new default value
	 */
	public void setMaxInactiveInterval(int interval);

	// --------------------------------------------------------- Public Methods

	/**
	 * Add this Session to the set of active Sessions for this Manager.
	 *
	 * @param session
	 *            Session to be added
	 */
	public void add(Session session);

	/**
	 * Add a property change listener to this component.
	 *
	 * @param listener
	 *            The listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Construct and return a new session object, based on the default settings
	 * specified by this Manager's properties. The session id will be assigned
	 * by this method, and available via the getId() method of the returned
	 * session. If a new session cannot be created for any reason, return
	 * <code>null</code>.
	 *
	 * @exception IllegalStateException
	 *                if a new session cannot be instantiated for any reason
	 */
	public Session createSession();

	/**
	 * Return the active Session, associated with this Manager, with the
	 * specified session id (if any); otherwise return <code>null</code>.
	 *
	 * @param id
	 *            The session id for the session to be returned
	 *
	 * @exception IllegalStateException
	 *                if a new session cannot be instantiated for any reason
	 * @exception IOException
	 *                if an input/output error occurs while processing this
	 *                request
	 */
	public Session findSession(String id) throws IOException;

	/**
	 * Return the set of active Sessions associated with this Manager. If this
	 * Manager has no active Sessions, a zero-length array is returned.
	 */
	public Session[] findSessions();

	/**
	 * Load any currently active sessions that were previously unloaded to the
	 * appropriate persistence mechanism, if any. If persistence is not
	 * supported, this method returns without doing anything.
	 *
	 * @exception ClassNotFoundException
	 *                if a serialized class cannot be found during the reload
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	public void load() throws ClassNotFoundException, IOException;

	/**
	 * Remove this Session from the active Sessions for this Manager.
	 *
	 * @param session
	 *            Session to be removed
	 */
	public void remove(Session session);

	/**
	 * Remove a property change listener from this component.
	 *
	 * @param listener
	 *            The listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Save any currently active sessions in the appropriate persistence
	 * mechanism, if any. If persistence is not supported, this method returns
	 * without doing anything.
	 *
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	public void unload() throws IOException;

}
