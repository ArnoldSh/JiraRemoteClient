/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.common.input;

import java.util.Date;

import org.radixware.jiraclient.wrap.*;
import org.radixware.jiraclient.wrap.IssueType;
import org.radixware.jiraclient.wrap.input.ParentIssueInput;

/**
 * Mutable parent issue.
 * @author ashamsutdinov
 */
public class ParentIssueInputImpl implements ParentIssueInput {

	protected Iterable<Version> affectsVersions;
	protected Iterable<Version> fixVersions;
	protected String summary;
	protected User assignee;
	protected User reporter;
	protected Project project;
	protected Priority priority;
	protected IssueType issueType;
	protected Date duedate;
	protected String description;

	public ParentIssueInputImpl(final String summary,
								final Project project,
								final Priority priority,
								final IssueType issueType,
								final User assignee,
								final User reporter) {

		this.summary = summary;
		this.project = project;
		this.priority = priority;
		this.issueType = issueType;
		this.assignee = assignee;
		this.reporter = reporter;

	}

	@Override
	public void setAffectsVersions(final Iterable<Version> affectsVersions) {
		this.affectsVersions = affectsVersions;
	}

	@Override
	public void setFixVersions(final Iterable<Version> fixVersions) {
		this.fixVersions = fixVersions;
	}

	@Override
	public void setSummary(final String summary) {
		this.summary = summary;
	}

	@Override
	public void setAssignee(final User assignee) {
		this.assignee = assignee;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public void setDuedate(final Date duedate) {
		this.duedate = (Date) duedate.clone();
	}

	@Override
	public void setPriority(final Priority priority) {
		this.priority = priority;
	}

	@Override
	public void setProject(final Project project) {
		this.project = project;
	}

	@Override
	public void setReporter(final User reporter) {
		this.reporter = reporter;
	}

	@Override
	public void setIssueType(final IssueType issueType) {
		this.issueType = issueType;
	}

	@Override
	public Iterable<Version> getFixVersions() {
		return fixVersions;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
	public User getAssignee() {
		return assignee;
	}

	@Override
	public User getReporter() {
		return reporter;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Date getDuedate() {
		return duedate == null ? null : (Date) duedate.clone();
	}

	@Override
	public Project getProject() {
		return project;
	}

	@Override
	public Priority getPriority() {
		return priority;
	}

	@Override
	public IssueType getIssueType() {
		return issueType;
	}

	@Override
	public Iterable<Version> getAffectsVersions() {
		return affectsVersions;
	}
}
