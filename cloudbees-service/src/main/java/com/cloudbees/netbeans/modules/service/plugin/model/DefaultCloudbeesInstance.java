package com.cloudbees.netbeans.modules.service.plugin.model;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.ApplicationListResponse;
import com.cloudbees.api.BeesClient;
import com.cloudbees.api.BeesClientConfiguration;
import com.cloudbees.api.DatabaseInfo;
import com.cloudbees.api.DatabaseListResponse;
import com.cloudbees.netbeans.modules.service.plugin.client.CloudbeesClientManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;

/**
 *
 * @author David BRASSELY
 */
public class DefaultCloudbeesInstance implements CloudbeesInstance {

    protected static final Logger LOG = Logger.getLogger(DefaultCloudbeesInstance.class.getName());
    
    private String mPreferenceName;
    private String apiKey;
    private String secret;
    private ChangeSupport mChangeSupport;
    private CloudbeesClientManager clientMgr;
    
    private DefaultCloudbeesInstance() {
        this(null);
    }

    public DefaultCloudbeesInstance(Preferences prefs) {
        this.mPreferenceName = prefs.name();
        this.mChangeSupport = new ChangeSupport(this);
        this.clientMgr = new CloudbeesClientManager();
        this.load(prefs);
    }

    @Override
    public void load(Preferences prefs) {
        setApiKey(prefs.get(PROP_APIKEY, ""));
        setSecret(prefs.get(PROP_SECRET, ""));

        this.clientMgr.setApiKey(getApiKey());
        this.clientMgr.setSecret(getSecret());
    }

    @Override
    public void save(Preferences prefs) {
        prefs.put(PROP_APIKEY, getApiKey());
        prefs.put(PROP_SECRET, getSecret());

        this.clientMgr.setApiKey(getApiKey());
        this.clientMgr.setSecret(getSecret());
    }
    
    public static DefaultCloudbeesInstance newInstance(Preferences prefs) {
        DefaultCloudbeesInstance instance = new DefaultCloudbeesInstance(prefs);
        return instance;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public ChangeSupport getmChangeSupport() {
        return mChangeSupport;
    }

    public void setmChangeSupport(ChangeSupport mChangeSupport) {
        this.mChangeSupport = mChangeSupport;
    }

    @Override
    public List<ApplicationInfo> listApplicationInfos() {
        List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
        
        try {
            list = clientMgr.listApplicationInfos();
        } catch (Exception ex) {
             LOG.log(Level.FINE, ex.getMessage(), ex);
        }
        
        return list;
    }

    @Override
    public List<DatabaseInfo> listDatabaseInfos() {
        List<DatabaseInfo> list = new ArrayList<DatabaseInfo>();
        
        try {
            list = clientMgr.listDatabaseInfos();
        } catch (Exception ex) {
             LOG.log(Level.FINE, ex.getMessage(), ex);
        }
        
        return list;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        this.mChangeSupport.addChangeListener(l);
    }

    protected void fireChange() {
        this.mChangeSupport.fireChange();
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        this.mChangeSupport.removeChangeListener(l);
    }

    @Override
    public String getPreferenceName() {
        return this.mPreferenceName;
    }
}
