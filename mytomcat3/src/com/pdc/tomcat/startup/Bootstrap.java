package com.pdc.tomcat.startup;

import com.pdc.tomcat.connector.http.HttpConnector;

//启动类，入口
public final class Bootstrap {
	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();
		connector.start();
	}
}