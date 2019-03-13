package com.pdc.tomcat.connector;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

import com.pdc.tomcat.connector.http.HttpResponse;

/**
 * Convenience implementation of <b>ServletOutputStream</b> that works with the
 * standard ResponseBase implementation of <b>Response</b>. If the content
 * length has been set on our associated Response, this implementation will
 * enforce not writing more than that many bytes on the underlying stream.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.6 $ $Date: 2002/03/18 07:15:39 $
 * @deprecated
 */

public class ResponseStream extends ServletOutputStream {

	// ----------------------------------------------------------- Constructors

	/**
	 * Construct a servlet output stream associated with the specified Request.
	 *
	 * @param response
	 *            The associated response
	 */
	public ResponseStream(HttpResponse response) {

		super();
		closed = false;
		commit = false;
		count = 0;
		this.response = response;
		// this.stream = response.getStream();

	}

	// ----------------------------------------------------- Instance Variables

	/**
	 * Has this stream been closed?
	 */
	protected boolean closed = false;

	/**
	 * Should we commit the response when we are flushed?
	 */
	protected boolean commit = false;

	/**
	 * The number of bytes which have already been written to this stream.
	 */
	protected int count = 0;

	/**
	 * The content length past which we will not write, or -1 if there is no
	 * defined content length.
	 */
	protected int length = -1;
	protected HttpResponse response = null;
	protected OutputStream stream = null;

	public boolean getCommit() {

		return this.commit;

	}

	public void setCommit(boolean commit) {

		this.commit = commit;

	}

	public void close() throws IOException {
		if (closed)
			throw new IOException("responseStream.close.closed");
		response.flushBuffer();
		closed = true;
	}

	// 输出后刷新
	public void flush() throws IOException {
		if (closed)
			throw new IOException("responseStream.flush.closed");
		if (commit)
			response.flushBuffer();

	}

	// 写指定的字符到response,即输出流中
	public void write(int b) throws IOException {
		if (closed)
			throw new IOException("responseStream.write.closed");
		if ((length > 0) && (count >= length))
			throw new IOException("responseStream.write.count");
		response.write(b);
		count++;
	}

	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte b[], int off, int len) throws IOException {
		if (closed)
			throw new IOException("responseStream.write.closed");
		int actual = len;
		if ((length > 0) && ((count + len) >= length))
			actual = length - count;
		response.write(b, off, actual);
		count += actual;
		if (actual < len)
			throw new IOException("responseStream.write.count");
	}

	boolean closed() {
		return this.closed;
	}

	void reset() {
		count = 0;
	}
}
