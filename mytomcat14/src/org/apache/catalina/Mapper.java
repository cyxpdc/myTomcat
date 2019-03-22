package org.apache.catalina;

public interface Mapper {

	// ------------------------------------------------------------- Properties

	/**
	 * Return the Container with which this Mapper is associated.
	 */
	public Container getContainer();

	/**
	 * Set the Container with which this Mapper is associated.
	 *
	 * @param container
	 *            The newly associated Container
	 *
	 * @exception IllegalArgumentException
	 *                if this Container is not acceptable to this Mapper
	 */
	public void setContainer(Container container);

	/**
	 * Return the protocol for which this Mapper is responsible.
	 */
	public String getProtocol();

	/**
	 * Set the protocol for which this Mapper is responsible.
	 *
	 * @param protocol
	 *            The newly associated protocol
	 */
	public void setProtocol(String protocol);

	// --------------------------------------------------------- Public Methods

	/**
	 * Return the child Container that should be used to process this Request,
	 * based upon its characteristics. If no such child Container can be
	 * identified, return <code>null</code> instead.
	 *
	 * @param request
	 *            Request being processed
	 * @param update
	 *            Update the Request to reflect the mapping selection?
	 */
	public Container map(Request request, boolean update);

}
