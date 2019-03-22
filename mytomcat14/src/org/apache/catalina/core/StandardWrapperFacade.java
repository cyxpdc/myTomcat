package org.apache.catalina.core;

import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Facade for the <b>StandardWrapper</b> object.
 *
 * @author Remy Maucharat
 * @version $Revision: 1.3 $ $Date: 2001/07/22 20:25:08 $
 */

public final class StandardWrapperFacade implements ServletConfig {

	// ----------------------------------------------------------- Constructors

	/**
	 * Create a new facede around a StandardWrapper.
	 */
	public StandardWrapperFacade(StandardWrapper config) {
		this.config = (ServletConfig) config;
	}

	// ----------------------------------------------------- Instance Variables

	/**
	 * Wrapped config.
	 */
	private ServletConfig config = null;

	// -------------------------------------------------- ServletConfig Methods

	public String getServletName() {
		return config.getServletName();
	}

	public ServletContext getServletContext() {
		ServletContext theContext = config.getServletContext();
		if ((theContext != null) && (theContext instanceof ApplicationContext))
			theContext = ((ApplicationContext) theContext).getFacade();
		return (theContext);
	}

	public String getInitParameter(String name) {
		return config.getInitParameter(name);
	}

	public Enumeration<?> getInitParameterNames() {
		return config.getInitParameterNames();
	}

}
