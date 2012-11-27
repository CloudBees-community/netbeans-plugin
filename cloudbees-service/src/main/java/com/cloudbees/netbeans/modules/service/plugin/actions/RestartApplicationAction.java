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
public class RestartApplicationAction extends NodeAction {
    
    protected static Logger getLogger() {
        return Logger.getLogger(RestartApplicationAction.class.getName());
    }
    
    @Override
    protected void performAction(Node[] nodes) {
        if (nodes == null || nodes.length != 1) {
            getLogger().fine("No node is selected for restarting application");
            return;
        }
        ApplicationInfo application = nodes[0].getLookup().lookup(ApplicationInfo.class);
        CloudbeesInstance instance = nodes[0].getLookup().lookup(CloudbeesInstance.class);
        restartApplication(instance, application); // run it in a separate thread.
    }
    
    public static void restartApplication(final CloudbeesInstance instance, final ApplicationInfo application) {
        if (application == null) {
            getLogger().fine("Application is NULL");
            return;
        }
        if (! application.getStatus().equals("active")) {
            getLogger().fine("Application " + application.getId() + " is not active");
            return;
        }
        
        //TODO: start the application.
        getLogger().info("Restarting the application " + application.getId());
        
        Runnable startThread = new Runnable() {
            public void run() {
                instance.restartApplication(application.getId());
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
            ApplicationInfo application = nodes[0].getLookup().lookup(ApplicationInfo.class);
            return ((application != null) && (application.getStatus().equals("active")));
        }
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(RestartApplicationAction.class, "LBL_RestartApplicationAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(RestartApplicationAction.class);
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
