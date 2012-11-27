package com.cloudbees.netbeans.modules.service.plugin;

import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import com.cloudbees.netbeans.modules.service.plugin.model.DefaultCloudbeesInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author David BRASSELY
 */
public class CloudbeesPreferences {
    
    private static final Logger LOG = Logger.getLogger(CloudbeesPreferences.class.getName());
    
    public static final String CLOUDBEES_INSTANCE_PREF_NAME = "CloudbeesInstance";
    
    private static Preferences getPreferences() {
        return NbPreferences.forModule(CloudbeesPreferences.class);
    }
    
    public static List<CloudbeesInstance> loadInstances() {
        List<CloudbeesInstance> list = new ArrayList<CloudbeesInstance>();
        try {
            Preferences prefs = getPreferences();
            String[] children = prefs.childrenNames();
            for (String prefName : children) {
                Preferences instancePrefs = prefs.node(prefName);
                CloudbeesInstance instance = DefaultCloudbeesInstance.newInstance(instancePrefs);
                if ( instance != null) {
                    list.add(instance);
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    private static Preferences getPreferences(String instancePrefName) {
       return  (getPreferences()).node(instancePrefName);
    }
    
    public static Preferences newInstancePreferences() {
        try {
            Preferences prefs = getPreferences();
            String[] children = prefs.childrenNames();
            List<String> list = Arrays.asList(children);
            String baseName = CLOUDBEES_INSTANCE_PREF_NAME;
            String prefName = baseName;
            for (int i = 0; list.contains(prefName) && i < 100; ++i) {
                prefName = baseName + i;
            }
            return prefs.node(prefName);
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static void saveInstancePreferences(CloudbeesInstance instance) {
        Preferences prefs = getPreferences(instance.getPreferenceName()); 
        instance.save(prefs);
    }
}
