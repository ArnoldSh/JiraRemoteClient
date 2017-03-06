/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.exception;

/**
 * Notifies when SSL context is not set. Before use SSL connection it is
 * necessary to set up SSLContext.
 * @author ashamsutdinov
 */
@SuppressWarnings("serial")
public class MissingSSLContextException extends JiraClientException {

	public MissingSSLContextException(final String message) {
		super(message);
	}
}
