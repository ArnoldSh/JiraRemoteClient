/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.common;

/**
 * Common implementation of JIRA custom field.
 * @author ashamsutdinov
 */
public class CustomFieldImpl implements org.radixware.jiraclient.wrap.CustomField {

	protected final String id;
	protected final String name;
	protected final String type;
	protected final Object value;

	public CustomFieldImpl(String id, String name, String type, Object value) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Object getValue() {
		return value;
	}
}
