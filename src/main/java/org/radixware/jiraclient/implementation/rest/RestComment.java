/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.util.Date;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Comment;

/**
 * @author ashamsutdinov
 */
public class RestComment implements Comment {

	protected final com.atlassian.jira.rest.client.domain.Comment comment;

	protected RestComment(final com.atlassian.jira.rest.client.domain.Comment comment) {
		this.comment = comment;
	}

	@Override
	public String getId() {
		return comment.getId().toString();
	}

	@Override
	public Date getCreationDate() {
		return comment.getCreationDate().toDate();
	}

	@Override
	public String getBody() {
		return comment.getBody();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RestComment) {
			RestComment that = (RestComment) obj;
			return Objects.equals(this.comment, that.comment);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.comment);
		return hash;
	}
}
