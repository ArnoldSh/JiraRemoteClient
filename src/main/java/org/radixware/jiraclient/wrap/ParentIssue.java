/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Parent issue containing references to all subtasks.
 *
 * @author ashamsutdinov
 */
public interface ParentIssue extends Issue {

	Iterable<Subtask> getAllSubtasks();
}
