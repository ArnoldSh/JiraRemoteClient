/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Interface represents custom fields in JIRA. Allows to get field value in raw
 * data format (returns lava.lang.Object).
 *
 * @author ashamsutdinov
 */
public interface CustomField {

	String getId();

	String getName();

	String getType();

	Object getValue();
}
