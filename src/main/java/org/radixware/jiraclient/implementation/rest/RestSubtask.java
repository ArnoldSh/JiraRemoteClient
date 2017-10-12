/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.client.impl.ClientRequestImpl;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.radixware.jiraclient.exception.JiraClientException;
import org.radixware.jiraclient.wrap.*;

/**
 *
 * @author ashamsutdinov
 */
public class RestSubtask extends RestClientContainer implements Subtask {

	protected final com.atlassian.jira.rest.client.domain.Subtask subtask;
	protected final ParentIssue parentIssue;

	/**
	 * @param owner link to the actual client instance
	 */
	protected RestSubtask(final com.atlassian.jira.rest.client.domain.Subtask subtask, final ParentIssue parentIssue, final RestJiraClient owner) {
		super(owner);
		this.subtask = subtask;
		this.parentIssue = parentIssue;
	}

	@Override
	public String getId() {
		return subtask.getId().toString();
	}

	@Override
	public String getKey() {
		return subtask.getIssueKey();
	}

	@Override
	public IssueType getIssueType() {
		return new RestIssueType(subtask.getIssueType());
	}

	@Override
	public Status getStatus() {
		return new RestStatus(subtask.getStatus());
	}

	@Override
	public String getSummary() {
		return subtask.getSummary();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestSubtask) {
			RestSubtask that = (RestSubtask) obj;
			return Objects.equals(this.subtask, that.subtask)
					&& Objects.equals(this.parentIssue, that.parentIssue);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(this.subtask);
		hash = 59 * hash + Objects.hashCode(this.parentIssue);
		return hash;
	}

	private JSONObject getResponse() {
		try {
			String host = getOwner().getJiraServerURL().getHost();
			String protocol = getOwner().getJiraServerURL().getProtocol();
			int port = getOwner().getJiraServerURL().getPort();

			String buildedUri = protocol + "://" + host + ":" + String.valueOf(port) + "/rest/api/latest/issue/" + subtask.getIssueKey();

			ClientRequest cr = new ClientRequestImpl(new URI(buildedUri), "GET");
			ClientResponse response = getOwner().getApacheHttpClient().handle(cr);

			JSONObject answer = response.getEntity(JSONObject.class);

			return answer;
		} catch (URISyntaxException ex) {
			throw new JiraClientException(ex);
		}
	}

	private Date parseJsonDate(Object jsonDate) {
		try {
			String dateStr = (String) jsonDate;
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.parse(dateStr);
			return sdf.getCalendar().getTime();
		} catch (ParseException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public String getDescription() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			return fields.getString("description");
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Date getCreatedDate() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			Object dateObj = fields.get("created");
			Date date = null;
			if (dateObj != null) {
				date = parseJsonDate(dateObj);
			}
			return date;
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Date getDuedate() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			Object dateObj = fields.get("duedate");
			Date date = null;
			if (dateObj != null) {
				date = parseJsonDate(dateObj);
			}
			return date;
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Date getUpdatedDate() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			Object dateObj = fields.get("updated");
			Date date = null;
			if (dateObj != null) {
				date = parseJsonDate(dateObj);
			}
			return date;
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Project getProject() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			JSONObject project = fields.getJSONObject("project");
			String key = project.getString("key");
			return getOwner().getProject(key);
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Resolution getResolution() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			JSONObject resolution = fields.getJSONObject("resolution");
			String id = resolution.getString("id");
			return getOwner().getResolution(id);
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public User getReporter() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			JSONObject user = fields.getJSONObject("reporter");
			String name = user.getString("name");
			return getOwner().getUser(name);
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public User getAssignee() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			JSONObject user = fields.getJSONObject("assignee");
			String name = user.getString("name");
			return getOwner().getUser(name);
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Priority getPriority() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			JSONObject priority = fields.getJSONObject("priority");
			String id = priority.getString("id");
			return getOwner().getPriority(id);
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Version> getAffectsVersions() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			JSONArray affectsVersions = fields.getJSONArray("versions");

			String[] id = new String[affectsVersions.length()];
			String projectKey = fields.getJSONObject("project").getString("key");

			List<Version> outList = new ArrayList<>();

			for (int i = 0; i < affectsVersions.length(); i++) {
				id[i] = affectsVersions.getJSONObject(i).getString("id");
				outList.add(getOwner().getVersion(id[i], projectKey));
			}
			return Collections.unmodifiableList(outList);
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Version> getFixVersions() {
		try {
			JSONObject fields = getResponse().getJSONObject("fields");
			JSONArray fixVersions = fields.getJSONArray("fixVersions");

			String[] id = new String[fixVersions.length()];
			String projectKey = fields.getJSONObject("project").getString("key");

			List<Version> outList = new ArrayList<>();

			for (int i = 0; i < fixVersions.length(); i++) {
				id[i] = fixVersions.getJSONObject(i).getString("id");
				outList.add(getOwner().getVersion(id[i], projectKey));
			}
			return Collections.unmodifiableList(outList);
		} catch (JSONException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public ParentIssue getParentIssue() {
		return parentIssue;
	}
	
	@Override
	public Iterable<Component> getComponents() {
		return this.parentIssue.getComponents();
	}
}
