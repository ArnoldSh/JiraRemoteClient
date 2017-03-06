/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.exception;

/**
 * Common JIRA Remote Client exception in case of problems (connectivity,
 * malformed messages, etc).
 * @author ashamsutdinov
 */
@SuppressWarnings("serial")
public class JiraClientException extends RuntimeException {

	public JiraClientException(final String message, final Exception cause) {
		super(message, cause);
	}

	public JiraClientException(final Exception cause) {
		super(cause);
	}

	public JiraClientException(final String message) {
		super(message);
	}
}
