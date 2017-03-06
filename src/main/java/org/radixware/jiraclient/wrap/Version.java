/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

import java.util.Date;

/**
 * Version of JIRA project.
 *
 * @author ashamsutdinov
 */
public interface Version {

	String getId();

	String getDescription();

	String getName();

	boolean isArchived();

	boolean isReleased();

	Date getReleaseDate();
}
