/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteNamedObject;
import org.radixware.jiraclient.wrap.Action;

/**
 *
 * @author ashamsutdinov
 */
public class SoapAction implements Action {

	protected final RemoteNamedObject action;

	protected SoapAction(RemoteNamedObject action) {
		this.action = action;
	}

	@Override
	public String getId() {
		return action.getId();
	}

	@Override
	public String getName() {
		return action.getName();
	}
}
