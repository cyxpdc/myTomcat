package org.apache.catalina.startup;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class HostRuleSet extends RuleSetBase {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The matching pattern prefix to use for recognizing our elements.
	 */
	protected String prefix = null;

	// ------------------------------------------------------------ Constructor

	/**
	 * Construct an instance of this <code>RuleSet</code> with the default
	 * matching pattern prefix.
	 */
	public HostRuleSet() {
		this("");
	}

	/**
	 * Construct an instance of this <code>RuleSet</code> with the specified
	 * matching pattern prefix.
	 *
	 * @param prefix
	 *            Prefix for matching pattern rules (including the trailing
	 *            slash character)
	 */
	public HostRuleSet(String prefix) {
		super();
		this.namespaceURI = null;
		this.prefix = prefix;
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * <p>
	 * Add the set of Rule instances defined in this RuleSet to the specified
	 * <code>Digester</code> instance, associating them with our namespace URI
	 * (if any). This method should only be called by a Digester instance.
	 * </p>
	 *
	 * @param digester
	 *            Digester instance to which the new Rule instances should be
	 *            added.
	 */
	public void addRuleInstances(Digester digester) {

		digester.addObjectCreate(prefix + "Host", "org.apache.catalina.core.StandardHost", "className");
		digester.addSetProperties(prefix + "Host");
		digester.addRule(prefix + "Host", new CopyParentClassLoaderRule(digester));
		digester.addRule(prefix + "Host",//添加HostConfig到Host实例中
				new LifecycleListenerRule(digester, "org.apache.catalina.startup.HostConfig", "hostConfigClass"));
		digester.addSetNext(prefix + "Host", "addChild", "org.apache.catalina.Container");

		digester.addCallMethod(prefix + "Host/Alias", "addAlias", 0);

		digester.addObjectCreate(prefix + "Host/Cluster", null, // MUST be
																// specified in
																// the element
				"className");
		digester.addSetProperties(prefix + "Host/Cluster");
		digester.addSetNext(prefix + "Host/Cluster", "addCluster", "org.apache.catalina.Cluster");

		digester.addObjectCreate(prefix + "Host/Listener", null, // MUST be
																	// specified
																	// in the
																	// element
				"className");
		digester.addSetProperties(prefix + "Host/Listener");
		digester.addSetNext(prefix + "Host/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");

		digester.addObjectCreate(prefix + "Host/Logger", null, // MUST be
																// specified in
																// the element
				"className");
		digester.addSetProperties(prefix + "Host/Logger");
		digester.addSetNext(prefix + "Host/Logger", "setLogger", "org.apache.catalina.Logger");

		digester.addObjectCreate(prefix + "Host/Realm", null, // MUST be
																// specified in
																// the element
				"className");
		digester.addSetProperties(prefix + "Host/Realm");
		digester.addSetNext(prefix + "Host/Realm", "setRealm", "org.apache.catalina.Realm");

		digester.addObjectCreate(prefix + "Host/Valve", null, // MUST be
																// specified in
																// the element
				"className");
		digester.addSetProperties(prefix + "Host/Valve");
		digester.addSetNext(prefix + "Host/Valve", "addValve", "org.apache.catalina.Valve");

	}
}
