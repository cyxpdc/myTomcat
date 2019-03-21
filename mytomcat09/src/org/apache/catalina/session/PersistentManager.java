package org.apache.catalina.session;

/**
 * Implementation of the <b>Manager</b> interface that makes use of a Store to
 * swap active Sessions to disk. It can be configured to achieve several
 * different goals:
 *
 * <li>Persist sessions across restarts of the Container</li>
 * <li>Fault tolerance, keep sessions backed up on disk to allow recovery in the
 * event of unplanned restarts.</li>
 * <li>Limit the number of active sessions kept in memory by swapping less
 * active sessions out to disk.</li>
 *
 * @version $Revision: 1.10 $
 * @author Kief Morris (kief@kief.com)
 */

public final class PersistentManager extends PersistentManagerBase {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The descriptive information about this implementation.
	 */
	private static final String info = "PersistentManager/1.0";

	/**
	 * The descriptive name of this Manager implementation (for logging).
	 */
	protected static String name = "PersistentManager";

	// ------------------------------------------------------------- Properties

	/**
	 * Return descriptive information about this Manager implementation and the
	 * corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo() {
		return PersistentManager.info;
	}

	/**
	 * Return the descriptive short name of this Manager implementation.
	 */
	public String getName() {
		return name;
	}
}
