package org.apache.catalina.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.StringManager;

/**
 * Implementation of <b>Logger</b> that appends log messages to a file named
 * {prefix}.{date}.{suffix} in a configured directory, with an optional
 * preceding timestamp.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.8 $ $Date: 2002/06/09 02:19:43 $
 */

public class FileLogger extends LoggerBase implements Lifecycle {
	// ----------------------------------------------------- Instance Variables
	/**
	 * The as-of date for the currently open log file, or a zero-length string
	 * if there is no open log file.
	 */
	private String date = "";
	/**
	 * The directory in which log files are created.
	 */
	private String directory = "logs";
	/**
	 * The descriptive information about this implementation.
	 */
	protected static final String info = 
			"org.apache.catalina.logger.FileLogger/1.0";
	/**
	 * The lifecycle event support for this component.
	 */
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	/**
	 * The prefix that is added to log file filenames.
	 */
	private String prefix = "catalina.";
	/**
	 * The string manager for this package.
	 */
	private StringManager sm = StringManager.getManager(Constants.Package);
	/**
	 * Has this component been started?
	 */
	private boolean started = false;
	/**
	 * The suffix that is added to log file filenames.
	 */
	private String suffix = ".log";
	/**
	 * Should logged messages be date/time stamped?
	 */
	private boolean timestamp = false;
	/**
	 * The PrintWriter to which we are currently logging, if any.
	 */
	private PrintWriter writer = null;
	// ------------------------------------------------------------- Properties
	/**
	 * Return the directory in which we create log files.
	 */
	public String getDirectory() {
		return directory;
	}
	/**
	 * Set the directory in which we create log files.
	 *
	 * @param directory
	 *            The new log file directory
	 */
	public void setDirectory(String directory) {
		String oldDirectory = this.directory;
		this.directory = directory;
		support.firePropertyChange("directory", oldDirectory, this.directory);
	}
	/**
	 * Return the log file prefix.
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * Set the log file prefix.
	 *
	 * @param prefix
	 *            The new log file prefix
	 */
	public void setPrefix(String prefix) {
		String oldPrefix = this.prefix;
		this.prefix = prefix;
		support.firePropertyChange("prefix", oldPrefix, this.prefix);
	}
	/**
	 * Return the log file suffix.
	 */
	public String getSuffix() {
		return suffix;
	}
	/**
	 * Set the log file suffix.
	 *
	 * @param suffix
	 *            The new log file suffix
	 */
	public void setSuffix(String suffix) {
		String oldSuffix = this.suffix;
		this.suffix = suffix;
		support.firePropertyChange("suffix", oldSuffix, this.suffix);
	}
	/**
	 * Return the timestamp flag.
	 */
	public boolean getTimestamp() {
		return timestamp;
	}
	/**
	 * Set the timestamp flag.
	 *
	 * @param timestamp
	 *            The new timestamp flag
	 */
	public void setTimestamp(boolean timestamp) {
		boolean oldTimestamp = this.timestamp;
		this.timestamp = timestamp;
		support.firePropertyChange
		("timestamp", new Boolean(oldTimestamp), new Boolean(this.timestamp));
	}
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
		// Construct the timestamp we will use, if requested
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		//toString:yyyy-mm-dd hh:mm:ss.fffffffff
		String tsString = ts.toString().substring(0, 19);
		String tsDate = tsString.substring(0, 10);
		// 日期发生变化时，关闭当前日志文件，打开一个新文件
		if (!date.equals(tsDate)) {
			synchronized (this) {
				if (!date.equals(tsDate)) {
					close();
					date = tsDate;
					open();
				}
			}
		}
		// Log this message, timestamped if necessary
		if (writer != null) {
			if (timestamp) {
				writer.println(tsString + " " + msg);
			} else {
				writer.println(msg);
			}
		}
	}
	// -------------------------------------------------------- Private Methods
	/**
	 * Close the currently open log file (if any)
	 */
	private void close() {
		if (writer == null)
			return;
		writer.flush();
		writer.close();
		writer = null;
		date = "";
	}
	/**
	 * Open the new log file for the date specified by <code>date</code>.
	 */
	private void open() {
		// Create the directory if necessary
		File dir = new File(directory);
		if (!dir.isAbsolute())
			dir = new File(System.getProperty("catalina.base"), directory);
		dir.mkdirs();
		// Open the current log file
		try {
			String pathname = dir.getAbsolutePath() + 
					File.separator + prefix + date + suffix;
			writer = new PrintWriter(new FileWriter(pathname, true), true);
		} catch (IOException e) {
			writer = null;
		}
	}
	// ------------------------------------------------------ Lifecycle Methods
	/**
	 * Add a lifecycle event listener to this component.
	 *
	 * @param listener
	 *            The listener to add
	 */
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycle.addLifecycleListener(listener);
	}
	/**
	 * Get the lifecycle listeners associated with this lifecycle. If this
	 * Lifecycle has no listeners registered, a zero-length array is returned.
	 */
	public LifecycleListener[] findLifecycleListeners() {
		return lifecycle.findLifecycleListeners();
	}
	/**
	 * Remove a lifecycle event listener from this component.
	 *
	 * @param listener
	 *            The listener to add
	 */
	public void removeLifecycleListener(LifecycleListener listener) {
		lifecycle.removeLifecycleListener(listener);
	}
	/**
	 * 启动文件日志记录器时触发生命周期事件
	 */
	public void start() throws LifecycleException {
		if (started)
		  throw new LifecycleException(sm.getString("fileLogger.alreadyStarted"));
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		started = true;
	}
	/**
	 * 关闭文件日志记录器时触发生命周期事件
	 */
	public void stop() throws LifecycleException {
		// Validate and update our current component state
		if (!started)
			throw new LifecycleException(sm.getString("fileLogger.notStarted"));
		lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		started = false;
		close();
	}
}