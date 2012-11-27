package com.cloudbees.netbeans.modules.service.plugin.actions;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.DatabaseInfo;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.util.logging.Logger;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author David BRASSELY
 */
public class DeleteDatabaseAction extends NodeAction {
    
    protected static Logger getLogger() {
        return Logger.getLogger(DeleteDatabaseAction.class.getName());
    }
    
    @Override
    protected void performAction(Node[] nodes) {
        if (nodes == null || nodes.length != 1) {
            getLogger().fine("No node is selected for deleting application");
            return;
        }
        DatabaseInfo database = nodes[0].getLookup().lookup(DatabaseInfo.class);
        CloudbeesInstance instance = nodes[0].getLookup().lookup(CloudbeesInstance.class);
        deleteDatabase(instance, database); // run it in a separate thread.
    }
    
    public static void deleteDatabase(final CloudbeesInstance instance, final DatabaseInfo database) {
        if (database == null) {
            getLogger().fine("Database is NULL");
            return;
        }
        
        //TODO: delete the application.
        getLogger().info("Deleting the database " + database.getName());
        
        Runnable startThread = new Runnable() {
            public void run() {
                instance.deleteDatabase(database.getName());
                /*
                try {
                    if (instance.isJMXAdminAvailable()) {
                        // already running an instance with this method. display notify dialog and return.
                        Object result = showConnectOrRestartDialog(instance);
                        if (CONNECT_OPTION.equals(result)) {
                            instance.connect();
                        } else if (RESTART_OPTION.equals(result)) {
                            instance.shutdownRemoteInstance();
                            instance.start();
                        } else {
                            // do nothing.
                        }
                    } else {
                        instance.start();
                    }
                } catch (Exception ex) {
                    // Exceptions.printStackTrace(ex);
                    UiUtils.showMessage(ex);
                }
                */
            }         
        };
        RequestProcessor.getDefault().post(startThread, 0, Thread.currentThread().getPriority());        
    }
    
    @Override
    protected boolean enable(Node[] nodes) {
        if (nodes != null && nodes.length == 1) {
            DatabaseInfo database = nodes[0].getLookup().lookup(DatabaseInfo.class);
            return database != null;
        }
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(DeleteDatabaseAction.class, "LBL_DeleteDatabaseAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(DeleteDatabaseAction.class);
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
