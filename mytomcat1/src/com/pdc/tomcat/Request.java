package com.pdc.tomcat;

import java.io.InputStream;
import java.io.IOException;
//本质为String
public class Request {

	private InputStream input = null;//通过InputStream来创建Request对象
	private String uri = null;//保存请求的uri

	public Request(InputStream input) {
		this.input = input;
	}
	//将请求头解析为一个String赋值给request
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
}