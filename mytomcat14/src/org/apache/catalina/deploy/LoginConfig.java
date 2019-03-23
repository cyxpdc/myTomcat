package org.apache.catalina.deploy;

import org.apache.catalina.util.RequestUtil;

/**
 * Representation of a login configuration element for a web application, as
 * represented in a <code>&lt;login-config&gt;</code> element in the deployment
 * descriptor.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.6 $ $Date: 2001/07/22 20:25:10 $
 */

public final class LoginConfig {

	// ----------------------------------------------------------- Constructors

	/**
	 * Construct a new LoginConfig with default properties.
	 */
	public LoginConfig() {
	}

	/**
	 * Construct a new LoginConfig with the specified properties.
	 *
	 * @param authMethod
	 *            The authentication method
	 * @param realmName
	 *            The realm name
	 * @param loginPage
	 *            The login page URI
	 * @param errorPage
	 *            The error page URI
	 */
	public LoginConfig(String authMethod, String realmName, 
			String loginPage, String errorPage) {
		setAuthMethod(authMethod);
		setRealmName(realmName);
		setLoginPage(loginPage);
		setErrorPage(errorPage);
	}

	// ------------------------------------------------------------- Properties

	/**
	 * The authentication method to use for application login. Must be BASIC,
	 * DIGEST, FORM, or CLIENT-CERT.
	 */
	private String authMethod = null;

	public String getAuthMethod() {
		return this.authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	/**
	 * The context-relative URI of the error page for form login.
	 */
	private String errorPage = null;

	public String getErrorPage() {
		return (this.errorPage);
	}

	public void setErrorPage(String errorPage) {
		// if ((errorPage == null) || !errorPage.startsWith("/"))
		// throw new IllegalArgumentException
		// ("Error Page resource path must start with a '/'");
		this.errorPage = RequestUtil.URLDecode(errorPage);
	}

	/**
	 * The context-relative URI of the login page for form login.
	 */
	private String loginPage = null;

	public String getLoginPage() {
		return (this.loginPage);
	}

	public void setLoginPage(String loginPage) {
		// if ((loginPage == null) || !loginPage.startsWith("/"))
		// throw new IllegalArgumentException
		// ("Login Page resource path must start with a '/'");
		this.loginPage = RequestUtil.URLDecode(loginPage);
	}

	/**
	 * The realm name used when challenging the user for authentication
	 * credentials.
	 */
	private String realmName = null;

	public String getRealmName() {
		return (this.realmName);
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Return a String representation of this object.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer("LoginConfig[");
		sb.append("authMethod=");
		sb.append(authMethod);
		if (realmName != null) {
			sb.append(", realmName=");
			sb.append(realmName);
		}
		if (loginPage != null) {
			sb.append(", loginPage=");
			sb.append(loginPage);
		}
		if (errorPage != null) {
			sb.append(", errorPage=");
			sb.append(errorPage);
		}
		sb.append("]");
		return (sb.toString());

	}

}