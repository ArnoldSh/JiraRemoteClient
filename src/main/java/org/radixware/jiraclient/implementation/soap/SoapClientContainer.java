/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import java.util.Objects;

/**
 * Provides an opportunity to get client parent link.
 *
 * @author ashamsutdinov
 */
public class SoapClientContainer {

	private final SoapJiraClient owner;

	protected SoapClientContainer(final SoapJiraClient owner) {
		this.owner = owner;
	}

	public SoapJiraClient getOwner() {
		return owner;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapClientContainer) {
			SoapClientContainer that = (SoapClientContainer) obj;
			return Objects.equals(this.getOwner(), that.getOwner());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 47 * hash + Objects.hashCode(this.owner);
		return hash;
	}
}
