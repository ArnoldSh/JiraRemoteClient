/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import com.atlassian.jira.rest.client.domain.Transition;
import org.radixware.jiraclient.wrap.Action;

/**
 *
 * @author ashamsutdinov
 */
public class RestAction implements Action {

	protected final Transition action;

	protected RestAction(final Transition action) {
		this.action = action;
	}

	@Override
	public String getId() {
		return Integer.valueOf(action.getId()).toString();
	}

	@Override
	public String getName() {
		return action.getName();
	}
}
