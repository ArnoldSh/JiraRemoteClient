/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

import java.net.URL;
import org.radixware.jiraclient.exception.JiraClientException;
import org.radixware.jiraclient.wrap.input.AttachmentInput;
import org.radixware.jiraclient.wrap.input.CommentInput;
import org.radixware.jiraclient.wrap.input.ParentIssueInput;
import org.radixware.jiraclient.wrap.input.SubtaskInput;
import org.radixware.jiraclient.wrap.input.WorklogInput;

/**
 * Remote client of JIRA.
 *
 * @author ashamsutdinov
 */
public interface JiraClient {

	// sizes of filter results: default and max
	final int DEFAULT_SEARCH_RESULTS_SIZE = 50;
	final int MAX_SEARCH_RESULTS_SIZE = 65536;

	URL getJiraServerURL() throws JiraClientException;

	User getUser(final String name) throws JiraClientException;

	Iterable<IssueType> getIssueTypes() throws JiraClientException;

	IssueType getIssueType(final String id) throws JiraClientException;

	IssueType getIssueTypeByName(final String issueTypeName) throws JiraClientException;

	Iterable<Priority> getPriorities() throws JiraClientException;

	Priority getPriority(final String id) throws JiraClientException;

	Priority getPriorityByName(final String priorityName) throws JiraClientException;

	Iterable<Resolution> getResolutions() throws JiraClientException;

	Resolution getResolution(final String id) throws JiraClientException;

	Resolution getResolutionByName(final String resolutionName) throws JiraClientException;

	Iterable<Status> getStatuses() throws JiraClientException;

	Status getStatus(final String id) throws JiraClientException;

	Status getStatusByName(final String statusName) throws JiraClientException;

	Iterable<Project> getProjects() throws JiraClientException;

	Project getProject(final String projectKey) throws JiraClientException;

	/**
	 * @param query string JQL-format filter query (see JQL documentation)
	 * @param answerSize max size of result
	 */
	Iterable<Issue> getIssuesByFilterQuery(final String query, final int answerSize) throws JiraClientException;

	/**
	 * @return just created new issue
	 */
	ParentIssue createParentIssue(final ParentIssueInput newIssue) throws JiraClientException;

	/**
	 * @return just created new subtask
	 */
	Subtask createSubtask(final SubtaskInput newSubtask, final ParentIssue parentIssue) throws JiraClientException;

	void deleteIssue(final Issue issue) throws JiraClientException;

	/**
	 * @return just created new comment
	 */
	Comment addComment(final Issue issue, final CommentInput comment) throws JiraClientException;

	/**
	 * @return just updated comment
	 */
	Comment editComment(final Issue issue, final Comment comment, final String newBody) throws JiraClientException;

	Iterable<Comment> getComments(final Issue issue) throws JiraClientException;

	IssueType getSubtaskType(final String id) throws JiraClientException;

	IssueType getSubtaskTypeByName(final String subtaskTypeName) throws JiraClientException;

	Iterable<IssueType> getSubtaskTypes() throws JiraClientException;

	Iterable<Action> getAvailableWorkflowActions(final Issue issue) throws JiraClientException;

	/**
	 * @return just updated issue
	 */
	Issue doWorkflowAction(final Issue issue, final Action action) throws JiraClientException;

	/**
	 * @return just created new worklog
	 */
	Worklog addWorklog(final Issue issue, final WorklogInput worklog) throws JiraClientException;

	Iterable<Worklog> getWorklogs(final Issue issue) throws JiraClientException;

	Iterable<CustomField> getCustomFields(final Issue issue) throws JiraClientException;

	String getJiraVersion() throws JiraClientException;

	Iterable<Version> getProjectVersions(final Project project) throws JiraClientException;

	Issue getIssue(final String issueKey) throws JiraClientException;

	ParentIssue getParentIssue(final String issueKey) throws JiraClientException;

	Iterable<SearchFilter> getFilters() throws JiraClientException;

	Iterable<Issue> getIssuesByFilter(final SearchFilter filter, final int maxResults) throws JiraClientException;

	/**
	 * @return true if attachments were successfully added; if the operation was
	 * not successful, an exception would be thrown.
	 */
	boolean addAttachmentToIssue(final Issue issue, final AttachmentInput attachment) throws JiraClientException;

	Iterable<Attachment> getAttachmentsFromIssue(final Issue issue) throws JiraClientException;
}