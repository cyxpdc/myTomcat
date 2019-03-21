package org.apache.catalina.session;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * Facade for the StandardSession object.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.1 $ $Date: 2001/05/14 04:07:55 $
 */
public class StandardSessionFacade implements HttpSession {

	// ----------------------------------------------------------- Constructors

	/**
	 * Construct a new session facade.
	 */
	public StandardSessionFacade(StandardSession session) {
		super();
		this.session = (HttpSession) session;
	}

	/**
	 * Construct a new session facade.
	 */
	public StandardSessionFacade(HttpSession session) {
		super();
		this.session = session;
	}

	// ----------------------------------------------------- Instance Variables

	/**
	 * Wrapped session object.
	 */
	private HttpSession session = null;

	// ---------------------------------------------------- HttpSession Methods

	public long getCreationTime() {
		return session.getCreationTime();
	}

	public String getId() {
		return session.getId();
	}

	public long getLastAccessedTime() {
		return session.getLastAccessedTime();
	}

	public ServletContext getServletContext() {
		// FIXME : Facade this object ?
		return session.getServletContext();
	}

	public void setMaxInactiveInterval(int interval) {
		session.setMaxInactiveInterval(interval);
	}

	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	public HttpSessionContext getSessionContext() {
		return session.getSessionContext();
	}

	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}

	public Object getValue(String name) {
		return session.getAttribute(name);
	}

	public Enumeration getAttributeNames() {
		return session.getAttributeNames();
	}

	public String[] getValueNames() {
		return session.getValueNames();
	}

	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}

	public void putValue(String name, Object value) {
		session.setAttribute(name, value);
	}

	public void removeAttribute(String name) {
		session.removeAttribute(name);
	}

	public void removeValue(String name) {
		session.removeAttribute(name);
	}

	public void invalidate() {
		session.invalidate();
	}

	public boolean isNew() {
		return session.isNew();
	}

}
