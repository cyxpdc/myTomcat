package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.security.Principal;
import java.security.cert.X509Certificate;

/**
 * A <b>Realm</b> is a read-only facade for an underlying security realm used to
 * authenticate individual users, and identify the security roles associated
 * with those users. Realms can be attached at any Container level, but will
 * typically only be attached to a Context, or higher level, Container.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.4 $ $Date: 2001/07/30 20:04:04 $
 */

public interface Realm {

	// ------------------------------------------------------------- Properties

	/**
	 * Return the Container with which this Realm has been associated.
	 */
	public Container getContainer();

	/**
	 * Set the Container with which this Realm has been associated.
	 *
	 * @param container
	 *            The associated Container
	 */
	public void setContainer(Container container);

	/**
	 * Return descriptive information about this Realm implementation and the
	 * corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo();

	// --------------------------------------------------------- Public Methods

	/**
	 * Add a property change listener to this component.
	 *
	 * @param listener
	 *            The listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Return the Principal associated with the specified username and
	 * credentials, if there is one; otherwise return <code>null</code>.
	 *
	 * @param username
	 *            Username of the Principal to look up
	 * @param credentials
	 *            Password or other credentials to use in authenticating this
	 *            username
	 */
	public Principal authenticate(String username, String credentials);

	/**
	 * Return the Principal associated with the specified username and
	 * credentials, if there is one; otherwise return <code>null</code>.
	 *
	 * @param username
	 *            Username of the Principal to look up
	 * @param credentials
	 *            Password or other credentials to use in authenticating this
	 *            username
	 */
	public Principal authenticate(String username, byte[] credentials);

	/**
	 * Return the Principal associated with the specified username, which
	 * matches the digest calculated using the given parameters using the method
	 * described in RFC 2069; otherwise return <code>null</code>.
	 *
	 * @param username
	 *            Username of the Principal to look up
	 * @param digest
	 *            Digest which has been submitted by the client
	 * @param nonce
	 *            Unique (or supposedly unique) token which has been used for
	 *            this request
	 * @param realm
	 *            Realm name
	 * @param md5a2
	 *            Second MD5 digest used to calculate the digest : MD5(Method +
	 *            ":" + uri)
	 */
	public Principal authenticate(String username, String digest, String nonce, String nc, String cnonce, String qop,
			String realm, String md5a2);

	/**
	 * Return the Principal associated with the specified chain of X509 client
	 * certificates. If there is none, return <code>null</code>.
	 *
	 * @param certs
	 *            Array of client certificates, with the first one in the array
	 *            being the certificate of the client itself.
	 */
	public Principal authenticate(X509Certificate certs[]);

	/**
	 * Return <code>true</code> if the specified Principal has the specified
	 * security role, within the context of this Realm; otherwise return
	 * <code>false</code>.
	 *
	 * @param principal
	 *            Principal for whom the role is to be checked
	 * @param role
	 *            Security role to be checked
	 */
	public boolean hasRole(Principal principal, String role);

	/**
	 * Remove a property change listener from this component.
	 *
	 * @param listener
	 *            The listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

}
