package com.cloudbees.netbeans.modules.service.plugin.model;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.ApplicationListResponse;
import com.cloudbees.api.BeesClient;
import com.cloudbees.api.BeesClientConfiguration;
import com.cloudbees.api.DatabaseInfo;
import com.cloudbees.api.DatabaseListResponse;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author David BRASSELY
 */
public class DefaultCloudbeesInstance implements CloudbeesInstance {

    private BeesClient client;
    private ChangeSupport mChangeSupport;
    
    private DefaultCloudbeesInstance() {
        this(null, null);
    }
    
    public DefaultCloudbeesInstance(String username, String password) {
        try {
            client = new BeesClient(new BeesClientConfiguration(
                    "https://api.cloudbees.com/api",
                    "apiKey",
                    "secret",
                    "xml", "1.0"));

            this.mChangeSupport = new ChangeSupport(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<ApplicationInfo> listApplicationInfos() {
        try {
            ApplicationListResponse alr = client.applicationList();
            return alr.getApplications();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isConnected() {
        return client != null;
    }

    @Override
    public List<DatabaseInfo> listDatabaseInfos() {
        try {
            DatabaseListResponse dlr = client.databaseList();
            return dlr.getDatabases();
        } catch (Exception e) {
            return null;
        }
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
}
