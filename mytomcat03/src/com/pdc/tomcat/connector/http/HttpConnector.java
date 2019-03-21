package com.pdc.tomcat.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//等待Http请求
public class HttpConnector implements Runnable {

	boolean stopped;
	private String scheme = "http";

	public String getScheme() {
		return scheme;
	}

	public void run() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!stopped) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();// 等待请求
			} catch (Exception e) {
				continue;
			}
			// 创建Request和Response对象
			HttpProcessor processor = new HttpProcessor(this);
			processor.process(socket);
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
}