/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.common.input;

import org.radixware.jiraclient.wrap.input.CommentInput;

/**
 * Mutable comment.
 * @author ashamsutdinov
 */
public class CommentInputImpl implements CommentInput {

	protected String body;

	public CommentInputImpl(final String body) {
		this.body = body;
	}

	@Override
	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String getBody() {
		return body;
	}
}
