/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Representation of issues status (state of workflow).
 *
 * @author ashamsutdinov
 */
public interface Status {

	String getId();

	String getName();

	String getDescription();
}
