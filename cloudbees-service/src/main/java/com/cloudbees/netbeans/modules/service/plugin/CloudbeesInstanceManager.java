package com.cloudbees.netbeans.modules.service.plugin;

import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import com.cloudbees.netbeans.modules.service.plugin.model.DefaultCloudbeesInstance;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 *
 * @author David BRASSELY
 */
public class CloudbeesInstanceManager {
    
    private static final Logger LOG = Logger.getLogger(CloudbeesInstanceManager.class.getName());
    
    private static CloudbeesInstanceManager sMgr;
    private Map<String, CloudbeesInstance> mInstanceMap;
    
    PropertyChangeSupport mChangeSupport;
    
    protected CloudbeesInstanceManager() {
        this.mInstanceMap = new HashMap<String, CloudbeesInstance>();
        loadServerInstances();
        this.mChangeSupport = new PropertyChangeSupport(this);
    }
    
    public static synchronized CloudbeesInstanceManager getInstance() {
        if ( sMgr == null ) {
            sMgr = new CloudbeesInstanceManager();
        }
        return sMgr;
    }
    
    protected void loadServerInstances() {
        List<CloudbeesInstance> instanceList = CloudbeesPreferences.loadInstances();
        
        for ( CloudbeesInstance inst : instanceList ) {
            this.mInstanceMap.put("default", inst);
        }
    }
    
    public List<CloudbeesInstance> listCloudbeesInstances() {
        List<CloudbeesInstance> instanceList = new ArrayList<CloudbeesInstance>();
        instanceList.addAll(this.mInstanceMap.values());
        return instanceList;
    }
    
    public CloudbeesInstance newServerInstance() {
        Preferences prefs = CloudbeesPreferences.newInstancePreferences();
        CloudbeesInstance instance = DefaultCloudbeesInstance.newInstance(prefs);
        return instance;
    }
    
    public void saveCloudbeesInstance(CloudbeesInstance instance) {
        CloudbeesInstance oldInstance = this.mInstanceMap.put(instance.getPreferenceName(), instance);
        CloudbeesPreferences.saveInstancePreferences(instance);
        if ( oldInstance == null ) {
            // a new instance is added. so, fire the property change event.
            firePropertyChange(null, instance);
        }
        // notify change.
    }
    
    protected void firePropertyChange(CloudbeesInstance oldValue, CloudbeesInstance newValue) {
        this.mChangeSupport.firePropertyChange(CloudbeesInstance.PROP_CLOUDBEES_INSTANCE, oldValue, newValue);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.mChangeSupport.addPropertyChangeListener(l);
    }
}
