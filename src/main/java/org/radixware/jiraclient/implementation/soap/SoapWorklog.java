/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteWorklog;
import java.util.Date;
import java.util.Objects;
import org.radixware.jiraclient.wrap.User;
import org.radixware.jiraclient.wrap.Worklog;

/**
 *
 * @author ashamsutdinov
 */
public class SoapWorklog extends SoapClientContainer implements Worklog {

	protected final RemoteWorklog worklog;

	/**
	 * @param owner link to the actual client instance
	 */
	protected SoapWorklog(final RemoteWorklog worklog, final SoapJiraClient owner) {
		super(owner);
		this.worklog = worklog;
	}

	@Override
	public User getAuthor() {
		return getOwner().getUser(worklog.getAuthor());
	}

	@Override
	public User getUpdateAuthor() {
		return getOwner().getUser(worklog.getUpdateAuthor());
	}

	@Override
	public String getComment() {
		return worklog.getComment();
	}

	@Override
	public Date getCreationDate() {
		return worklog.getCreated().getTime();
	}

	@Override
	public Date getStartDate() {
		return worklog.getStartDate().getTime();
	}

	@Override
	public Date getUpdateDate() {
		return worklog.getUpdated().getTime();
	}

	@Override
	public Long getTimeSpentInMinutes() {
		return worklog.getTimeSpentInSeconds() / 60;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapWorklog) {
			SoapWorklog that = (SoapWorklog) obj;
			return Objects.equals(this.worklog, that.worklog);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.worklog);
		return hash;
	}

	@Override
	public String getId() {
		return worklog.getId();
	}
}
