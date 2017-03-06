/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.util.Objects;
import org.radixware.jiraclient.wrap.Priority;

/**
 *
 * @author ashamsutdinov
 */
public class RestPriority implements Priority {

	protected final com.atlassian.jira.rest.client.domain.Priority priority;

	protected RestPriority(final com.atlassian.jira.rest.client.domain.Priority priority) {
		this.priority = priority;
	}

	@Override
	public String getId() {
		return priority.getId().toString();
	}

	@Override
	public String getName() {
		return priority.getName();
	}

	@Override
	public String getDescription() {
		return priority.getDescription();
	}

	@Override
	public String getColor() {
		return priority.getStatusColor();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestPriority) {
			RestPriority that = (RestPriority) obj;
			return Objects.equals(this.priority, that.priority);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.priority);
		return hash;
	}
}
