package com.cloudbees.netbeans.modules.service.plugin.actions;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.util.logging.Logger;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author David BRASSELY
 */
public class ViewApplicationLogAction extends NodeAction {
    
    protected static Logger getLogger() {
        return Logger.getLogger(ViewApplicationLogAction.class.getName());
    }
    
    @Override
    protected void performAction(Node[] nodes) {
        if (nodes == null || nodes.length != 1) {
            getLogger().fine("No node is selected for viewing application log");
            return;
        }
        
        CloudbeesInstance instance = nodes[0].getLookup().lookup(CloudbeesInstance.class);
        ApplicationInfo application = nodes[0].getLookup().lookup(ApplicationInfo.class);
        if ( instance == null ) {
            getLogger().fine("Cloudbees instance is NULL");
            return; 
        }
        if (!application.getStatus().equals("active")) {
            getLogger().fine("Application " + application.getId() +" not active");
            return;
        }
        
        instance.showApplicationLogWindow(application.getId());
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
        return NbBundle.getMessage(ViewApplicationLogAction.class, "LBL_ViewApplicationLogAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ViewApplicationLogAction.class);
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
