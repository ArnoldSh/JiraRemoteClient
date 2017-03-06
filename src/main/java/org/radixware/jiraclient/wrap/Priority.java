/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Representation of issue priority (for ex.: blocker, minor, info request and
 * etc.).
 *
 * @author ashamsutdinov
 */
public interface Priority {

	String getId();

	String getName();

	String getDescription();

	String getColor();
}
