package org.apache.catalina.logger;

/**
 * Simple implementation of <b>Logger</b> that writes to System.err.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.3 $ $Date: 2002/04/26 21:09:06 $
 */

public class SystemErrLogger extends LoggerBase {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The descriptive information about this implementation.
	 */
	protected static final String info = "org.apache.catalina.logger.SystemErrLogger/1.0";

	// --------------------------------------------------------- Public Methods

	/**
	 * Writes the specified message to a servlet log file, usually an event log.
	 * The name and type of the servlet log is specific to the servlet
	 * container.
	 *
	 * @param msg
	 *            A <code>String</code> specifying the message to be written to
	 *            the log file
	 */
	public void log(String msg) {
		System.err.println(msg);
	}
}
