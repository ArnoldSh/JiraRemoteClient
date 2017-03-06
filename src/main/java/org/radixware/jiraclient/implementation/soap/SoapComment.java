/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteComment;
import java.util.Date;
import java.util.Objects;
import org.radixware.jiraclient.wrap.Comment;

/**
 *
 * @author ashamsutdinov
 */
public class SoapComment implements Comment {

	protected final RemoteComment comment;

	protected SoapComment(final RemoteComment comment) {
		this.comment = comment;
	}

	@Override
	public String getId() {
		return comment.getId();
	}

	@Override
	public Date getCreationDate() {
		return comment.getCreated().getTime();
	}

	@Override
	public String getBody() {
		return comment.getBody();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoapComment) {
			SoapComment that = (SoapComment) obj;
			return Objects.equals(this.comment, that.comment);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 17 * hash + Objects.hashCode(this.comment);
		return hash;
	}
}
