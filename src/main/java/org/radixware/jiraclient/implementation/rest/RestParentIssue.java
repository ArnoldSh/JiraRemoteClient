/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import com.atlassian.jira.rest.client.domain.Issue;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.radixware.jiraclient.wrap.*;
import org.radixware.jiraclient.wrap.IssueType;

/**
 *
 * @author ashamsutdinov
 */
public class RestParentIssue extends RestClientContainer implements ParentIssue {

	protected final Issue issue;

	/**
	 * @param owner link to the actual client instance
	 */
	protected RestParentIssue(final Issue issue, final RestJiraClient owner) {
		super(owner);
		this.issue = issue;
	}

	@Override
	public String getId() {
		return issue.getId().toString();
	}

	@Override
	public String getKey() {
		return issue.getKey();
	}

	public URI getSelfUri() {
		return issue.getSelf();
	}

	@Override
	public String getDescription() {
		return issue.getDescription();
	}

	@Override
	public String getSummary() {
		return issue.getSummary();
	}

	@Override
	public Project getProject() {
		return getOwner().getProject(issue.getProject().getKey());
	}

	@Override
	public IssueType getIssueType() {
		return getOwner().getIssueType(issue.getIssueType().getId().toString());
	}

	@Override
	public Status getStatus() {
		return getOwner().getStatus(issue.getStatus().getId().toString());
	}

	@Override
	public Resolution getResolution() {
		// if the resolution "Unresolved" that means returned id is null
		// make a wrap
		if (issue.getResolution() == null) {
			return new RestResolution(new com.atlassian.jira.rest.client.domain.Resolution(null, "Unresolved", "", Long.valueOf(-1)));
		} else {
			return getOwner().getResolution(issue.getResolution().getId().toString());
		}
	}

	@Override
	public User getReporter() {
		return getOwner().getUser(issue.getReporter().getName());
	}

	@Override
	public User getAssignee() {
		return getOwner().getUser(issue.getAssignee().getName());
	}

	@Override
	public Iterable<Version> getAffectsVersions() {

		List<Version> outList = new ArrayList<>();
		Iterable<com.atlassian.jira.rest.client.domain.Version> inList = issue.getAffectedVersions();

		for (com.atlassian.jira.rest.client.domain.Version v : inList) {
			outList.add(new RestVersion(v));
		}

		return outList;
	}

	@Override
	public Iterable<Version> getFixVersions() {

		List<Version> outList = new ArrayList<>();
		Iterable<com.atlassian.jira.rest.client.domain.Version> inList = issue.getFixVersions();

		for (com.atlassian.jira.rest.client.domain.Version v : inList) {
			outList.add(new RestVersion(v));
		}

		return outList;
	}

	@Override
	public Priority getPriority() {
		return getOwner().getPriority(issue.getPriority().getId().toString());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestParentIssue) {
			RestParentIssue that = (RestParentIssue) obj;
			return Objects.equals(this.issue, that.issue);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 41 * hash + Objects.hashCode(this.issue);
		return hash;
	}

	@Override
	public Date getDuedate() {
		return issue.getDueDate().toDate();
	}

	@Override
	public Date getCreatedDate() {
		return issue.getCreationDate().toDate();
	}

	@Override
	public Date getUpdatedDate() {
		return issue.getUpdateDate().toDate();
	}

	@Override
	public Iterable<Subtask> getAllSubtasks() {
		Iterable<com.atlassian.jira.rest.client.domain.Subtask> inList = issue.getSubtasks();
		List<Subtask> outList = new ArrayList<>();
		for (com.atlassian.jira.rest.client.domain.Subtask s : inList) {
			outList.add(new RestSubtask(s, this, getOwner()));
		}
		return Collections.unmodifiableList(outList);
	}
}
