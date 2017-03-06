/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.net.URI;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Project;
import org.radixware.jiraclient.wrap.User;

/**
 *
 * @author ashamsutdinov
 */
public class RestProject extends RestClientContainer implements Project {

	protected final com.atlassian.jira.rest.client.domain.Project project;

	/**
	 * @param owner link to the actual client instance
	 */
	protected RestProject(final com.atlassian.jira.rest.client.domain.Project project, final RestJiraClient owner) {
		super(owner);
		this.project = project;
	}

	@Override
	public String getId() {
		return project.getId().toString();
	}

	@Override
	public String getName() {
		return project.getName();
	}

	@Override
	public String getKey() {
		return project.getKey();
	}

	public URI getSelfUri() {
		return project.getSelf();
	}

	@Override
	public String getDescription() {
		return project.getDescription();
	}

	@Override
	public User getLead() {
		return getOwner().getUser(project.getLead().getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestProject) {
			RestProject that = (RestProject) obj;
			return Objects.equals(this.project, that.project);

		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.project);
		return hash;
	}
}
