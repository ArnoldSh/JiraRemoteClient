/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.wrap;

/**
 * Searching filter. Allows to get issues by the query in JQL.
 *
 * @author ashamsutdinov
 */
public interface SearchFilter {

	String getId();

	String getQuery();

	String getName();
}
