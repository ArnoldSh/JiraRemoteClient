/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

import java.util.Date;

/**
 * Information about the work done.
 *
 * @author ashamsutdinov
 */
public interface Worklog {

	String getId();

	User getAuthor();

	User getUpdateAuthor();

	String getComment();

	Date getCreationDate();

	Date getStartDate();

	Date getUpdateDate();

	Long getTimeSpentInMinutes();
}
