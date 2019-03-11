package com.pdc.tomcat;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
//发出请求:http://localhost:8080/index.html
//关闭服务器:http://localhost:8080/SHUTDOWN
public class HttpServer {
	//webroot目录，存放静态资源
	//System.getProperty("user.dir"):获取用户的当前工作目录
	//File.separator:当前系统的路径分隔符,windows下为\
	public static final String WEB_ROOT = 
							System.getProperty("user.dir") + File.separator + "webroot";
	//关闭服务器命令
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	//命令是否被接收
	private boolean shutdown = false;
	//启动
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		HttpServer server = new HttpServer();
		server.await();
	}
	//阻塞等待
	public void await() {
		//创建ServerSocker
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		//循环等待请求
		while (!shutdown) {//如果没有关闭服务器，则无限循环
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
				response.sendStaticResource();//发送一个静态资源到浏览器
				//关闭socket
				socket.close();
				//测试HTTP请求的uri是否是关闭的命令，若是，则会退出while循环
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}