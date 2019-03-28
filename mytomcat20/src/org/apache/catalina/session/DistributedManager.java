package org.apache.catalina.session;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.Session;
import org.apache.catalina.cluster.ClusterSender;
import org.apache.catalina.cluster.ClusterReceiver;
import org.apache.catalina.cluster.ReplicationWrapper;
import org.apache.catalina.util.CustomObjectInputStream;

/**
 * This manager is responsible for in memory replication of Sessions across a
 * defined Cluster. It could also utilize a Store to make Sessions persistence.
 *
 * @author Bip Thelin
 * @version $Revision: 1.5 $, $Date: 2002/01/03 08:52:57 $
 */

public final class DistributedManager extends PersistentManagerBase {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The descriptive information about this implementation.
	 */
	private static final String info = "DistributedManager/1.0";

	/**
	 * The descriptive name of this Manager implementation (for logging).
	 */
	protected static String name = "DistributedManager";

	/**
	 * Our ClusterSender, used when replicating sessions
	 */
	private ClusterSender clusterSender = null;

	/**
	 * Our ClusterReceiver
	 */
	private ClusterReceiver clusterReceiver = null;

	// ------------------------------------------------------------- Properties

	/**
	 * Return descriptive information about this Manager implementation and the
	 * corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo() {
		return (this.info);
	}

	/**
	 * Return the descriptive short name of this Manager implementation.
	 */
	public String getName() {
		return (this.name);
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Create a Session and replicate it in our Cluster
	 *
	 * @return The newly created Session
	 */
	public Session createSession() {
		Session session = super.createSession();
		ObjectOutputStream oos = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(new BufferedOutputStream(bos));
			((StandardSession) session).writeObjectData(oos);
			oos.close();
			byte[] obs = bos.toByteArray();
			clusterSender.send(obs);
			if (debug > 0)
				log("Replicating Session: " + session.getId());
		} catch (IOException e) {
			log("An error occurred when replicating Session: " + session.getId());
		}
		return session;
	}

	/**
	 * Start this manager
	 *
	 * @exception LifecycleException
	 *                if an error occurs
	 */
	public void start() throws LifecycleException {
		Container container = getContainer();
		Cluster cluster = null;

		if (container != null)
			cluster = container.getCluster();

		if (cluster != null) {
			this.clusterSender = cluster.getClusterSender(getName());
			this.clusterReceiver = cluster.getClusterReceiver(getName());
		}

		super.start();
	}

	/**
	 * Called from our background thread to process new received Sessions
	 *
	 */
	public void processClusterReceiver() {
		Object[] objs = clusterReceiver.getObjects();
		StandardSession _session = null;
		ByteArrayInputStream bis = null;
		Loader loader = null;
		ClassLoader classLoader = null;
		ObjectInputStream ois = null;
		byte[] buf = new byte[5000];
		ReplicationWrapper repObj = null;

		for (int i = 0; i < objs.length; i++) {
			try {
				bis = new ByteArrayInputStream(buf);
				repObj = (ReplicationWrapper) objs[i];
				buf = repObj.getDataStream();
				bis = new ByteArrayInputStream(buf, 0, buf.length);

				if (container != null)
					loader = container.getLoader();

				if (loader != null)
					classLoader = loader.getClassLoader();

				if (classLoader != null)
					ois = new CustomObjectInputStream(bis, classLoader);
				else
					ois = new ObjectInputStream(bis);

				_session = (StandardSession) super.createSession();
				_session.readObjectData(ois);
				_session.setManager(this);

				if (debug > 0)
					log("Loading replicated session: " + _session.getId());
			} catch (IOException e) {
				log("Error occurred when trying to read replicated session: " + e.toString());
			} catch (ClassNotFoundException e) {
				log("Error occurred when trying to read replicated session: " + e.toString());
			} finally {
				if (ois != null) {
					try {
						ois.close();
						bis = null;
					} catch (IOException e) {
						;
					}
				}
			}
		}
	}

	/**
	 * The background thread that checks for session timeouts and shutdown.
	 */
	public void run() {
		// Loop until the termination semaphore is set
		while (!threadDone) {
			threadSleep();
			processClusterReceiver();
			processExpires();
			processPersistenceChecks();
		}
	}
}
