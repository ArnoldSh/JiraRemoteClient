/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicIssueType;
import com.atlassian.jira.rest.client.domain.BasicPriority;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.FavouriteFilter;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.domain.input.WorklogInputBuilder;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.impl.ClientRequestImpl;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.radixware.jiraclient.implementation.common.AbstractJiraClient;
import org.radixware.jiraclient.implementation.common.CustomFieldImpl;
import org.radixware.jiraclient.exception.JiraClientException;
import org.radixware.jiraclient.exception.JiraObjectNotFoundException;
import org.radixware.jiraclient.implementation.rest.ssl.RestSSLAuthHandler;
import org.radixware.jiraclient.wrap.*;
import org.radixware.jiraclient.wrap.IssueType;
import org.radixware.jiraclient.wrap.input.AttachmentInput;
import org.radixware.jiraclient.wrap.input.CommentInput;
import org.radixware.jiraclient.wrap.input.ParentIssueInput;
import org.radixware.jiraclient.wrap.input.SubtaskInput;
import org.radixware.jiraclient.wrap.input.WorklogInput;

/**
 * REST JIRA Remote Client.
 * Some functions are implemented based on raw REST API via HTTP with using of
 * JSON Objects.
 *
 * @author ashamsutdinov
 */
public final class RestJiraClient extends AbstractJiraClient {

	private JiraRestClient service;
	private final URL jiraServerURL;
	private final DummyProgressMonitor progressMonitor = new DummyProgressMonitor();

	/**
	 * Low-level API handler.
	 */
	private ApacheHttpClient httpClient;

	/**
	 * Creates new client and authenticate by user name & password.
	 *
	 * @throws JiraClientException in case of problems (connectivity, malformed
	 * messages, etc)
	 */
	public RestJiraClient(final URL jiraServerURL, final String username, final String password) throws JiraClientException {
		super(new ExternalAttachmentHandler(username, password));	
		try {								
			this.jiraServerURL = jiraServerURL;
			service = new JerseyJiraRestClientFactory().createWithBasicHttpAuthentication(new URI(jiraServerURL.toString()), username, password);
			httpClient = service.getTransportClient();
		} catch (URISyntaxException ex) {
			throw new JiraClientException(ex);
		}
	}

	/**
	 * Creates new client and authenticate by SSLContext.
	 *
	 * @throws JiraClientException in case of problems (connectivity, malformed
	 * messages, etc)
	 */
	public RestJiraClient(final URL jiraServerURL, final SSLContext sslContext) throws JiraClientException {
		super(new ExternalAttachmentHandler(sslContext));
		try {
			this.jiraServerURL = jiraServerURL;
			service = new JerseyJiraRestClientFactory().create(new URI(jiraServerURL.toString()), new RestSSLAuthHandler(sslContext));
			httpClient = service.getTransportClient();
		} catch (URISyntaxException ex) {
			throw new JiraClientException(ex);
		}
	}

	byte[] getAttachmentBytes(final URL attachmentURL) throws JiraClientException {
		return externalAttachmentHandler.getBytes(attachmentURL);
	}

	InputStream getAttachmentInputStream(final URL attachmentURL) throws JiraClientException {
		return externalAttachmentHandler.getInputStream(attachmentURL);
	}
	
	/**
	 * Low-level handler for debugging and development.
	 * Use it to send manually constructed REST queries.
	 * Also see org.codehaus.jettison.json.* for work with JSON messages.
	 */
	public ApacheHttpClient getApacheHttpClient() {
		return httpClient;
	}

	@Override
	public URL getJiraServerURL() {
		return jiraServerURL;
	}

