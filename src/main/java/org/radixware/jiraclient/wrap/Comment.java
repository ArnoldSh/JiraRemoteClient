/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

import java.util.Date;

/**
 * JIRA issue comment.
 *
 * @author ashamsutdinov
 */
public interface Comment {

	String getId();

	Date getCreationDate();

	String getBody();
}
