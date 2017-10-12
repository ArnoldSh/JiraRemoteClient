/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteComponent;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteResolution;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.radixware.jiraclient.wrap.*;

/**
 *
 * @author ashamsutdinov
 */
public class SoapParentIssue extends SoapClientContainer implements ParentIssue {

	protected final RemoteIssue issue;

	/**
	 * @param owner link to the actual client instance
	 */
	protected SoapParentIssue(final RemoteIssue issue, final SoapJiraClient owner) {
		super(owner);
		this.issue = issue;
	}

	@Override
	public String getId() {
		return issue.getId();
	}

	@Override
	public String getSummary() {
		return issue.getSummary();
	}

	@Override
	public String getKey() {
		return issue.getKey();
	}

	@Override
	public String getDescription() {
		return issue.getDescription();
	}

	@Override
	public Project getProject() {
		return getOwner().getProject(issue.getProject());
	}

	@Override
	public IssueType getIssueType() {
		return getOwner().getIssueType(issue.getType());
	}

	@Override
	public Status getStatus() {
		return getOwner().getStatus(issue.getStatus());
	}

	@Override
	public Resolution getResolution() {
		// if the resolution "Unresolved" that means returned id is null
		// make a wrap
		if (issue.getResolution() == null) {
			return new SoapResolution(new RemoteResolution("-1", "Unresolved", "", ""));
		} else {
			return getOwner().getResolution(issue.getResolution());
		}
	}

	@Override
	public User getReporter() {
		return getOwner().getUser(issue.getReporter());
	}

	@Override
	public User getAssignee() {
		return getOwner().getUser(issue.getAssignee());
	}

	@Override
	public Priority getPriority() {
		return getOwner().getPriority(issue.getPriority());
	}

	@Override
	public Iterable<Version> getAffectsVersions() {
		List<Version> outList = new ArrayList<>();
		RemoteVersion[] inList = issue.getAffectsVersions();
		if (inList != null) {
			for (RemoteVersion rv : inList) {
				outList.add(new SoapVersion(rv));
			}
		}
		return Collections.unmodifiableList(outList);
	}

	@Override
	public Iterable<Version> getFixVersions() {
		List<Version> outList = new ArrayList<>();
		RemoteVersion[] inList = issue.getFixVersions();
		if (inList != null) {
			for (RemoteVersion rv : inList) {
				outList.add(new SoapVersion(rv));
			}
		}
		return Collections.unmodifiableList(outList);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapParentIssue) {
			SoapParentIssue that = (SoapParentIssue) obj;
			return Objects.equals(this.issue, that.issue);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 11 * hash + Objects.hashCode(this.issue);
		return hash;
	}

	@Override
	public Date getDuedate() {
		Calendar date = issue.getDuedate();
		if (date == null) {
			date = Calendar.getInstance();
			date.setTimeInMillis(0);
		}
		return date.getTime();
	}

	@Override
	public Date getCreatedDate() {
		Calendar date = issue.getCreated();
		if (date == null) {
			date = Calendar.getInstance();
			date.setTimeInMillis(0);
		}
		return date.getTime();
	}

	@Override
	public Date getUpdatedDate() {
		Calendar date = issue.getUpdated();
		if (date == null) {
			date = Calendar.getInstance();
			date.setTimeInMillis(0);
		}
		return date.getTime();
	}

	@Override
	public Iterable<Subtask> getAllSubtasks() {
		return getOwner().getOnlySubtasks(issue.getKey());
	}
	
	@Override
	public Iterable<Component> getComponents() {
		List<Component> components = new ArrayList<>();
		for(RemoteComponent component : this.issue.getComponents()) {
			components.add( new SoapComponent( component ) );
		}
		return Collections.unmodifiableList( components );
	}
	
}
