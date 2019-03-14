/* explains Tomcat's default container */
package com.pdc.tomcat.startup;

import com.pdc.tomcat.core.SimpleContainer;
import org.apache.catalina.connector.http.HttpConnector;

public final class Bootstrap {
	public static void main(String[] args) {
	​    HttpConnector connector = new HttpConnector();
	​    SimpleContainer container = new SimpleContainer();
	​    connector.setContainer(container);
	​    try {
	​      connector.initialize();//创建ServerSocket
	​      connector.start();//创建初始数量的HttpProcessor和启动线程
	      // make the application wait until we press any key.
	      System.in.read();
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
  }
}