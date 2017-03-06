/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.net.URI;
import java.util.Objects;
import org.radixware.jiraclient.wrap.IssueType;

/**
 *
 * @author ashamsutdinov
 */
public class RestIssueType implements IssueType {

	protected final com.atlassian.jira.rest.client.domain.IssueType issueType;

	protected RestIssueType(final com.atlassian.jira.rest.client.domain.IssueType issueType) {
		this.issueType = issueType;
	}

	@Override
	public String getId() {
		return issueType.getId().toString();
	}

	@Override
	public String getName() {
		return issueType.getName();
	}

	@Override
	public String getDescription() {
		return issueType.getDescription();
	}

	@Override
	public boolean isSubtask() {
		return issueType.isSubtask();
	}

	public URI getSelfUri() {
		return issueType.getSelf();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestIssueType) {
			RestIssueType that = (RestIssueType) obj;
			return Objects.equals(this.issueType, that.issueType);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.issueType);
		return hash;
	}
}
