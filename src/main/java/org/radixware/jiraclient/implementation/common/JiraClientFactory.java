/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.common;

import java.net.URL;
import javax.net.ssl.SSLContext;
import org.radixware.jiraclient.implementation.rest.RestJiraClient;
import org.radixware.jiraclient.implementation.soap.SoapJiraClient;
import org.radixware.jiraclient.wrap.JiraClient;

/**
 * Factory allowing to get actual client. 
 * SOAP (versions 3.3 - 5.0), 
 * REST (versions 5.0 and greater).
 * @author ashamsutdinov
 */
@SuppressWarnings("Duplicates")
public class JiraClientFactory {

	public static final Double LOWEST_VERSION = 3.3;
	public static final Double REST_RELEASE_VERSION = 5.0;

	/**
	 * Creates JIRA client with authentication by user/password.
	 */
	public static JiraClient createWithBasicAuth(final URL jiraServerURL, final String user, final String password) {

		JiraClient client;

		SoapJiraClient soapClient = new SoapJiraClient(jiraServerURL, user, password);

		String jiraVersion = soapClient.getJiraVersion();
		Double jiraVersionClean = Double.parseDouble(jiraVersion.split("\\.")[0] + "." + jiraVersion.split("\\.")[1]);

		if (jiraVersionClean >= LOWEST_VERSION && jiraVersionClean < REST_RELEASE_VERSION) {
			client = soapClient;
		} else /* if(jiraVersionClean >= REST_RELEASE_VERSION) */ {
			client = new RestJiraClient(jiraServerURL, user, password);
		}

		return client;
	}

	/**
	 * Creates JIRA client by SSL-context.
	 */
	public static JiraClient createWithSSLContext(final URL jiraServerURL, final SSLContext sslContext) {

		JiraClient client;

		SoapJiraClient soapClient = new SoapJiraClient(jiraServerURL, sslContext);

		String jiraVersion = soapClient.getJiraVersion();
		Double jiraVersionClean = Double.parseDouble(jiraVersion.split("\\.")[0] + "." + jiraVersion.split("\\.")[1]);

		if (jiraVersionClean >= LOWEST_VERSION && jiraVersionClean < REST_RELEASE_VERSION) {
			client = soapClient;
		} else /* if(jiraVersionClean >= REST_RELEASE_VERSION) */ {
			client = new RestJiraClient(jiraServerURL, sslContext);
		}

		return client;
	}

}
