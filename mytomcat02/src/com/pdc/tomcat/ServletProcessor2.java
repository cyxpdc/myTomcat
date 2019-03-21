package com.pdc.tomcat;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
//localhost:8080/servelt/servletName
public class ServletProcessor2 {

	public void process(Request request, Response response) {
		String uri = request.getUri();
		String servletName = uri.substring(uri.lastIndexOf("/") + 1);
		URLClassLoader urlClassLoader = null;
		try {//创建URLClassLoader对象
			//生成仓库后会调用org.apache.catalina.startup.ClassLoaderFactory类的createClassLoader()
			File classPath = new File(Constants.WEB_ROOT);//webroot
			String repository = (//webroot\
					new URL("file", null, classPath.getCanonicalPath() + File.separator)
					).toString();
			System.out.println("Repository:" + repository);
			//生成URL对象后会调用org.apache.catalina.loader.StandardClassLoader类的addRepository()方法
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;//用来确保调用指定得构造方法
			urls[0] = new URL(null, repository, streamHandler);
			urlClassLoader = new URLClassLoader(urls);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		Class<?> myClass = null;
		try {
			myClass = urlClassLoader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
		}
		Servlet servlet = null;
		RequestFacade requestFacade = new RequestFacade(request);
		ResponseFacade responseFacade = new ResponseFacade(response);
		try {
			servlet = (Servlet) myClass.newInstance();
			servlet.service((ServletRequest) requestFacade, (ServletResponse) responseFacade);
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}
	}
}