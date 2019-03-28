package org.apache.catalina;

import java.beans.PropertyChangeListener;

public interface Logger {
	// ----------------------------------------------------- Manifest Constants
	/**
	 * Verbosity level constants for log messages that may be filtered by the
	 * underlying logger.
	 */

	public static final int FATAL = Integer.MIN_VALUE;//致命的

	public static final int ERROR = 1;

	public static final int WARNING = 2;

	public static final int INFORMATION = 3;

	public static final int DEBUG = 4;

	// ------------------------------------------------------------- Properties
	/**
	 * 获得关联的servlet容器
	 */
	public Container getContainer();
	/**
	 * 关联servlet容器
	 * @param container
	 */
	public void setContainer(Container container);

	/**
	 * Return descriptive information about this Logger implementation and the
	 * corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo();

	/**
	 * Return the verbosity level of this logger. Messages logged with a higher
	 * verbosity than this level will be silently ignored.
	 */
	public int getVerbosity();

	/**
	 * Set the verbosity level of this logger. Messages logged with a higher
	 * verbosity than this level will be silently ignored.
	 *
	 * @param verbosity
	 *            The new verbosity level
	 */
	public void setVerbosity(int verbosity);

	// --------------------------------------------------------- Public Methods
	/**
	 * 增加PropertyChangeListener实例
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Writes the specified message to a servlet log file, usually an event log.
	 * The name and type of the servlet log is specific to the servlet
	 * container. This message will be logged unconditionally.
	 *
	 * @param message
	 *            A <code>String</code> specifying the message to be written to
	 *            the log file
	 */
	public void log(String message);

	/**
	 * Writes the specified exception, and message, to a servlet log file. The
	 * implementation of this method should call
	 * <code>log(msg, exception)</code> instead. This method is deprecated in
	 * the ServletContext interface, but not deprecated here to avoid many
	 * useless compiler warnings. This message will be logged unconditionally.
	 *
	 * @param exception
	 *            An <code>Exception</code> to be reported
	 * @param msg
	 *            The associated message string
	 */
	public void log(Exception exception, String msg);

	/**
	 * Writes an explanatory message and a stack trace for a given
	 * <code>Throwable</code> exception to the servlet log file. The name and
	 * type of the servlet log file is specific to the servlet container,
	 * usually an event log. This message will be logged unconditionally.
	 *
	 * @param message
	 *            A <code>String</code> that describes the error or exception
	 * @param throwable
	 *            The <code>Throwable</code> error or exception
	 */
	public void log(String message, Throwable throwable);

	/**
	 * Writes the specified message to the servlet log file, usually an event
	 * log, if the logger is set to a verbosity level equal to or higher than
	 * the specified value for this message.
	 *
	 * @param message
	 *            A <code>String</code> specifying the message to be written to
	 *            the log file
	 * @param verbosity
	 *            Verbosity level of this message
	 */
	public void log(String message, int verbosity);

	/**
	 * Writes the specified message and exception to the servlet log file,
	 * usually an event log, if the logger is set to a verbosity level equal to
	 * or higher than the specified value for this message.
	 *
	 * @param message
	 *            A <code>String</code> that describes the error or exception
	 * @param throwable
	 *            The <code>Throwable</code> error or exception
	 * @param verbosity
	 *            Verbosity level of this message
	 */
	public void log(String message, Throwable throwable, int verbosity);
	/**
	 * 移除PropertyChangeListener实例
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

}
