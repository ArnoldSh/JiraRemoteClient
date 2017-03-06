/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.rest;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.radixware.jiraclient.exception.JiraClientException;
import org.radixware.jiraclient.wrap.Attachment;

/**
 *
 * @author ashamsutdinov
 */
public class RestAttachment extends RestClientContainer implements Attachment {

	protected com.atlassian.jira.rest.client.domain.Attachment attachment;

	protected RestAttachment(final com.atlassian.jira.rest.client.domain.Attachment attachment, final RestJiraClient owner) {
		super(owner);
		this.attachment = attachment;
	}

	/**
	 * @deprecated Method for debug. Use #getInputStream().
	 */
	@Deprecated
	@Override
	public byte[] getBytes() {
		return getOwner().getAttachmentBytes(getAttachmentURL());
	}

	@Override
	public InputStream getInputStream() {
		return getOwner().getAttachmentInputStream(getAttachmentURL());
	}
	
	@Override
	public String getFilename() {
		return attachment.getFilename();
	}

	@Override
	public int getFilesize() {
		return attachment.getSize();
	}

	@Override
	public URL getAttachmentURL() {
		try {
			return new URL(getOwner().getJiraServerURL() + "/secure/attachment/" + attachment.getId() + "/" + attachment.getFilename());
		} catch (MalformedURLException ex) {
			throw new JiraClientException(ex);
		}
	}
}
