/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.net.URI;
import java.util.Date;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Version;

/**
 *
 * @author ashamsutdinov
 */
public class RestVersion implements Version {

	protected final com.atlassian.jira.rest.client.domain.Version version;

	protected RestVersion(final com.atlassian.jira.rest.client.domain.Version version) {
		this.version = version;
	}

	@Override
	public String getDescription() {
		return version.getDescription();
	}

	@Override
	public String getName() {
		return version.getName();
	}

	@Override
	public boolean isArchived() {
		return version.isArchived();
	}

	@Override
	public boolean isReleased() {
		return version.isReleased();
	}

	@Override
	public String getId() {
		return version.getId().toString();
	}

	public URI getSelfUri() {
		return version.getSelf();
	}

	@Override
	public Date getReleaseDate() {
		return version.getReleaseDate().toDate();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestVersion) {
			RestVersion that = (RestVersion) obj;
			return Objects.equals(this.version, that.version);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.version);
		return hash;
	}
}
