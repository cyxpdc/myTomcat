package ex05.pyrmont.core;

import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.Container;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Mapper;
import org.apache.catalina.Request;
import org.apache.catalina.Wrapper;

public class SimpleContextMapper implements Mapper {

	/**
	 * The Container with which this Mapper is associated.
	 */
	private SimpleContext context = null;

	public Container getContainer() {
		return (context);
	}

	public void setContainer(Container container) {
		if (!(container instanceof SimpleContext))
			throw new IllegalArgumentException("Illegal type of container");
		context = (SimpleContext) container;
	}

	public String getProtocol() {
		return null;
	}

	public void setProtocol(String protocol) {
	}

	/**
	 * 返回一个子容器(即Wrapper类实例)来处理请求:
	 * 从request对象中解析出请求的上下文路径
	 * 并调用Context实例的findServletMapping来获取一个与该路径相关联的名称
	 * 如果找到了这个名称，则它调用Context实例的findChild获取一个Wrapper实例
	 * 第二个参数暂时忽略
	 */
	public Container map(Request request, boolean update) {
		// Identify the context-relative URI to be mapped
		String contextPath = 
				((HttpServletRequest) request.getRequest()).getContextPath();
		String requestURI = ((HttpRequest) request).getDecodedRequestURI();
		String servletPath = requestURI.substring(contextPath.length());
		Wrapper wrapper = null;
		String name = context.findServletMapping(servletPath);
		if (name != null)
			wrapper = (Wrapper) context.findChild(name);
		return (wrapper);
	}
}