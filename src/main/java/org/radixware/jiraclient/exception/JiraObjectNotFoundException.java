/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.exception;

/**
 * Special exception that notifies client JIRA object not found (for ex.: user,
 * project, issue, issue type and etc).
 * @author ashamsutdinov
 */
@SuppressWarnings("serial")
public class JiraObjectNotFoundException extends JiraClientException {

	public JiraObjectNotFoundException(final String message) {
		super(message);
	}
}
