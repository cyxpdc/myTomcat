package com.pdc.tomcat;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;
//比第一章的Response：
//1.实现了ServletResponse
//2.多了getWriter()
public class Response implements ServletResponse {

	private static final int BUFFER_SIZE = 1024;
	Request request;
	OutputStream output;
	PrintWriter writer;

	public Response(OutputStream output) {
		this.output = output;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
	//发送静态资源
	public void sendStaticResource() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		try {
			File file = new File(Constants.WEB_ROOT, request.getUri());
			fis = new FileInputStream(file);
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
		} catch (FileNotFoundException e) {
			String errorMessage = 
						"HTTP/1.1 404 File Not Found\r\n" + 
						"Content-Type: text/html\r\n" + 
						"Content-Length: 23\r\n" + "\r\n" + 
						"<h1>File Not Found</h1>";
			output.write(errorMessage.getBytes());
		} finally {
			if (fis != null)
				fis.close();
		}
	}
	/** implementation of ServletResponse */
	public PrintWriter getWriter() throws IOException {
		//autoflush为true，则对println()的调用都会刷新输出，但是print()不会
		writer = new PrintWriter(output, true);
		return writer;
	}

	public void flushBuffer() throws IOException {
	}

	public int getBufferSize() {
		return 0;
	}

	public String getCharacterEncoding() {
		return null;
	}

	public Locale getLocale() {
		return null;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	public boolean isCommitted() {
		return false;
	}

	public void reset() {
	}

	public void resetBuffer() {
	}

	public void setBufferSize(int size) {
	}

	public void setContentLength(int length) {
	}

	public void setContentType(String type) {
	}

	public void setLocale(Locale locale) {
	}
}