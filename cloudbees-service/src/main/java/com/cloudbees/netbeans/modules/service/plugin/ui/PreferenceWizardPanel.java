/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudbees.netbeans.modules.service.plugin.ui;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

/**
 *
 * @author David BRASSELY
 */
public class PreferenceWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Component component;
    private ChangeSupport changeSupport;
    private WizardDescriptor wizDesc;
    
    public PreferenceWizardPanel() {
        changeSupport = new ChangeSupport(this);
    }
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new PreferenceVisualPanel(this);
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
    //    return true;
    // If it depends on some condition (form filled out...), then:
    // return someCondition();
    // and when this condition changes (last form field filled in...) then:
    // fireChangeEvent();
    // and uncomment the complicated stuff below.
        String errMsg = ((PreferenceVisualPanel)getComponent()).isValidVisual();
        if ( wizDesc != null ) {
            wizDesc.putProperty("WizardPanel_errorMessage", errMsg);
        } else {
            System.err.println("WizDesc is null in isValid");
        }
        if ( errMsg == null ) {
            return true;
        } else {
            return false;
        }
    }

    public final void addChangeListener(ChangeListener l) {
        this.changeSupport.addChangeListener(l);
    }

    public final void removeChangeListener(ChangeListener l) {
        this.changeSupport.removeChangeListener(l);
    }

    protected final void fireChangeEvent() {
        this.changeSupport.fireChange();
    }
    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.

    public void readSettings(WizardDescriptor settings) {
        wizDesc = settings;
        ((PreferenceVisualPanel) getComponent()).readSettings(wizDesc);
    }

    public void storeSettings(WizardDescriptor settings) {

        ((PreferenceVisualPanel) getComponent()).storeSettings(settings);
    }
}
