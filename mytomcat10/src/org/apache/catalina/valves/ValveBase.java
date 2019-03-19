package org.apache.catalina.valves;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.util.StringManager;

public abstract class ValveBase implements Contained, Valve {

	// ------------------------------------------------------ Instance Variables

	/**
	 * The Container whose pipeline this Valve is a component of.
	 */
	protected Container container = null;

	/**
	 * The debugging detail level for this component.
	 */
	protected int debug = 0;

	/**
	 * Descriptive information about this Valve implementation. This value
	 * should be overridden by subclasses.
	 */
	protected static String info = "org.apache.catalina.core.ValveBase/1.0";

	/**
	 * The string manager for this package.
	 */
	protected final static StringManager sm = StringManager.getManager(Constants.Package);

	// -------------------------------------------------------------- Properties

	/**
	 * Return the Container with which this Valve is associated, if any.
	 */
	public Container getContainer() {

		return (container);

	}

	/**
	 * Set the Container with which this Valve is associated, if any.
	 *
	 * @param container
	 *            The new associated container
	 */
	public void setContainer(Container container) {

		this.container = container;

	}

	/**
	 * Return the debugging detail level for this component.
	 */
	public int getDebug() {

		return (this.debug);

	}

	/**
	 * Set the debugging detail level for this component.
	 *
	 * @param debug
	 *            The new debugging detail level
	 */
	public void setDebug(int debug) {

		this.debug = debug;

	}

	/**
	 * Return descriptive information about this Valve implementation.
	 */
	public String getInfo() {

		return (info);

	}

	// ---------------------------------------------------------- Public Methods

	/**
	 * The implementation-specific logic represented by this Valve. See the
	 * Valve description for the normal design patterns for this method.
	 * <p>
	 * This method <strong>MUST</strong> be provided by a subclass.
	 *
	 * @param request
	 *            The servlet request to be processed
	 * @param response
	 *            The servlet response to be created
	 * @param context
	 *            The valve context used to invoke the next valve in the current
	 *            processing pipeline
	 *
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet error occurs
	 */
	public abstract void invoke(Request request, Response response, ValveContext context)
			throws IOException, ServletException;

}
