/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap.input;

import java.io.FileInputStream;

/**
 * New input attachment.
 * @author ashamsutdinov
 */
public interface AttachmentInput {

	FileInputStream getFileInputStream();

	String getFilename();

	int getFilesize();
}
