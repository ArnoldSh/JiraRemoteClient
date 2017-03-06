/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteUser;
import java.util.Objects;
import org.radixware.jiraclient.wrap.User;

/**
 *
 * @author ashamsutdinov
 */
public class SoapUser implements User {

	protected final RemoteUser user;

	protected SoapUser(final RemoteUser user) {
		this.user = user;
	}

	@Override
	public String getName() {
		return user.getName();
	}

	@Override
	public String getDisplayName() {
		return user.getFullname();
	}

	@Override
	public String getEmail() {
		return user.getEmail();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapUser) {
			SoapUser that = (SoapUser) obj;
			return Objects.equals(this.user, that.user);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + Objects.hashCode(this.user);
		return hash;
	}
}
