/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap.input;

import java.util.Date;

/**
 * New input worklog.
 *
 * @author ashamsutdinov
 */
public interface WorklogInput {

	void setComment(final String comment);

	void setStartDate(final Date startDate);

	void setTimeSpentInMinutes(final int minutes);

	String getComment();

	Date getStartDate();

	int getTimeSpentInMinutes();
}
