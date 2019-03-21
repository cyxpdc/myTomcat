package com.pdc.tomcat;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
//比第一章的HttpServer：
//1.多了分发请求给servlet
//2.webroot提取到一个单独的类Constants
public class HttpServer2 {
	//关闭服务器命令
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	private boolean shutdown = false;

	public static void main(String[] args) {
		HttpServer1 server = new HttpServer1();
		server.await();
	}
	//一致等待HTTP请求，直到接受到关闭命令
	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		//循环等待请求
		while (!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				//创建Request对象并解析
				Request request = new Request(input);
				request.parse();
				//创建Response对象
				Response response = new Response(output);
				response.setRequest(request);
				//分发请求
				if (request.getUri().startsWith("/servlet/")) {
					ServletProcessor2 processor = new ServletProcessor2();
					processor.process(request, response);
				} else {
					StaticResourceProcessor processor = new StaticResourceProcessor();
					processor.process(request, response);
				}
				socket.close();
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}
