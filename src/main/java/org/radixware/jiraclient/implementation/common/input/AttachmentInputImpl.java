/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.common.input;

import java.io.FileInputStream;
import org.radixware.jiraclient.wrap.input.AttachmentInput;

/**
 * Mutable attachment.
 * @author ashamsutdinov
 */
public class AttachmentInputImpl implements AttachmentInput {

	protected final FileInputStream in;
	protected final String filename;
	protected final int filesize;

	public AttachmentInputImpl(final FileInputStream in, final String filename, final int filesize) {
		this.in = in;
		this.filename = filename;
		this.filesize = filesize;
	}

	@Override
	public FileInputStream getFileInputStream() {
		return in;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public int getFilesize() {
		return filesize;
	}
}
