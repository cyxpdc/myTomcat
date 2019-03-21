package com.pdc.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import javax.servlet.Servlet;

import com.pdc.tomcat.connector.http.Constants;
import com.pdc.tomcat.connector.http.HttpRequest;
import com.pdc.tomcat.connector.http.HttpRequestFacade;
import com.pdc.tomcat.connector.http.HttpResponse;
import com.pdc.tomcat.connector.http.HttpResponseFacade;

public class ServletProcessor<K, V> {

	public void process(HttpRequest<K, V> request, HttpResponse response) {

		String uri = request.getRequestURI();
		String servletName = uri.substring(uri.lastIndexOf("/") + 1);
		URLClassLoader loader = null;
		try {
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(Constants.WEB_ROOT);
			String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
			urls[0] = new URL(null, repository, streamHandler);
			loader = new URLClassLoader(urls);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		Class<?> myClass = null;
		try {
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
		}
		Servlet servlet = null;
		try {
			servlet = (Servlet) myClass.newInstance();
			HttpRequestFacade requestFacade = new HttpRequestFacade(request);
			HttpResponseFacade responseFacade = new HttpResponseFacade(response);
			servlet.service(requestFacade, responseFacade);
			((HttpResponse) response).finishResponse();
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}
	}
}