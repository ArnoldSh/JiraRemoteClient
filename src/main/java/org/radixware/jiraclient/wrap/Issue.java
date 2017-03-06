/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

import java.util.Date;

/**
 * Representation of JIRA issue. Common part of parent issue and subtask.
 *
 * @author ashamsutdinov
 */
public interface Issue {

	String getId();

	String getSummary();

	String getKey();

	String getDescription();

	Date getCreatedDate();

	Date getDuedate();

	Date getUpdatedDate();

	Project getProject();

	IssueType getIssueType();

	Status getStatus();

	Resolution getResolution();

	User getReporter();

	User getAssignee();

	Priority getPriority();

	Iterable<Version> getAffectsVersions();

	Iterable<Version> getFixVersions();
}
