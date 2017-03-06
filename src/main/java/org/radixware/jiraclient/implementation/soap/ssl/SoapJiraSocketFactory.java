/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap.ssl;

import java.io.IOException;
import javax.net.ssl.SSLContext;
import org.apache.axis.components.net.JSSESocketFactory;

/**
 * Service factory for replacement in using API (see constructor
 * SoapJiraClient(URL, SSLContext)).
 *
 * @author ashamsutdinov
 */
public class SoapJiraSocketFactory extends JSSESocketFactory {

	protected final SSLContext sslContext;

	public SoapJiraSocketFactory(final SSLContext sslContext) {
		super(null);
		this.sslContext = sslContext;
	}

	@Override
	protected void initFactory() throws IOException {
		sslFactory = sslContext.getSocketFactory();
	}
}
