package com.cloudbees.netbeans.modules.service.plugin.actions;

import com.cloudbees.netbeans.modules.service.plugin.CloudbeesInstanceManager;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import com.cloudbees.netbeans.modules.service.plugin.ui.PreferenceWizard;
import java.util.logging.Logger;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author David BRASSEY
 */
public class CustomizeInstanceAction extends NodeAction {

    protected Logger getLogger() {
        return Logger.getLogger(CustomizeInstanceAction.class.getName());
    }
    
    @Override
    public void performAction(Node[] nodes) {
        if (nodes == null || nodes.length != 1) {
            getLogger().fine("no node is selected for change Cloudbees instance preferences");
            return;
        }
        CloudbeesInstance instance = nodes[0].getLookup().lookup(CloudbeesInstance.class);
        if ( instance == null ) {
            getLogger().fine("Cloudbees instance is NULL");
            return; 
        }
        
        PreferenceWizard.CustomizePreferenceWizard wiz = 
                new PreferenceWizard.CustomizePreferenceWizard(instance);
        
        wiz.showWizard();
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CustomizeInstanceAction.class, "LBL_CustomizeInstanceAction");
    }

    @Override
    public String iconResource() {
        return null;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    protected boolean enable(Node[] nodes) {
        if (nodes != null && nodes.length == 1) {
            CloudbeesInstance instance = nodes[0].getLookup().lookup(CloudbeesInstance.class);
            return instance != null;
        }
        return true;
    }
    
}
