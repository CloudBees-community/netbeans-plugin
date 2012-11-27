package com.cloudbees.netbeans.modules.service.plugin.actions;

import com.cloudbees.netbeans.modules.service.plugin.ui.PreferenceWizard;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author David BRASSELY
 */
public class NewInstanceAction extends CallableSystemAction {

    public void performAction() {
        PreferenceWizard.NewInstanceWizard wiz = new PreferenceWizard.NewInstanceWizard();
        wiz.showWizard();
    }

    public String getName() {
        return "Add Cloudbees Instance";
    }

    @Override
    public String iconResource() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
    
}
