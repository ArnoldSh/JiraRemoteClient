/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Information about specified JIRA user.
 *
 * @author ashamsutdinov
 */
public interface User {

	String getName();

	String getDisplayName();

	String getEmail();
}
