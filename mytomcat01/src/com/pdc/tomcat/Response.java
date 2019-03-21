package com.pdc.tomcat;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class Response {

	private static final int BUFFER_SIZE = 1024;
	Request request;//request对象
	OutputStream output;//输出

	public Response(OutputStream output) {
		this.output = output;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
	//发送一个静态资源到浏览器
	public void sendStaticResource() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		try {
			//根据路径来获取文件
			File file = new File(HttpServer.WEB_ROOT, request.getUri());
			System.out.println("文件路径:"+file.getAbsolutePath());
			if (file.exists()) {
				fis = new FileInputStream(file);
				//原书代码的bug解决：https://blog.csdn.net/h820911469/article/details/54498297
				//这个地方对于静态文件发送的时候没有只是发送了文本内容，没有HTTP协议头，所以使用浏览器是无法解析的。
				//对于这个问题可以在发送文本文件之前加上HTTP头，方法如下五行：
				String headerMessage = 
								"HTTP/1.1 200 OK\r\n" + 
								"Content-Type:text/html\r\n" + 
								"\r\n";
				output.write(headerMessage.getBytes());
				int len = fis.read(bytes, 0, BUFFER_SIZE);
				while (len != -1) {
					output.write(bytes, 0, len);
					len = fis.read(bytes, 0, BUFFER_SIZE);
				}
			} else {
				//没有找到该文件
				String errorMessage = 
									"HTTP/1.1 404 File Not Found\r\n" + 
									"Content-Type: text/html\r\n"+ 
									"Content-Length: 23\r\n" + "\r\n" + 
									"<h1>File Not Found</h1>";
				output.write(errorMessage.getBytes());
			}
		} catch (Exception e) {
			//如果实例化文件对象失败，抛异常
			System.out.println(e.toString());
		} finally {
			if (fis != null)
				fis.close();
		}
	}
}