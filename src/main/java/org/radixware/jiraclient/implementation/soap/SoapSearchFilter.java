/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteFilter;
import java.util.Objects;
import org.radixware.jiraclient.wrap.SearchFilter;

/**
 *
 * @author ashamsutdinov
 */
public class SoapSearchFilter implements SearchFilter {

	protected RemoteFilter filter;

	protected SoapSearchFilter(final RemoteFilter filter) {
		this.filter = filter;
	}

	@Override
	public String getId() {
		return filter.getId();
	}

	@Override
	public String getQuery() {
		return filter.getDescription();
	}

	@Override
	public String getName() {
		return filter.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapSearchFilter) {
			SoapSearchFilter that = (SoapSearchFilter) obj;
			return Objects.equals(this.filter, that.filter);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.filter);
		return hash;
	}
	
}
