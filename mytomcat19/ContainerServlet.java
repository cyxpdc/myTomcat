package org.apache.catalina;

public interface ContainerServlet {

	// ------------------------------------------------------------- Properties

	/**
	 * Return the Wrapper with which this Servlet is associated.
	 */
	public Wrapper getWrapper();

	/**
	 * Set the Wrapper with which this Servlet is associated.
	 *
	 * @param wrapper
	 *            The new associated Wrapper
	 */
	public void setWrapper(Wrapper wrapper);

}
