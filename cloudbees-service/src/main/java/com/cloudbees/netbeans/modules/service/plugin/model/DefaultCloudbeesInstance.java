package com.cloudbees.netbeans.modules.service.plugin.model;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.ApplicationListResponse;
import com.cloudbees.api.BeesClient;
import com.cloudbees.api.BeesClientConfiguration;
import com.cloudbees.api.DatabaseInfo;
import com.cloudbees.api.DatabaseListResponse;
import com.cloudbees.netbeans.modules.service.plugin.client.CloudbeesClientManager;
import com.cloudbees.netbeans.modules.service.plugin.io.IOHandler;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

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
            LOG.log(Level.WARNING, ex.getMessage(), ex);
        }

        return list;
    }

    @Override
    public List<DatabaseInfo> listDatabaseInfos() {
        List<DatabaseInfo> list = new ArrayList<DatabaseInfo>();

        try {
            list = clientMgr.listDatabaseInfos();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
        }

        return list;
    }

    @Override
    public String startApplication(String applicationId) {
        try {
            return clientMgr.startApplication(applicationId);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String stopApplication(String applicationId) {
        try {
            return clientMgr.stopApplication(applicationId);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public boolean restartApplication(String applicationId) {
        try {
            return clientMgr.restartApplication(applicationId);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean deleteApplication(String applicationId) {
        try {
            return clientMgr.deleteApplication(applicationId);
        } catch (Exception ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
            return false;
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

    @Override
    public String getPreferenceName() {
        return this.mPreferenceName;
    }

    @Override
    public void showApplicationLogWindow(final String applicationId) {
        final String logName = applicationId + " - Log";
        final InputOutput io = openApplicationLog(logName);

        try {
            PipedInputStream in = new PipedInputStream();
            final PipedOutputStream out = new PipedOutputStream(in);

            IOHandler mIOHandler = new IOHandler(io, in, out);

            new Thread(
                    new Runnable() {
                        public void run() {
                            try {
                                clientMgr.tailApplicationLog(applicationId, "server", out);
                            } catch (Exception e) {
                                io.getErr().println("ERROR :" + e.getMessage());
                            }
                        }
                    }).start();
        } catch (Exception e) {
        }

    }

    public InputOutput openApplicationLog(String logName) {
        InputOutput io = createApplicationLogIO(logName);
        io.select();
        return io;
    }

    private InputOutput createApplicationLogIO(String logName) {
        IOProvider ioProvider = IOProvider.getDefault();
        return ioProvider.getIO(logName, false);
    }
}
