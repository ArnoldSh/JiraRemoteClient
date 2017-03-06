/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.net.URI;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Resolution;

/**
 *
 * @author ashamsutdinov
 */
public class RestResolution implements Resolution {

	protected final com.atlassian.jira.rest.client.domain.Resolution resolution;

	protected RestResolution(final com.atlassian.jira.rest.client.domain.Resolution resolution) {
		this.resolution = resolution;
	}

	@Override
	public String getId() {
		return resolution.getId().toString();
	}

	@Override
	public String getName() {
		return resolution.getName();
	}

	@Override
	public String getDescription() {
		return resolution.getDescription();
	}

	public URI getSelfUri() {
		return resolution.getSelf();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestResolution) {
			RestResolution that = (RestResolution) obj;
			return Objects.equals(this.resolution, that.resolution);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.resolution);
		return hash;
	}
}
