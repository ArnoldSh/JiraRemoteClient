/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import com.atlassian.jira.rest.client.domain.FavouriteFilter;
import org.radixware.jiraclient.wrap.SearchFilter;

/**
 *
 * @author ashamsutdinov
 */
public class RestSearchFilter implements SearchFilter {

	protected FavouriteFilter filter;

	protected RestSearchFilter(final FavouriteFilter filter) {
		this.filter = filter;
	}

	@Override
	public String getId() {
		return filter.getId().toString();
	}

	@Override
	public String getQuery() {
		return filter.getJql();
	}

	@Override
	public String getName() {
		return filter.getName();
	}
}
