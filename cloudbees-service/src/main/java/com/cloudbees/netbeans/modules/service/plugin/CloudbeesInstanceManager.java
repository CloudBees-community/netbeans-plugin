package com.cloudbees.netbeans.modules.service.plugin;

import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import com.cloudbees.netbeans.modules.service.plugin.model.DefaultCloudbeesInstance;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
        // TODO: please use Netbeans preferences system.
        this.mInstanceMap.put("default", new DefaultCloudbeesInstance("", ""));
        
        /*
        List<CloudbeesInstance> instanceList = CloudbeesInstancePreferences.loadInstances();
        for ( CloudbeesInstance inst : instanceList ) {
            String root = inst.getRoot();
            if ( root != null && root.trim().length() > 0 && new File(root).exists() ) {
                this.mInstanceMap.put(inst.getPreferenceName(), inst);
            }
        }*/
    }
    
    public List<CloudbeesInstance> listCloudbeesInstances() {
        List<CloudbeesInstance> instanceList = new ArrayList<CloudbeesInstance>();
        instanceList.addAll(this.mInstanceMap.values());
        return instanceList;
    }
}
