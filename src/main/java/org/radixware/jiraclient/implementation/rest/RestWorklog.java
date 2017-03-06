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
import java.util.Date;
import java.util.Objects;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.radixware.jiraclient.exception.JiraClientException;
import org.radixware.jiraclient.wrap.User;
import org.radixware.jiraclient.wrap.Worklog;

/**
 *
 * @author ashamsutdinov
 */
public class RestWorklog extends RestClientContainer implements Worklog {

	protected final com.atlassian.jira.rest.client.domain.Worklog worklog;

	/**
	 * @param owner link to the actual client instance
	 */
	protected RestWorklog(final com.atlassian.jira.rest.client.domain.Worklog worklog, final RestJiraClient owner) {
		super(owner);
		this.worklog = worklog;
	}

	@Override
	public User getAuthor() {
		return getOwner().getUser(worklog.getAuthor().getName());
	}

	@Override
	public User getUpdateAuthor() {
		return getOwner().getUser(worklog.getUpdateAuthor().getName());
	}

	@Override
	public String getComment() {
		return worklog.getComment();
	}

	@Override
	public Date getCreationDate() {
		return worklog.getCreationDate().toDate();
	}

	@Override
	public Date getStartDate() {
		return worklog.getStartDate().toDate();
	}

	@Override
	public Date getUpdateDate() {
		return worklog.getUpdateDate().toDate();
	}

	@Override
	public Long getTimeSpentInMinutes() {
		return worklog.getMinutesSpent().longValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestWorklog) {
			RestWorklog that = (RestWorklog) obj;
			return Objects.equals(this.worklog, that.worklog);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this.worklog);
		return hash;
	}

	@Override
	public String getId() {
		try {
			String buildedUri = worklog.getIssueUri().toString() + "/worklog";
			
			ClientRequest cr = new ClientRequestImpl(new URI(buildedUri), "GET");
			ClientResponse response = getOwner().getApacheHttpClient().handle(cr);

			JSONArray answer = response.getEntity(JSONArray.class);
			
			for(int index = 0; index < answer.length(); index++) {
				Object obj = answer.get(index);
				if(obj instanceof JSONObject) {
					JSONObject jsonObj = (JSONObject) obj;
					if((jsonObj.getString("self")).equals(worklog.getSelf().toString())) {
						return jsonObj.getString("id");
					}
				}
			}
			throw new JiraClientException("Issue's worklog is not created yet.");
		} catch (URISyntaxException | JSONException ex) {
			throw new JiraClientException(ex);
		} 
	}
}
