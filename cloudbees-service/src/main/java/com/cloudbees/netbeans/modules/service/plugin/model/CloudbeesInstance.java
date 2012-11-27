package com.cloudbees.netbeans.modules.service.plugin.model;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.DatabaseInfo;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;

/**
 *
 * @author David BRASSELY
 */
public interface CloudbeesInstance {
    
    public static final String PROP_CLOUDBEES_INSTANCE = "CloudbeesInstance";
    
    public static final String PROP_APIKEY = "ApiKey";
    public static final String PROP_SECRET = "Secret";
    
    List<ApplicationInfo> listApplicationInfos();
    
    List<DatabaseInfo> listDatabaseInfos();
    
    String getPreferenceName();
    
    String getApiKey();
    void setApiKey(String apiKey);
    
    String getSecret();
    void setSecret(String secret);
    
    public void save(Preferences prefs);
    public void load(Preferences prefs);
    
    void addChangeListener(ChangeListener l);
    void removeChangeListener(ChangeListener l);
}
