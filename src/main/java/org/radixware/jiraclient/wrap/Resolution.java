/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Representation of issue resolution (state of workflow).
 *
 * @author ashamsutdinov
 */
public interface Resolution {

	String getId();

	String getName();

	String getDescription();
}
