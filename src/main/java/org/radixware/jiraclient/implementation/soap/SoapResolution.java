/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteResolution;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Resolution;

/**
 *
 * @author ashamsutdinov
 */
public class SoapResolution implements Resolution {

	protected final RemoteResolution resolution;

	protected SoapResolution(final RemoteResolution resolution) {
		this.resolution = resolution;
	}

	@Override
	public String getId() {
		return resolution.getId();
	}

	@Override
	public String getName() {
		return resolution.getName();
	}

	@Override
	public String getDescription() {
		return resolution.getDescription();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapResolution) {
			SoapResolution that = (SoapResolution) obj;
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