	public URL getJiraRestServiceURL() {
		try {
			return new URL(jiraServerURL.toString() + "/rest/api/latest");
		} catch (MalformedURLException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Project getProject(final String projectKey) {

		com.atlassian.jira.rest.client.domain.Project project = null;

		try {
			project = service.getProjectClient().getProject(projectKey, progressMonitor);
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (project == null) {
			throw new JiraObjectNotFoundException("No project could be found with key \'" + projectKey + "\'");
		}

		return new RestProject(project, this);
	}

	@Override
	public Issue getIssue(final String issueKey) {

		com.atlassian.jira.rest.client.domain.Issue issue = null;

		try {
			issue = service.getIssueClient().getIssue(issueKey, progressMonitor);
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (issue == null) {
			throw new JiraObjectNotFoundException("No issue could be found with key \'" + issueKey + "\'");
		}

		return new RestParentIssue(issue, this);

	}

	@Override
	public Iterable<Project> getProjects() {

		List<Project> outList = new ArrayList<>();

		try {
			Iterable<BasicProject> inList = service.getProjectClient().getAllProjects(progressMonitor);

			for (BasicProject bp : inList) {
				outList.add(new RestProject(service.getProjectClient().getProject(bp.getKey(), progressMonitor), this));
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		return Collections.unmodifiableList(outList);
	}

	@Override
	public Iterable<IssueType> getIssueTypes() {

		List<IssueType> outList = new ArrayList<>();

		try {
			Iterable<com.atlassian.jira.rest.client.domain.IssueType> inList = service.getMetadataClient().getIssueTypes(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.IssueType it : inList) {
				outList.add(new RestIssueType(it));
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		return Collections.unmodifiableList(outList);
	}

	@Override
	public IssueType getIssueType(final String id) {

		RestIssueType answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.IssueType> issueTypes = service.getMetadataClient().getIssueTypes(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.IssueType it : issueTypes) {
				if (String.valueOf(it.getId()).equals(id)) {
					answer = new RestIssueType(it);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No issue type could be found with id \'" + id + "\'");
		}

		return answer;

	}

	@Override
	public User getUser(final String name) {

		com.atlassian.jira.rest.client.domain.User user = null;

		try {
			user = service.getUserClient().getUser(name, progressMonitor);
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (user == null) {
			throw new JiraObjectNotFoundException("User not found");
		}

		return new RestUser(user);

	}

	@Override
	public Iterable<Priority> getPriorities() {

		List<Priority> outList = new ArrayList<>();

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Priority> inList = service.getMetadataClient().getPriorities(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Priority p : inList) {
				outList.add(new RestPriority(p));
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		return Collections.unmodifiableList(outList);
	}

	@Override
	public Priority getPriority(final String id) {

		RestPriority answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Priority> priorities = service.getMetadataClient().getPriorities(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Priority p : priorities) {
				if (p.getId().toString().equals(id)) {
					answer = new RestPriority(p);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No priority could be found with id \'" + id + "\'");
		}

		return answer;
	}

	@Override
	public Iterable<Resolution> getResolutions() {

		List<Resolution> outList = new ArrayList<>();

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Resolution> inList = service.getMetadataClient().getResolutions(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Resolution r : inList) {
				outList.add(new RestResolution(r));
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		return Collections.unmodifiableList(outList);
	}

	@Override
	public Resolution getResolution(final String id) {

		RestResolution answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Resolution> resolutions = service.getMetadataClient().getResolutions(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Resolution r : resolutions) {
				if (r.getId().toString().equals(id)) {
					answer = new RestResolution(r);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No resolution could be found with id \'" + id + "\'");
		}

		return answer;
	}

	@Override
	public Iterable<Status> getStatuses() {

		List<Status> outList = new ArrayList<>();

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Status> inList = service.getMetadataClient().getStatuses(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Status s : inList) {
				outList.add(new RestStatus(s));
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		return Collections.unmodifiableList(outList);

	}

	@Override
	public Status getStatus(final String id) {

		RestStatus answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Status> statuses = service.getMetadataClient().getStatuses(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Status s : statuses) {
				if (s.getId().toString().equals(id)) {
					answer = new RestStatus(s);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No status could be found with id \'" + id + "\'");
		}

		return answer;

	}

	@Override
	public Iterable<Issue> getIssuesByFilterQuery(final String query, final int answerSize) {

		int newAnswerSize = answerSize < 1 || answerSize > MAX_SEARCH_RESULTS_SIZE
				? DEFAULT_SEARCH_RESULTS_SIZE
				: answerSize;

		List<Issue> outList = new ArrayList<>();

		try {
			Iterable<? extends BasicIssue> inList = service.getSearchClient().searchJql(query, newAnswerSize, 0, progressMonitor).getIssues();
			for (BasicIssue bi : inList) {
				outList.add(new RestParentIssue(service.getIssueClient().getIssue(bi.getKey(), progressMonitor), this));
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		return Collections.unmodifiableList(outList);

	}

	@Override
	public void deleteIssue(final Issue issue) {
		service.getIssueClient().removeIssue(issue.getKey(), true, progressMonitor);
	}

	@Override
	public Iterable<IssueType> getSubtaskTypes() {

		List<IssueType> outList = new ArrayList<>();

		try {
			Iterable<com.atlassian.jira.rest.client.domain.IssueType> inList = service.getMetadataClient().getIssueTypes(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.IssueType it : inList) {
				if (it.isSubtask()) {
					outList.add(new RestIssueType(it));
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		return Collections.unmodifiableList(outList);
	}

	@Override
	public Iterable<Action> getAvailableWorkflowActions(final Issue issue) {

		List<Action> outList = new ArrayList<>();

		try {

			Iterable<Transition> inList = service.getIssueClient().getTransitions(service.getIssueClient().getIssue(issue.getKey(), progressMonitor), progressMonitor);
			for (Transition t : inList) {
				outList.add(new RestAction(t));
			}

		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		return Collections.unmodifiableList(outList);

	}

	@Override
	public Comment addComment(final Issue issue, final CommentInput comment) {
		try {
			com.atlassian.jira.rest.client.domain.Comment restComment = new com.atlassian.jira.rest.client.domain.Comment(
					service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getCommentsUri(),
					comment.getBody(),
					null, // there is no need to fill these fields
					null,
					null,
					null,
					null,
					null);

			// process: add comment
			service.getIssueClient().addComment(progressMonitor, service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getCommentsUri(), restComment);

			// searching of just created comment for return
			Comment answerComment = null;
			Iterator<Comment> iter = getComments(issue).iterator();
			while (iter.hasNext()) {
				answerComment = iter.next();
			}
			return answerComment;
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Issue doWorkflowAction(final Issue issue, final Action action) {
		TransitionInput transition = new TransitionInput(Integer.parseInt(action.getId()));
		service.getIssueClient().transition(service.getIssueClient().getIssue(issue.getKey(), progressMonitor), transition, progressMonitor);
		return getIssue(issue.getKey());
	}

	@Override
	public Subtask createSubtask(final SubtaskInput newSubtask, final ParentIssue parentIssue) {
		try {
			String host = jiraServerURL.getHost();
			String protocol = jiraServerURL.getProtocol();
			int port = jiraServerURL.getPort();

			String buildedUri = protocol + "://" + host + ":" + String.valueOf(port) + "/rest/api/latest/issue";

			JSONObject[] jsonObject = new JSONObject[6];
			// 0 assignee, 
			// 1 reporter, 
			// 2 subtaskType, 
			// 3 priority, 
			// 4 project, 
			// 5 parentIssue
			for (int index = 0; index < 6; index++) {
				jsonObject[index] = new JSONObject();
			}

			JSONArray fixVersions = new JSONArray();
			JSONArray affectsVersions = new JSONArray();
			for (Version vi : newSubtask.getFixVersions()) {
				JSONObject buf = new JSONObject();
				buf.put("id", vi.getId());
				fixVersions.put(buf);
			}
			for (Version vi : newSubtask.getAffectsVersions()) {
				JSONObject buf = new JSONObject();
				buf.put("id", vi.getId());
				affectsVersions.put(buf);
			}

			jsonObject[0].put("name", newSubtask.getAssignee().getName());
			jsonObject[1].put("name", newSubtask.getReporter().getName());
			jsonObject[2].put("id", newSubtask.getIssueType().getId());
			jsonObject[3].put("id", newSubtask.getPriority().getId());
			jsonObject[4].put("key", newSubtask.getProject().getKey());
			jsonObject[5].put("key", parentIssue.getKey());

			JSONObject fields = new JSONObject();

			fields.put("assignee", jsonObject[0]);
			fields.put("reporter", jsonObject[1]);
			fields.put("issuetype", jsonObject[2]);
			fields.put("priority", jsonObject[3]);
			fields.put("project", jsonObject[4]);
			fields.put("parent", jsonObject[5]);

			fields.put("description", newSubtask.getDescription());
			fields.put("summary", newSubtask.getSummary());
			if (newSubtask.getDuedate() != null) {
				fields.put("duedate", newSubtask.getDuedate().toString());
			}

			fields.put("versions", affectsVersions);
			fields.put("fixVersions", fixVersions);

			JSONObject requestMessage = new JSONObject();
			requestMessage.put("fields", fields);

			ClientRequest cr = new ClientRequestImpl(new URI(buildedUri), "POST", requestMessage);

			// creating subtask
			ClientResponse response = service.getTransportClient().handle(cr);

			JSONObject answer = response.getEntity(JSONObject.class);
			String answerKey = (String) answer.get("key");

			com.atlassian.jira.rest.client.domain.Issue i = service.getIssueClient().getIssue(parentIssue.getKey(), progressMonitor);
			com.atlassian.jira.rest.client.domain.Subtask createdSubtask = null;
			for (com.atlassian.jira.rest.client.domain.Subtask s : i.getSubtasks()) {
				if (s.getIssueKey().equals(answerKey)) {
					createdSubtask = s;
					break;
				}
			}

			return new RestSubtask(createdSubtask, parentIssue, this);
		} catch (JSONException | URISyntaxException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public ParentIssue createParentIssue(final ParentIssueInput newIssue) {
		User assignee = newIssue.getAssignee();
		User reporter = newIssue.getReporter();
		String summary = newIssue.getSummary();
		String description = newIssue.getDescription();
		Date duedate = newIssue.getDuedate();

		Iterable<Version> affectsVersions = newIssue.getAffectsVersions();
		Iterable<Version> fixVersions = newIssue.getFixVersions();

		Project project = newIssue.getProject();
		Priority priority = newIssue.getPriority();
		IssueType issueType = newIssue.getIssueType();

		BasicUser bAssignee = service.getUserClient().getUser(assignee.getName(), progressMonitor);
		BasicUser bReporter = service.getUserClient().getUser(reporter.getName(), progressMonitor);

		BasicProject bProject = service.getProjectClient().getProject(project.getKey(), progressMonitor);

		Iterable<com.atlassian.jira.rest.client.domain.Version> versions = service.getProjectClient().getProject(project.getKey(), progressMonitor).getVersions();
		LinkedList<com.atlassian.jira.rest.client.domain.Version> generatedAffectsVersions = new LinkedList<>();
		if (affectsVersions != null) {
			for (Version vi : affectsVersions) {
				for (com.atlassian.jira.rest.client.domain.Version v : versions) {
					if (v.getName().equals(vi.getName())) {
						generatedAffectsVersions.add(v);
						break;
					}
				}
			}


		}
		LinkedList<com.atlassian.jira.rest.client.domain.Version> generatedFixVersions = new LinkedList<>();
		if (fixVersions != null) {
			for (Version vi : fixVersions) {
				for (com.atlassian.jira.rest.client.domain.Version v : versions) {
					if (v.getName().equals(vi.getName())) {
						generatedFixVersions.add(v);
						break;
					}
				}
			}
		}
		BasicPriority bPriority = null;
		Iterable<com.atlassian.jira.rest.client.domain.Priority> priorities = service.getMetadataClient().getPriorities(progressMonitor);
		for (com.atlassian.jira.rest.client.domain.Priority p : priorities) {
			if (p.getName().equals(priority.getName())) {
				bPriority = p;
				break;
			}
		}

		BasicIssueType bIssueType = null;
		Iterable<com.atlassian.jira.rest.client.domain.IssueType> issueTypes = service.getMetadataClient().getIssueTypes(progressMonitor);
		for (com.atlassian.jira.rest.client.domain.IssueType ii : issueTypes) {
			if (ii.getName().equals(issueType.getName())) {
				bIssueType = ii;
				break;
			}
		}

		IssueInputBuilder builder = new IssueInputBuilder(bProject, bIssueType);
		builder.setAffectedVersions(generatedAffectsVersions);
		builder.setFixVersions(generatedFixVersions);
		builder.setSummary(summary);
		builder.setReporter(bReporter);
		builder.setAssignee(bAssignee);
		builder.setPriority(bPriority);
		if (description != null) {
			builder.setDescription(description);
		}
		if (duedate != null) {
			builder.setDueDate(new DateTime(duedate.getTime()));
		}

		IssueInput issueInput = builder.build();

		BasicIssue bi = service.getIssueClient().createIssue(issueInput, progressMonitor);

		com.atlassian.jira.rest.client.domain.Issue i = service.getIssueClient().getIssue(bi.getKey(), progressMonitor);

		return new RestParentIssue(i, this);
	}

	@Override
	public IssueType getIssueTypeByName(final String issueTypeName) {

		RestIssueType answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.IssueType> issueTypes = service.getMetadataClient().getIssueTypes(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.IssueType it : issueTypes) {
				if (it.getName().equals(issueTypeName)) {
					answer = new RestIssueType(it);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No issue type could be found with name \'" + issueTypeName + "\'");
		}

		return answer;

	}

	@Override
	public Priority getPriorityByName(final String priorityName) {

		RestPriority answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Priority> priorityTypes = service.getMetadataClient().getPriorities(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Priority p : priorityTypes) {
				if (p.getName().equals(priorityName)) {
					answer = new RestPriority(p);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No priority could be found with name \'" + priorityName + "\'");
		}

		return answer;

	}

	@Override
	public Resolution getResolutionByName(final String resolutionName) {

		RestResolution answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Resolution> resolutions = service.getMetadataClient().getResolutions(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Resolution r : resolutions) {
				if (r.getName().equals(resolutionName)) {
					answer = new RestResolution(r);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No resolution could be found with name \'" + resolutionName + "\'");
		}

		return answer;
	}

	@Override
	public Status getStatusByName(final String statusName) {

		RestStatus answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Status> statuses = service.getMetadataClient().getStatuses(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.Status s : statuses) {
				if (s.getName().equals(statusName)) {
					answer = new RestStatus(s);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No status could be found with name \'" + statusName + "\'");
		}

		return answer;

	}

	@Override
	public Worklog addWorklog(final Issue issue, final WorklogInput worklog) {
		try {
			WorklogInputBuilder builder = new WorklogInputBuilder(service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getSelf());
			builder.setStartDate(new DateTime(worklog.getStartDate().getTime()));
			builder.setMinutesSpent(worklog.getTimeSpentInMinutes());
			builder.setComment(worklog.getComment());

			com.atlassian.jira.rest.client.domain.input.WorklogInput worklogInput = builder.build();

			// process: add worklog
			service.getIssueClient().addWorklog(service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getWorklogUri(), worklogInput, progressMonitor);

			Iterable<com.atlassian.jira.rest.client.domain.Worklog> worklogs = service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getWorklogs();
			Iterator<com.atlassian.jira.rest.client.domain.Worklog> iter = worklogs.iterator();
			RestWorklog answerWorklog = null;
			while (iter.hasNext()) {
				answerWorklog = new RestWorklog(iter.next(), this);
			}
			return answerWorklog;
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public IssueType getSubtaskType(final String id) {

		RestIssueType answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.IssueType> issueTypes = service.getMetadataClient().getIssueTypes(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.IssueType it : issueTypes) {
				if (String.valueOf(it.getId()).equals(id) && it.isSubtask()) {
					answer = new RestIssueType(it);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No subtask type could be found with id \'" + id + "\'");
		}

		return answer;

	}

	@Override
	public Comment editComment(final Issue issue, final Comment comment, final String newBody) {
		try {
			String host = jiraServerURL.getHost();
			String protocol = jiraServerURL.getProtocol();
			int port = jiraServerURL.getPort();

			String buildedUri = protocol + "://" + host + ":" + String.valueOf(port) + "/rest/api/latest/issue/" + issue.getKey() + "/comment/" + comment.getId();

			JSONObject requestMessage = new JSONObject();

			requestMessage.put("body", newBody);

			ClientRequest cr = new ClientRequestImpl(new URI(buildedUri), "PUT", requestMessage);

			// process: edit comment
			getApacheHttpClient().handle(cr);

			RestComment createdComment = null;
			for (com.atlassian.jira.rest.client.domain.Comment c : service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getComments()) {
				if (c.getId().toString().equals(comment.getId())) {
					createdComment = new RestComment(c);
					break;
				}
			}
			return createdComment;
		} catch (JSONException | URISyntaxException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public IssueType getSubtaskTypeByName(final String subtaskTypeName) {

		RestIssueType answer = null;

		try {
			Iterable<com.atlassian.jira.rest.client.domain.IssueType> issueTypes = service.getMetadataClient().getIssueTypes(progressMonitor);

			for (com.atlassian.jira.rest.client.domain.IssueType it : issueTypes) {
				if (it.getName().equals(subtaskTypeName) && it.isSubtask()) {
					answer = new RestIssueType(it);
					break;
				}
			}
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

		if (answer == null) {
			throw new JiraObjectNotFoundException("No subtask type could be found with name \'" + subtaskTypeName + "\'");
		}

		return answer;

	}

	@Override
	public Iterable<Comment> getComments(final Issue issue) {

		try {
			Iterable<com.atlassian.jira.rest.client.domain.Comment> inList = service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getComments();
			List<Comment> outList = new ArrayList<>();

			for (com.atlassian.jira.rest.client.domain.Comment c : inList) {
				outList.add(new RestComment(c));
			}

			return Collections.unmodifiableList(outList);

		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

	}

	@Override
	public Iterable<Worklog> getWorklogs(final Issue issue) {
		try {
			Iterable<com.atlassian.jira.rest.client.domain.Worklog> inList = service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getWorklogs();
			List<Worklog> outList = new ArrayList<>();
			for (com.atlassian.jira.rest.client.domain.Worklog w : inList) {
				outList.add(new RestWorklog(w, this));
			}
			return Collections.unmodifiableList(outList);
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}

	}

	@Override
	public Iterable<CustomField> getCustomFields(final Issue issue) {
		try {
			Iterable<Field> inList = service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getFields();
			List<CustomField> outList = new ArrayList<>();
			for (Field f : inList) {
				outList.add(new CustomFieldImpl(f.getId(), f.getName(), f.getType(), f.getValue()));
			}
			return Collections.unmodifiableList(outList);
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public String getJiraVersion() {
		try {
			return service.getMetadataClient().getServerInfo(progressMonitor).getVersion();
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Version> getProjectVersions(final Project project) {
		try {
			Iterable<com.atlassian.jira.rest.client.domain.Version> inList = service.getProjectClient().getProject(project.getKey(), progressMonitor).getVersions();
			List<Version> outList = new ArrayList<>();
			for (com.atlassian.jira.rest.client.domain.Version v : inList) {
				outList.add(new RestVersion(v));
			}
			return Collections.unmodifiableList(outList);
		} catch (RestClientException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public ParentIssue getParentIssue(final String issueKey) {
		Issue answer = getIssue(issueKey);
		if (!answer.getIssueType().isSubtask()) {
			return (ParentIssue) answer;
		} else {
			throw new JiraClientException("Found issue is not a parent");
		}
	}

	/**
	 * Service method. see using in RestSubtask - getAffectsVersions() and getFixVersions().
	 */
	Version getVersion(final String id, final String projectKey) throws JiraClientException {
		Version answer = null;
		Iterable<Version> versions = getProjectVersions(getProject(projectKey));
		for (Version vi : versions) {
			if (id.equals(vi.getId())) {
				answer = vi;
				break;
			}
		}
		if (answer == null) {
			throw new JiraObjectNotFoundException("Could not found version with id: " + id);
		}
		return answer;
	}

	@Override
	public Iterable<SearchFilter> getFilters() {
		try {
			Iterable<FavouriteFilter> inList = service.getSearchClient().getFavouriteFilters(new NullProgressMonitor());
			List<SearchFilter> outList = new ArrayList<>();
			for (FavouriteFilter ff : inList) {
				outList.add(new RestSearchFilter(ff));
			}
			return Collections.unmodifiableList(outList);
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public Iterable<Issue> getIssuesByFilter(final SearchFilter filter, final int maxResults) {
		try {
			Iterable<? extends BasicIssue> inList = service.getSearchClient().searchJql(filter.getQuery(), progressMonitor).getIssues();
			List<Issue> outList = new ArrayList<>();
			for (BasicIssue bi : inList) {
				outList.add(getIssue(bi.getKey()));
			}
			return Collections.unmodifiableList(outList);
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public boolean addAttachmentToIssue(final Issue issue, final AttachmentInput attachment) {
		try {
			com.atlassian.jira.rest.client.domain.input.AttachmentInput input = new com.atlassian.jira.rest.client.domain.input.AttachmentInput(attachment.getFilename(), attachment.getFileInputStream());
			service.getIssueClient().addAttachments(progressMonitor, service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getAttachmentsUri(), input);
			return true;
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Attachment> getAttachmentsFromIssue(final Issue issue) {
		try {
			Iterable<com.atlassian.jira.rest.client.domain.Attachment> inList = service.getIssueClient().getIssue(issue.getKey(), progressMonitor).getAttachments();
			List<Attachment> outList = new ArrayList<>();
			for (com.atlassian.jira.rest.client.domain.Attachment a : inList) {
				outList.add(new RestAttachment(a, this));
			}
			return Collections.unmodifiableList(outList);
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}
}
