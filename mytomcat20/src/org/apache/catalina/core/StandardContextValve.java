package org.apache.catalina.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;

/**
 * Valve that implements the default basic behavior for the
 * <code>StandardContext</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>: This implementation is likely to be useful only when
 * processing HTTP requests.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.16 $ $Date: 2002/03/14 20:58:24 $
 */

final class StandardContextValve extends ValveBase {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The descriptive information related to this implementation.
	 */
	private static final String info = "org.apache.catalina.core.StandardContextValve/1.0";

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
	 * Select the appropriate child Wrapper to process this request, based on
	 * the specified request URI. If no matching Wrapper can be found, return an
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
		// Disallow any direct access to resources under WEB-INF or META-INF
		HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
		String contextPath = hreq.getContextPath();
		String requestURI = ((HttpRequest) request).getDecodedRequestURI();
		String relativeURI = requestURI.substring(contextPath.length()).toUpperCase();
		if (relativeURI.equals("/META-INF") || relativeURI.equals("/WEB-INF") || relativeURI.startsWith("/META-INF/")
				|| relativeURI.startsWith("/WEB-INF/")) {
			notFound(requestURI, (HttpServletResponse) response.getResponse());
			return;
		}

		Context context = (Context) getContainer();
		// Select the Wrapper to be used for this Request
		Wrapper wrapper = null;
		try {
			wrapper = (Wrapper) context.map(request, true);
		} catch (IllegalArgumentException e) {
			badRequest(requestURI, (HttpServletResponse) response.getResponse());
			return;
		}
		if (wrapper == null) {
			notFound(requestURI, (HttpServletResponse) response.getResponse());
			return;
		}

		// Ask this Wrapper to process this Request
		response.setContext(context);

		wrapper.invoke(request, response);

	}

	// -------------------------------------------------------- Private Methods

	/**
	 * Report a "bad request" error for the specified resource. FIXME: We should
	 * really be using the error reporting settings for this web application,
	 * but currently that code runs at the wrapper level rather than the context
	 * level.
	 *
	 * @param requestURI
	 *            The request URI for the requested resource
	 * @param response
	 *            The response we are creating
	 */
	private void badRequest(String requestURI, HttpServletResponse response) {

		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
		} catch (IllegalStateException e) {
			;
		} catch (IOException e) {
			;
		}

	}

	/**
	 * Report a "not found" error for the specified resource. FIXME: We should
	 * really be using the error reporting settings for this web application,
	 * but currently that code runs at the wrapper level rather than the context
	 * level.
	 *
	 * @param requestURI
	 *            The request URI for the requested resource
	 * @param response
	 *            The response we are creating
	 */
	private void notFound(String requestURI, HttpServletResponse response) {

		try {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
		} catch (IllegalStateException e) {
			;
		} catch (IOException e) {
			;
		}

	}

}
