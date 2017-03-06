/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLContext;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.radixware.jiraclient.exception.JiraClientException;

/**
 * Service factory for replacement in using API (see constructor
 * RestJiraClient(URL, SSLContext)).
 *
 * @author ashamsutdinov
 */
public class RestJiraSocketFactory implements ProtocolSocketFactory {

	protected final SSLContext sslConxtext;

	public RestJiraSocketFactory(final SSLContext sslContext) {
		this.sslConxtext = sslContext;
	}

	@Override
	@SuppressWarnings("unused")
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
		try {
			return this.createSocket(host, port);
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	@SuppressWarnings("unused")
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) {
		try {
			return this.createSocket(host, port);
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}

	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		try {
			return sslConxtext.getSocketFactory().createSocket(host, port);
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}
}
