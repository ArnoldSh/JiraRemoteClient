package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.RemoteComponent;
import org.radixware.jiraclient.wrap.Component;

/**
 * Created by ashamsutdinov on 12.10.2017.
 */
public class SoapComponent implements Component {
    
    protected final RemoteComponent component;
    
    protected SoapComponent(RemoteComponent component) {
        this.component = component;
    }
    
    @Override
    public String getId() {
        return this.component.getId();
    }
    
    @Override
    public String getName() {
        return this.component.getName();
    }
    
    @Override
    public String getDescription() {
        return "";
    }
}
