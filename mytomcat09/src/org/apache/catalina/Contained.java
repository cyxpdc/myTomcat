package org.apache.catalina;

/**
 * <p>
 * Decoupling interface which specifies that an implementing class is associated
 * with at most one <strong>Container</strong> instance.
 * </p>
 *
 * @author Craig R. McClanahan
 * @author Peter Donald
 * @version $Revision: 1.3 $ $Date: 2001/07/22 20:13:30 $
 */

public interface Contained {

	// -------------------------------------------------------------- Properties

	/**
	 * Return the <code>Container</code> with which this instance is associated
	 * (if any); otherwise return <code>null</code>.
	 */
	public Container getContainer();

	/**
	 * Set the <code>Container</code> with which this instance is associated.
	 *
	 * @param container
	 *            The Container instance with which this instance is to be
	 *            associated, or <code>null</code> to disassociate this instance
	 *            from any Container
	 */
	public void setContainer(Container container);

}
