package com.pdc.tomcat.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.catalina.util.RequestUtil;
import org.apache.tomcat.util.res.StringManager;

import com.pdc.tomcat.ServletProcessor;
import com.pdc.tomcat.StaticResourceProcessor;

public class HttpProcessor<K, V> {

	private HttpConnector connector = null;
	private HttpRequest request;
	private HttpResponse response;
	private HttpRequestLine requestLine = new HttpRequestLine();
	protected String method = null;
	protected String queryString = null;
	// 获取当前包下的StringManager
	protected StringManager sm = StringManager.getManager("ex03.pyrmont.connector.http");

	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}

	// 创建HttpRequest和HttpResponse对象
	// 解析HTTP请求的第一行内容和请求头信息，填充HttpRequest对象
	// 以HttpRequest和HttpResponse为参数
	// 调用servletProcessor或StaticResouceProcessor的process方法
	public void process(Socket socket) {
		SocketInputStream input = null;
		OutputStream output = null;
		try {
			input = new SocketInputStream(socket.getInputStream(), 2048);
			output = socket.getOutputStream();
			// 创建并解析HttpRequest
			request = new HttpRequest(input);
			// 创建HttpResponse对象
			response = new HttpResponse(output);
			response.setRequest(request);
			response.setHeader("Server", "pdc Servlet Container");// 响应头
			// 填充HttpRequest对象
			parseRequest(input, output);// 解析请求行
			parseHeaders(input);// 解析请求头
			// 分发请求
			if (request.getRequestURI().startsWith("/servlet/")) {
				ServletProcessor<K,V> processor = new ServletProcessor<>();
				processor.process(request, response);
			} else {
				StaticResourceProcessor processor = new StaticResourceProcessor();
				processor.process(request, response);
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 解析请求行 GET /myApp/ModernServlet?userName=pdc&password=123 HTTP/1.1
	private void parseRequest(SocketInputStream input, OutputStream output) throws IOException, ServletException {
		// 使用SocketInputStream对象中的信息填充HttpRequestLine对象
		input.readRequestLine(requestLine);
		// 获取请求方法、uri和请求协议的版本信息
		String method = new String(requestLine.method, 0, requestLine.methodEnd);
		String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);
		String uri = null;
		if (method.length() < 1) {
			throw new ServletException("Missing HTTP request method");
		} else if (requestLine.uriEnd < 1) {
			throw new ServletException("Missing HTTP request URI");
		}
		// 解析查询参数
		int question = requestLine.indexOf("?");
		if (question >= 0) {
			request.setQueryString(new String(requestLine.uri, question + 1, requestLine.uriEnd - question - 1));
			uri = new String(requestLine.uri, 0, question);
		} else {
			request.setQueryString(null);
			uri = new String(requestLine.uri, 0, requestLine.uriEnd);
		}
		// 绝对路径判断
		// 如：http://www.pdc.com/index.html?name=pdc
		if (!uri.startsWith("/")) {
			int pos = uri.indexOf("://");
			// 解析协议和主机名
			if (pos != -1) {
				pos = uri.indexOf('/', pos + 3);
				if (pos == -1) {
					uri = "";
				} else {
					uri = uri.substring(pos);// index.html?name=pdc
				}
			}
		}
		// 判断是否有jsessionid，若有，则解析并填充入HttpRequest实例
		String match = ";jsessionid=";
		int semicolon = uri.indexOf(match);// ;
		if (semicolon >= 0) {
			String rest = uri.substring(semicolon + match.length());// jsessionid的value
			int semicolon2 = rest.indexOf(';');// 下一个;
			if (semicolon2 >= 0) {
				request.setRequestedSessionId(rest.substring(0, semicolon2));
				rest = rest.substring(semicolon2);// jsessionid后面的参数
			} else {
				request.setRequestedSessionId(rest);
				rest = "";
			}
			request.setRequestedSessionURL(true);
			uri = uri.substring(0, semicolon) + rest;
		} else {
			request.setRequestedSessionId(null);
			request.setRequestedSessionURL(false);
		}

		// 修正uri
		String normalizedUri = normalize(uri);

		// Set the corresponding request properties
		((HttpRequest) request).setMethod(method);
		request.setProtocol(protocol);
		if (normalizedUri != null) {
			((HttpRequest) request).setRequestURI(normalizedUri);
		} else {
			((HttpRequest) request).setRequestURI(uri);
		}

		if (normalizedUri == null) {
			throw new ServletException("Invalid URI: " + uri + "'");
		}
	}

	/**
	 * This method is the simplified version of the similar method in
	 * org.apache.catalina.connector.http.HttpProcessor. However, this method
	 * only parses some "easy" headers, such as "cookie", "content-length", and
	 * "content-type", and ignore other headers.
	 * 解析请求头简化版本，只能解析如cookie、content-length和content-type
	 * 
	 * @param input
	 *            The input stream connected to our socket
	 *
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a parsing error occurs
	 */
	private void parseHeaders(SocketInputStream input) throws IOException, ServletException {
		while (true) {
			HttpHeader header = new HttpHeader();
			// 如果有请求头信息可以读取，readHeader方法会相应地填充HttpHeader对象
			// 如果没有请求头信息可以读取，则nameEnd和valueEnd都为0
			input.readHeader(header);
			if (header.getNameEnd() == 0) {
				if (header.valueEnd == 0) {
					return;
				} else {
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.colon"));
				}
			}
			String name = new String(header.getName(), 0, header.getNameEnd());
			String value = new String(header.value, 0, header.valueEnd);
			// 添加到HttpRequest的HashMap请求头中:HashMap<Object, ArrayList<String>>
			// 即一个key可能有多个value
			request.addHeader(name, value);
			// 属性设置信息
			if (name.equals("cookie")) {// Cookie: userName=pdc; password=123;
				Cookie cookies[] = RequestUtil.parseCookieHeader(value);// 解析cookie
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals("jsessionid")) {
						// 覆盖URL中请求的任何内容
						if (!request.isRequestedSessionIdFromCookie()) {
							// 只接受第一个会话ID cookie
							request.setRequestedSessionId(cookies[i].getValue());
							request.setRequestedSessionCookie(true);
							request.setRequestedSessionURL(false);
						}
					}
					request.addCookie(cookies[i]);// ArrayList
				}
			} else if (name.equals("content-length")) {
				int n = -1;
				try {
					n = Integer.parseInt(value);
				} catch (Exception e) {
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
				}
				request.setContentLength(n);
			} else if (name.equals("content-type")) {
				request.setContentType(value);
			}
		}
	}

	// 标准化uri
	protected String normalize(String path) {
		if (path == null)
			return null;
		// Create a place for the normalized path
		String normalized = path;

		// Normalize "/%7E" and "/%7e" at the beginning to "/~"
		if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
			normalized = "/~" + normalized.substring(4);

		// Prevent encoding '%', '/', '.' and '\', which are special reserved
		// characters
		if ((normalized.indexOf("%25") >= 0) || (normalized.indexOf("%2F") >= 0) || (normalized.indexOf("%2E") >= 0)
				|| (normalized.indexOf("%5C") >= 0) || (normalized.indexOf("%2f") >= 0)
				|| (normalized.indexOf("%2e") >= 0) || (normalized.indexOf("%5c") >= 0)) {
			return null;
		}

		if (normalized.equals("/."))
			return "/";

		// Normalize the slashes and add leading slash if necessary
		if (normalized.indexOf('\\') >= 0)
			normalized = normalized.replace('\\', '/');
		if (!normalized.startsWith("/"))
			normalized = "/" + normalized;

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) + normalized.substring(index + 1);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) + normalized.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0)
				break;
			if (index == 0)
				return (null); // Trying to go outside our context
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
		}

		// Declare occurrences of "/..." (three or more dots) to be invalid
		// (on some Windows platforms this walks the directory tree!!!)
		if (normalized.indexOf("/...") >= 0)
			return (null);

		// Return the normalized path that we have completed
		return (normalized);

	}

}
