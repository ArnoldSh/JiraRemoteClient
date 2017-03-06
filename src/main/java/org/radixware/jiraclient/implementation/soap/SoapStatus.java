/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteStatus;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Status;

/**
 *
 * @author ashamsutdinov
 */
public class SoapStatus implements Status {

	protected final RemoteStatus status;

	protected SoapStatus(final RemoteStatus status) {
		this.status = status;
	}

	@Override
	public String getId() {
		return status.getId();
	}

	@Override
	public String getName() {
		return status.getName();
	}

	@Override
	public String getDescription() {
		return status.getDescription();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapStatus) {
			SoapStatus that = (SoapStatus) obj;
			return Objects.equals(this.status, that.status);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.status);
		return hash;
	}
}
