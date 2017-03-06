/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

import java.io.InputStream;
import java.net.URL;

/**
 * Representation of JIRA attachment. Allows to get raw input stream or URL.
 *
 * @author ashamsutdinov
 */
public interface Attachment {

	InputStream getInputStream();
	
	/**
	 * @deprecated Method for debug. Use #getInputStream().
	 */
	@Deprecated 
	byte[]	getBytes();
	
	String getFilename();

	int getFilesize();

	URL getAttachmentURL();
}
