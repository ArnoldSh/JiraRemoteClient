/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteProject;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Project;
import org.radixware.jiraclient.wrap.User;

/**
 *
 * @author ashamsutdinov
 */
public class SoapProject extends SoapClientContainer implements Project {

	protected final RemoteProject project;

	/**
	 * @param owner link to the actual client instance
	 */
	protected SoapProject(final RemoteProject project, final SoapJiraClient owner) {
		super(owner);
		this.project = project;
	}

	@Override
	public String getId() {
		return project.getId();
	}

	@Override
	public String getName() {
		return project.getName();
	}

	@Override
	public String getKey() {
		return project.getKey();
	}

	@Override
	public String getDescription() {
		return project.getDescription();
	}

	@Override
	public User getLead() {
		return getOwner().getUser(project.getLead());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapProject) {
			SoapProject that = (SoapProject) obj;
			return Objects.equals(this.project, that.project);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(this.project);
		return hash;
	}
}
