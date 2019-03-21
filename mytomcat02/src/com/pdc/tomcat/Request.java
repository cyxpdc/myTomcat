package com.pdc.tomcat;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
//比第一章的Request：
//1.实现了个ServletRequest
public class Request implements ServletRequest {

	private InputStream input = null;//通过InputStream来创建Request对象
	private String uri = null;//保存请求的uri

	public Request(InputStream input) {
		this.input = input;
	}
	//将请求解析为一个String赋值给request
	public void parse() {
		//保存inputStream的字符集合
		StringBuffer request = new StringBuffer(2048);
		int len;
		byte[] buffer = new byte[2048];
		try {
			len = input.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			len = -1;
		}
		for (int i = 0; i < len; i++) {
			request.append((char) buffer[i]);
		}
		System.out.print("Request:" + request.toString());
		uri = parseUri(request.toString());
	}
	//从请求行中获取URI，从两个空格中获取
	private String parseUri(String request) {
		int firstSpace, secondSpace;//第一、二个空格
		firstSpace = request.indexOf(' ');
		if (firstSpace != -1) {
			secondSpace = request.indexOf(' ', firstSpace + 1);
			if (secondSpace > firstSpace)
				return request.substring(firstSpace + 1, secondSpace);
		}
		return null;
	}
	//获取URI
	public String getUri() {
		return uri;
	}

	/* implementation of the ServletRequest */
	public Object getAttribute(String attribute) {
		return null;
	}

	public Enumeration<?> getAttributeNames() {
		return null;
	}

	public String getRealPath(String path) {
		return null;
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	public boolean isSecure() {
		return false;
	}

	public String getCharacterEncoding() {
		return null;
	}

	public int getContentLength() {
		return 0;
	}

	public String getContentType() {
		return null;
	}

	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	public Locale getLocale() {
		return null;
	}

	public Enumeration<?> getLocales() {
		return null;
	}

	public String getParameter(String name) {
		return null;
	}

	public Map<?, ?> getParameterMap() {
		return null;
	}

	public Enumeration<?> getParameterNames() {
		return null;
	}

	public String[] getParameterValues(String parameter) {
		return null;
	}

	public String getProtocol() {
		return null;
	}

	public BufferedReader getReader() throws IOException {
		return null;
	}

	public String getRemoteAddr() {
		return null;
	}

	public String getRemoteHost() {
		return null;
	}

	public String getScheme() {
		return null;
	}

	public String getServerName() {
		return null;
	}

	public int getServerPort() {
		return 0;
	}

	public void removeAttribute(String attribute) {
	}

	public void setAttribute(String key, Object value) {
	}

	public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
	}
}