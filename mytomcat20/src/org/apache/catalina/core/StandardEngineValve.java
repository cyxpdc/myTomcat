package org.apache.catalina.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Host;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.ValveContext;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;

/**
 * Valve that implements the default basic behavior for the
 * <code>StandardEngine</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>: This implementation is likely to be useful only when
 * processing HTTP requests.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.7 $ $Date: 2001/11/10 01:24:20 $
 */

final class StandardEngineValve extends ValveBase {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The descriptive information related to this implementation.
	 */
	private static final String info = "org.apache.catalina.core.StandardEngineValve/1.0";

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
	 * Select the appropriate child Host to process this request, based on the
	 * requested server name. If no matching Host can be found, return an
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

		// Validate that any HTTP/1.1 request included a host header
		HttpServletRequest hrequest = (HttpServletRequest) request;
		if ("HTTP/1.1".equals(hrequest.getProtocol()) && (hrequest.getServerName() == null)) {
			((HttpServletResponse) response.getResponse()).sendError(HttpServletResponse.SC_BAD_REQUEST,
					sm.getString("standardEngine.noHostHeader", request.getRequest().getServerName()));
			return;
		}

		// Select the Host to be used for this Request
		StandardEngine engine = (StandardEngine) getContainer();
		Host host = (Host) engine.map(request, true);
		if (host == null) {
			((HttpServletResponse) response.getResponse()).sendError(HttpServletResponse.SC_BAD_REQUEST,
					sm.getString("standardEngine.noHost", request.getRequest().getServerName()));
			return;
		}

		// Ask this Host to process this request
		host.invoke(request, response);

	}

}
