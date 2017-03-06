/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap.input;

import java.util.Date;
import org.radixware.jiraclient.wrap.IssueType;
import org.radixware.jiraclient.wrap.Priority;
import org.radixware.jiraclient.wrap.Project;
import org.radixware.jiraclient.wrap.User;
import org.radixware.jiraclient.wrap.Version;

/**
 * New input issue.
 *
 * @author ashamsutdinov
 */
public interface ParentIssueInput {

	void setAffectsVersions(final Iterable<Version> affectsVersions);

	void setFixVersions(final Iterable<Version> fixVersions);

	void setSummary(final String summary);

	void setAssignee(final User assignee);

	void setDescription(final String description);

	void setDuedate(final Date duedate);

	void setPriority(final Priority priority);

	void setProject(final Project project);

	void setReporter(final User reporter);

	void setIssueType(final IssueType issueType);

	Iterable<Version> getAffectsVersions();

	Iterable<Version> getFixVersions();

	String getSummary();

	User getAssignee();

	User getReporter();

	String getDescription();

	Date getDuedate();

	Project getProject();

	Priority getPriority();

	IssueType getIssueType();
}
