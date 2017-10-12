package org.radixware.jiraclient.implementation.rest;

import com.atlassian.jira.rest.client.domain.BasicComponent;
import org.radixware.jiraclient.wrap.Component;

/**
 * Created by ashamsutdinov on 12.10.2017.
 */
public class RestComponent implements Component {

    protected final BasicComponent component;
    
    protected RestComponent(BasicComponent component) {
        this.component = component;
    }
    
    @Override
    public String getId() {
        return this.component.getId().toString();
    }
    
    @Override
    public String getName() {
        return this.component.getName();
    }
    
    @Override
    public String getDescription() {
        return this.component.getDescription();
    }
}
