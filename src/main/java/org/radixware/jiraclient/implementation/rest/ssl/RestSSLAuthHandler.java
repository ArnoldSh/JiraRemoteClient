/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest.ssl;

import com.atlassian.jira.rest.client.AuthenticationHandler;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.Filterable;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import javax.net.ssl.SSLContext;
import org.apache.commons.httpclient.protocol.Protocol;
import org.radixware.jiraclient.exception.JiraClientException;

/**
 * Handler for authentication by prepared SSLContext.
 *
 * @author ashamsutdinov
 */
public class RestSSLAuthHandler implements AuthenticationHandler {

	protected SSLContext sslContext;

	public RestSSLAuthHandler(final SSLContext sslContext) {
		this.sslContext = sslContext;
	}

	@Override
	public void configure(ApacheHttpClientConfig config) {
		try {
			// force load: sad, but true (static is an evil)
			// We need to load this class FIRSTLY that we can initialize our implementation of factory
			Class.forName(org.apache.commons.httpclient.protocol.Protocol.class.getCanonicalName());

			final String scheme = "https";

			Protocol https = new Protocol(scheme, new RestJiraSocketFactory(sslContext), 443);

			Protocol.registerProtocol(scheme, https);

		} catch (ClassNotFoundException ex) {
			throw new JiraClientException(ex);
		}

	}

	@Override
	public void configure(@SuppressWarnings("unused") Filterable filterable, @SuppressWarnings("unused") Client client) {
		configure(new DefaultApacheHttpClientConfig());
	}
}
