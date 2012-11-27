package com.cloudbees.netbeans.modules.service.plugin.client;

import com.cloudbees.api.ApplicationDeleteResponse;
import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.ApplicationListResponse;
import com.cloudbees.api.ApplicationRestartResponse;
import com.cloudbees.api.ApplicationStatusResponse;
import com.cloudbees.api.BeesClient;
import com.cloudbees.api.BeesClientConfiguration;
import com.cloudbees.api.DatabaseDeleteResponse;
import com.cloudbees.api.DatabaseInfo;
import com.cloudbees.api.DatabaseListResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author David BRASSELY
 */
public class CloudbeesClientManager {

    private static Logger LOG = Logger.getLogger(CloudbeesClientManager.class.getName());
    private static final String API_HOST = "https://api.cloudbees.com/api";
    private static final String API_DATA_TYPE = "xml";
    private static final String API_VERSION = "1.0";
    private String apiKey;
    private String secret;
    private BeesClient client;

    public CloudbeesClientManager() {
    }

    protected void connect() throws Exception {
        if (client == null) {
            try {
                client = new BeesClient(new BeesClientConfiguration(
                        API_HOST, getApiKey(), getSecret(), API_DATA_TYPE, API_VERSION));

            } catch (Exception e) {
                LOG.info("Can not connect to Cloudbees API : " + API_HOST);
                throw e;
            }
        }
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<ApplicationInfo> listApplicationInfos() throws Exception {
        this.connect();
        
        ApplicationListResponse alr = client.applicationList();
        return alr.getApplications();
    }

    public List<DatabaseInfo> listDatabaseInfos() throws Exception {
        this.connect();
        
        DatabaseListResponse dlr = client.databaseList();
        return dlr.getDatabases();
    }
    
    public String startApplication(String applicationId) throws Exception {
        this.connect();
        
        ApplicationStatusResponse status = client.applicationStart(applicationId);
        return status.getStatus();
    }
    
    public String stopApplication(String applicationId) throws Exception {
        this.connect();
        
        ApplicationStatusResponse status = client.applicationStop(applicationId);
        return status.getStatus();
    }
    
    public boolean restartApplication(String applicationId) throws Exception {
        this.connect();
        
        ApplicationRestartResponse status = client.applicationRestart(applicationId);
        return status.isRestarted();
    }
    
    public boolean deleteApplication(String applicationId) throws Exception {
        this.connect();
        
        ApplicationDeleteResponse response = client.applicationDelete(applicationId);
        return response.isDeleted();
    }
    
    public void tailApplicationLog(String applicationId, String logName, OutputStream os) throws Exception {
        this.connect();
        
        client.tailLog(applicationId, logName, os);
    }
    
    public boolean deleteDatabase(String databaseId) throws Exception {
        this.connect();
        
        DatabaseDeleteResponse response = client.databaseDelete(databaseId);
        return response.isDeleted();
    }
}
