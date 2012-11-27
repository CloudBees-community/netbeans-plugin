package com.cloudbees.netbeans.modules.service.plugin.ui;

import com.cloudbees.netbeans.modules.service.plugin.CloudbeesInstanceManager;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.awt.Component;
import java.awt.Dialog;
import java.text.MessageFormat;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

/**
 *
 * @author David BRASSELY
 */
public abstract class PreferenceWizard {
    
    public static final String PROP_WIZ_TYPE = "WizardType";
    public static final String WIZ_TYPE_NEW = "NewWizard";
    public static final String WIZ_TYPE_CUSTOMIZER = "Customizer";

    private WizardDescriptor.Panel<WizardDescriptor>[] panels;

    public PreferenceWizard() {
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel<WizardDescriptor>[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                    new PreferenceWizardPanel()
                };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    public boolean showWizard() {
        WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        initWizard(wizardDescriptor);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean finished = (wizardDescriptor.getValue() == WizardDescriptor.FINISH_OPTION);
        if (finished) {
            performAction(wizardDescriptor);
        }
        return finished;
    }

    protected abstract void initWizard(WizardDescriptor wizDesc);

    protected abstract void performAction(WizardDescriptor wizDesc);

    protected void loadInstanceSettings(WizardDescriptor wizDesc, CloudbeesInstance instance) {
        wizDesc.putProperty(CloudbeesInstance.PROP_APIKEY, instance.getApiKey());
        wizDesc.putProperty(CloudbeesInstance.PROP_SECRET, instance.getSecret());
    }
    
    protected void saveInstanceSettings(WizardDescriptor wizDesc, CloudbeesInstance instance) {
        String apiKey = (String) wizDesc.getProperty(CloudbeesInstance.PROP_APIKEY);
        String secret = (String) wizDesc.getProperty(CloudbeesInstance.PROP_SECRET);

        instance.setApiKey(apiKey);
        instance.setSecret(secret);

        CloudbeesInstanceManager.getInstance().saveCloudbeesInstance(instance);
    }

    public static class NewInstanceWizard extends PreferenceWizard {

        @Override
        protected void initWizard(WizardDescriptor wizDesc) {
            wizDesc.setTitleFormat(new MessageFormat("{0}"));
            wizDesc.setTitle("Add Cloudbees Instance");
            wizDesc.putProperty(PROP_WIZ_TYPE, WIZ_TYPE_NEW);
        }

        @Override
        protected void performAction(WizardDescriptor wizDesc) {
            CloudbeesInstance instance = CloudbeesInstanceManager.getInstance().newServerInstance();
            saveInstanceSettings(wizDesc, instance);
        }
    }
    
    public static class CustomizePreferenceWizard extends PreferenceWizard {

        private CloudbeesInstance mInstance;

        public CustomizePreferenceWizard(CloudbeesInstance instance) {
            this.mInstance = instance;
        }

        @Override
        protected void initWizard(WizardDescriptor wizDesc) {
            wizDesc.setTitleFormat(new MessageFormat("{0}"));
            wizDesc.setTitle("Cloudbees Preferences");
            wizDesc.putProperty(PROP_WIZ_TYPE, WIZ_TYPE_CUSTOMIZER);
            loadInstanceSettings(wizDesc, this.mInstance);
        }

        @Override
        protected void performAction(WizardDescriptor wizDesc) {
            saveInstanceSettings(wizDesc, this.mInstance);
        }
    }
}
