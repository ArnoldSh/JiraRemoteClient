/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap.input;

/**
 * New input subtask. This interface is an empty because JIRA subtask is
 * different from JIRA issue only the presence of link to parent issue and some
 * restrictions on the issue type - for subtask are available only subtask types
 * (for a clear definition see methods JiraClient.getSubtaskTypes() and
 * IssueType.isSubtask())
 *
 * @author ashamsutdinov
 */
public interface SubtaskInput extends ParentIssueInput {
}
