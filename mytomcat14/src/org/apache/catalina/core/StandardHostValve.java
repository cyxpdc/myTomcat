package org.apache.catalina.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Manager;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Session;
import org.apache.catalina.ValveContext;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;

/**
 * Valve that implements the default basic behavior for the
 * <code>StandardHost</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>: This implementation is likely to be useful only when
 * processing HTTP requests.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.6 $ $Date: 2002/01/04 16:33:40 $
 */

final class StandardHostValve extends ValveBase {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The descriptive information related to this implementation.
	 */
	private static final String info = "org.apache.catalina.core.StandardHostValve/1.0";

	/**
	 * The string manager for this package.
	 */
	private static final StringManager sm = StringManager.getManager(Constants.Package);

	// ------------------------------------------------------------- Properties

	/**
	 * Return descriptive information about this Valve implementation.
	 */
	public String getInfo() {

		return (info);

	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Select the appropriate child Context to process this request, based on
	 * the specified request URI. If no matching Context can be found, return an
	 * appropriate HTTP error.
	 *
	 * @param request
	 *            Request to be processed
	 * @param response
	 *            Response to be produced
	 * @param valveContext
	 *            Valve context used to forward to the next Valve
	 *
	 * @exception IOException
	 *                if an input/output error occurred
	 * @exception ServletException
	 *                if a servlet error occurred
	 */
	public void invoke(Request request, Response response, ValveContext valveContext)
			throws IOException, ServletException {
		// Validate the request and response object types
		if (!(request.getRequest() instanceof HttpServletRequest)
				|| !(response.getResponse() instanceof HttpServletResponse)) {
			return; // NOTE - Not much else we can do generically
		}
		// Select the Context to be used for this Request
		StandardHost host = (StandardHost) getContainer();
		Context context = (Context) host.map(request, true);
		if (context == null) {
			((HttpServletResponse) response.getResponse()).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					sm.getString("standardHost.noContext"));
			return;
		}
		// Bind the context CL to the current thread
		Thread.currentThread().setContextClassLoader(context.getLoader().getClassLoader());
		// Update the session last access time for our session (if any)
		HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
		String sessionId = hreq.getRequestedSessionId();
		if (sessionId != null) {
			Manager manager = context.getManager();
			if (manager != null) {
				Session session = manager.findSession(sessionId);
				if ((session != null) && session.isValid())
					session.access();
			}
		}
		// Ask this Context to process this request
		context.invoke(request, response);
	}

}
