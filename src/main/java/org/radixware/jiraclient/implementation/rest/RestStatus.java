/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.net.URI;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Status;

/**
 *
 * @author ashamsutdinov
 */
public class RestStatus implements Status {

	protected final com.atlassian.jira.rest.client.domain.Status status;

	protected RestStatus(final com.atlassian.jira.rest.client.domain.Status status) {
		this.status = status;
	}

	@Override
	public String getId() {
		return status.getId().toString();
	}

	@Override
	public String getName() {
		return status.getName();
	}

	@Override
	public String getDescription() {
		return status.getDescription();
	}

	public URI getSelfUri() {
		return status.getSelf();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestStatus) {
			RestStatus that = (RestStatus) obj;
			return Objects.equals(this.status, that.status);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 47 * hash + Objects.hashCode(this.status);
		return hash;
	}
}
