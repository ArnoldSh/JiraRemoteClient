/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Information about JIRA project.
 *
 * @author ashamsutdinov
 */
public interface Project {

	String getId();

	String getName();

	String getKey();

	String getDescription();

	User getLead();
}
