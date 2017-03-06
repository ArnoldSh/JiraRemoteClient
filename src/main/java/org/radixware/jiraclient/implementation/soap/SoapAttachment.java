/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteAttachment;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.radixware.jiraclient.exception.JiraClientException;
import org.radixware.jiraclient.wrap.Attachment;

/**
 *
 * @author ashamsutdinov
 */
public class SoapAttachment extends SoapClientContainer implements Attachment {

	protected RemoteAttachment attachment;

	protected SoapAttachment(final RemoteAttachment attachment, final SoapJiraClient owner) {
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
		return attachment.getFilesize().intValue();
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
