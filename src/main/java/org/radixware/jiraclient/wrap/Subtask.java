/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Subtask. Contains reference to a parent issue.
 *
 * @author ashamsutdinov
 */
public interface Subtask extends Issue {

	ParentIssue getParentIssue();
}
