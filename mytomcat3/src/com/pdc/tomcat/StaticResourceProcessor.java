package com.pdc.tomcat;

import com.pdc.tomcat.connector.http.HttpRequest;
import com.pdc.tomcat.connector.http.HttpResponse;

import java.io.IOException;

public class StaticResourceProcessor {

	public void process(HttpRequest request, HttpResponse response) {
		try {
			response.sendStaticResource();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
