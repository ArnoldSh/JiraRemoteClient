/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Representation of JIRA issue type (for ex.: bug, task, question, improvement
 * and etc).
 *
 * @author ashamsutdinov
 */
public interface IssueType {

	String getId();

	String getName();

	String getDescription();

	boolean isSubtask();
}
