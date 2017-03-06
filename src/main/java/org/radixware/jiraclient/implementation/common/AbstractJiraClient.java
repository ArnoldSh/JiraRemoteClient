/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.common;

import com.sun.jersey.core.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import javax.net.ssl.SSLContext;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.radixware.jiraclient.exception.JiraClientException;
import org.radixware.jiraclient.exception.MissingSSLContextException;
import org.radixware.jiraclient.wrap.JiraClient;

/**
 * Abstract client for expanding.
 * @author ashamsutdinov
 */
public abstract class AbstractJiraClient implements JiraClient {
	
	protected final ExternalAttachmentHandler externalAttachmentHandler;
	
	/**
	 * Service class that allows to get independent input stream to attachment. This
	 * class provides access to attachment by basic HTTP auth. or via SSL-connection
	 * with prepared SSLContext. If uses HTTPS it is necessary to setup SSL context.
	 */

	public AbstractJiraClient(final ExternalAttachmentHandler externalAttachmentHandler) {
		this.externalAttachmentHandler = externalAttachmentHandler;
	}

	/**
	 * Auxiliary class for external access to JIRA attachments via HTTP/HTTPS using credentials from init authentication data.
	 */
	protected static class ExternalAttachmentHandler {
		/**
		 * SSL context using in auth.
		 */
		protected final SSLContext sslContext;
		/**
		 * Auth. credentials.
		 */
		protected final String username;
		/**
		 * Auth. credentials.
		 */
		protected final String password;
		/**
		 * ByteArrayOutputStream default buffer size uses at reading.
		 */
		protected final int DEFAULT_BUFFER_SIZE = 4*1024;
		/**
		 * ByteArrayOutputStream max buffer size uses at reading.
		 */
		protected final int MAX_BUFFER_SIZE = Integer.MAX_VALUE / 2;
		/**
		 * ByteArrayOutputStream actual buffer size uses at reading.
		 */
		protected int bufferSize = DEFAULT_BUFFER_SIZE;
		/**
		 * Content length using at stream reading.
		 */
		protected int contentLength = -1; // initialize when getInputStream has invoked

		/**
		 * Authentication by SSL.
		 * @param sslContext
		 */
		public ExternalAttachmentHandler(final SSLContext sslContext) {
			this.sslContext = sslContext;
			this.username = "";
			this.password = "";
		}

		/**
		 * Authentication by login.
		 * @param username
		 * @param password
		 */
		public ExternalAttachmentHandler(final String username, final String password) {
			this.sslContext = null;
			this.username = username;
			this.password = password;
		}

		public void setBufferSize(final int bufferSize) {		
			this.bufferSize =	bufferSize < 1 || bufferSize >	MAX_BUFFER_SIZE	? 
								DEFAULT_BUFFER_SIZE : bufferSize;
		}

		public int getBufferSize() {
			return bufferSize;
		}

		/**
		 * Method trying to get input stream of specified attachment by URL via HTTP/HTTPS connection.
		 * Requires authentication (see constructors).
		 * @param attachmentUrl link to attachment (ex.: https://jira.mycompany.ru/secure/attachment/895462/log.txt)
		 * @return raw input stream
		 * @throws JiraClientException
		 */
		public InputStream getInputStream(final URL attachmentUrl) throws JiraClientException {
			try {
				Socket socket;
				String protocol = attachmentUrl.getProtocol();
				switch (protocol) {
					case "http":
						socket = new DefaultProtocolSocketFactory().createSocket(attachmentUrl.getHost(), attachmentUrl.getPort());
						break;
					case "https":
						if (sslContext != null) {
							socket = sslContext.getSocketFactory().createSocket(attachmentUrl.getHost(), attachmentUrl.getPort());
						} else {
							throw new MissingSSLContextException("Protocol is HTTPS, but SSL context is not set.");
						}
						break;
					default:
						throw new JiraClientException("Unknown protocol. Actually only supports HTTP and HTTPS.");
				}
				OutputStream os = socket.getOutputStream();
				// writing of starting line
				os.write(("GET " + attachmentUrl.getFile() + " HTTP/1.1\r\n").getBytes("UTF-8"));

				// HTTP-header with basic HTTP auth. info
				if (protocol.equals("http")) {
					String base64encodedCredentials = new String(Base64.encode(username + ":" + password), "UTF-8");
					os.write(("Authorization: Basic " + base64encodedCredentials + "\r\n").getBytes("UTF-8"));
				}

				os.write(("Host: " + attachmentUrl.getHost() + ":" + attachmentUrl.getPort() + "\r\n").getBytes("UTF-8"));
				os.write(("Accept: */*\r\n\r\n").getBytes("UTF-8"));
				os.flush();

				InputStream is = socket.getInputStream();

				//skip unused response code
				HttpParser.readLine(is, "UTF-8");

				for (Header h : HttpParser.parseHeaders(is, "UTF-8")) {
					if ("Content-Length".equals(h.getName())) {
						contentLength = Integer.valueOf(h.getValue());
						break;
					}
				}

				return is;
			} catch (IOException ex) {
				throw new JiraClientException(ex);
			}
		}

		/**
		 * Auxiliary method returning attachment as raw array of bytes.
		 * @deprecated uses only on debug. Use getInputStream(url).
		 * @param attachmentUrl link to attachment (ex.: https://jira.mycompany.ru/secure/attachment/895462/log.txt)
		 * @return raw array of bytes
		 * @throws JiraClientException
		 */
		public byte[] getBytes(final URL attachmentUrl) throws JiraClientException {
			try {
				InputStream is = getInputStream(attachmentUrl);

				byte[] buffer = new byte[bufferSize];
				int bytesRead = 0;
				int length = 0;

				ByteArrayOutputStream baos = new ByteArrayOutputStream(contentLength);

				while (bytesRead < contentLength && length != -1) {

					length = is.read(buffer, 0, bufferSize);
					baos.write(buffer, 0, length);
					bytesRead += length;

				}
				return baos.toByteArray();
			} catch (IOException ex) {
				throw new JiraClientException(ex);
			}
		}
	}
	
}
