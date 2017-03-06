/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.common.input;

import org.radixware.jiraclient.wrap.*;
import org.radixware.jiraclient.wrap.IssueType;
import org.radixware.jiraclient.wrap.input.SubtaskInput;

/**
 * Mutable subtask.
 * Note: Public constructor doesn't contain reference to project - link contains in the parent issue.
 * @author ashamsutdinov
 */
public class SubtaskInputImpl extends ParentIssueInputImpl implements SubtaskInput {

	protected final ParentIssue parentIssue;

	public SubtaskInputImpl(final String summary,
							final ParentIssue parentIssue,
							final Priority priority,
							final IssueType issueType,
							final User assignee,
							final User reporter) {

		super(summary, parentIssue.getProject(), priority, issueType, assignee, reporter);
		this.parentIssue = parentIssue;
	}

	public Issue getParentIssue() {
		return parentIssue;
	}
}
