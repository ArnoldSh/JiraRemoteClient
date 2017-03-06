/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.common.input;

import java.util.Date;
import org.radixware.jiraclient.wrap.input.WorklogInput;

/**
 * Mutable worklog.
 * @author ashamsutdinov
 */
public class WorklogInputImpl implements WorklogInput {

	protected String comment;
	protected Date startDate;
	protected int timeSpentInMinutes;

	public WorklogInputImpl(final String comment, final Date startDate, final int timeSpentInMinutes) {
		this.comment = comment;
		this.startDate = (Date) startDate.clone();
		this.timeSpentInMinutes = timeSpentInMinutes;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = (Date) startDate.clone();
	}

	@Override
	public void setTimeSpentInMinutes(int minutes) {
		this.timeSpentInMinutes = minutes;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public Date getStartDate() {
		return (Date) startDate.clone();
	}

	@Override
	public int getTimeSpentInMinutes() {
		return timeSpentInMinutes;
	}
}
