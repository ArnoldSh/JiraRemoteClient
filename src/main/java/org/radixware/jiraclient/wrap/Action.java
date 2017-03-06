/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Representation of JIRA workflow action (for ex.: default actions "Start
 * Progress", "Close Issue", "Resolve Issue" and etc).
 *
 * @author ashamsutdinov
 */
public interface Action {

	String getId();

	String getName();
}
