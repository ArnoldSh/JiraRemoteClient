/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.net.URI;
import java.util.Objects;
import org.radixware.jiraclient.wrap.User;

/**
 *
 * @author ashamsutdinov
 */
public class RestUser implements User {

	protected final com.atlassian.jira.rest.client.domain.User user;

	protected RestUser(final com.atlassian.jira.rest.client.domain.User user) {
		this.user = user;
	}

	@Override
	public String getName() {
		return user.getName();
	}

	@Override
	public String getDisplayName() {
		return user.getDisplayName();
	}

	public URI getSelfUri() {
		return user.getSelf();
	}

	@Override
	public String getEmail() {
		return user.getEmailAddress();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestUser) {
			RestUser that = (RestUser) obj;
			return Objects.equals(this.user, that.user);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.user);
		return hash;
	}
}
