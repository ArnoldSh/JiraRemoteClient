/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import java.util.Date;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Version;

/**
 *
 * @author ashamsutdinov
 */
public class SoapVersion implements Version {

	protected final RemoteVersion version;

	protected SoapVersion(final RemoteVersion version) {
		this.version = version;
	}

	@Override
	public String getId() {
		return version.getId();
	}

	/**
	 * <p>SOAP API doesn't implement getDescription()</p>
	 *
	 * @throws UnsupportedOperationException
	 */
	@Override
	public String getDescription() {
		throw new UnsupportedOperationException("Not supported yet.");
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
	public Date getReleaseDate() {
		return version.getReleaseDate().getTime();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapVersion) {
			SoapVersion that = (SoapVersion) obj;
			return Objects.equals(this.version, that.version);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 13 * hash + Objects.hashCode(this.version);
		return hash;
	}
}
