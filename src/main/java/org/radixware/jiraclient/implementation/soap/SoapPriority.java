/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemotePriority;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Priority;

/**
 *
 * @author ashamsutdinov
 */
public class SoapPriority implements Priority {

	protected final RemotePriority priority;

	protected SoapPriority(final RemotePriority priority) {
		this.priority = priority;
	}

	@Override
	public String getId() {
		return priority.getId();
	}

	@Override
	public String getName() {
		return priority.getName();
	}

	@Override
	public String getDescription() {
		return priority.getDescription();
	}

	@Override
	public String getColor() {
		return priority.getColor();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapPriority) {
			SoapPriority that = (SoapPriority) obj;
			return Objects.equals(this.priority, that.priority);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(this.priority);
		return hash;
	}
}
