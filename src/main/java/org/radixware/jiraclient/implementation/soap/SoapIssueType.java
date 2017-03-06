/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteIssueType;
import java.util.Objects;
import org.radixware.jiraclient.wrap.IssueType;

/**
 *
 * @author ashamsutdinov
 */
public class SoapIssueType implements IssueType {

	protected final RemoteIssueType issueType;

	protected SoapIssueType(final RemoteIssueType issueType) {
		this.issueType = issueType;
	}

	@Override
	public String getId() {
		return issueType.getId();
	}

	@Override
	public String getName() {
		return issueType.getName();
	}

	@Override
	public String getDescription() {
		return issueType.getDescription();
	}

	@Override
	public boolean isSubtask() {
		return issueType.isSubTask();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapIssueType) {
			SoapIssueType that = (SoapIssueType) obj;
			return Objects.equals(this.issueType, that.issueType);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + Objects.hashCode(this.issueType);
		return hash;
	}
}
