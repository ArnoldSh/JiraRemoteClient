/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.util.Objects;

/**
 * Provides an opportunity to get client parent link.
 * @author ashamsutdinov
 */
public class RestClientContainer {

	private final RestJiraClient owner;

	protected RestClientContainer(final RestJiraClient owner) {
		this.owner = owner;
	}

	public RestJiraClient getOwner() {
		return owner;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestClientContainer) {
			RestClientContainer that = (RestClientContainer) obj;
			return Objects.equals(this.getOwner(), that.getOwner());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.owner);
		return hash;
	}
}
