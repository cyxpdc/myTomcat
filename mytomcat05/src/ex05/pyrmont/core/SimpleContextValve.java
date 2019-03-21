package ex05.pyrmont.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Wrapper;
/**
 * Context的基础阀
 * @author Administrator
 *
 */
public class SimpleContextValve implements Valve, Contained {

	protected Container container;

	public void invoke(Request request, Response response, ValveContext valveContext)
			throws IOException, ServletException {
		if (!(request.getRequest() instanceof HttpServletRequest)
				|| !(response.getResponse() instanceof HttpServletResponse)) {
			return ;
		}
		// Disallow any direct access to resources under WEB-INF or META-INF
		String requestURI = ((HttpRequest) request).getDecodedRequestURI();
		
		Context context = (Context) getContainer();
		//查找Wrapper实例
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
		//如果找到了Wrapper容器，则调用其invoke方法
		wrapper.invoke(request, response);
	}

	public String getInfo() {
		return null;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	private void badRequest(String requestURI, HttpServletResponse response) {
		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
		} catch (IllegalStateException e) {
			;
		} catch (IOException e) {
			;
		}
	}

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