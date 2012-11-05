package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.netbeans.modules.service.plugin.CloudbeesInstanceManager;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.util.Collections;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * 
 * 
 * @author David BRASSELY
 */
@ServicesTabNodeRegistration(name=CloudbeesManagerNode.CLOUDBEES_ROOT_NAME, displayName="#LBL_CloudbeesNode", shortDescription="#LBL_CloudbeesNode", iconResource=CloudbeesManagerNode.ICON_BASE, position=488)
public class CloudbeesManagerNode extends AbstractNode {

    public static final String CLOUDBEES_ROOT_NAME = "Cloudbees"; // NOI18N
    static final String ICON_BASE = "com/cloudbees/netbeans/modules/service/plugin/nodes/resources/cloudbees.png"; // NOI18N
    
    private CloudbeesManagerNode() {
        super(new CloudbeesInstanceNodeChildren());
        setName(CLOUDBEES_ROOT_NAME);
        setDisplayName(NbBundle.getMessage(CloudbeesManagerNode.class, "LBL_CloudbeesNode"));
        setIconBaseWithExtension(ICON_BASE);
    }
    
    public @Override Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
    //    actions.add(new AddInstanceAction());
        return actions.toArray(new Action[actions.size()]);
    }

    private static final class CloudbeesInstanceNodeChildren extends Children.Keys<String> implements ChangeListener {
        
        private final String[] KEYS = {
                ApplicationInfoFolderNode.FOLDER_KEY,
                DatabaseInfoFolderNode.FOLDER_KEY
        };
        
        private CloudbeesInstance mInstance;
        
        public CloudbeesInstanceNodeChildren() {
            CloudbeesInstanceManager mgr = CloudbeesInstanceManager.getInstance();
            mInstance = mgr.listCloudbeesInstances().iterator().next();
        }
        
        protected Node[] createNodes(String key) {
            if (ApplicationInfoFolderNode.FOLDER_KEY.equals(key)) {
                return new Node[]{new ApplicationInfoFolderNode(this.mInstance)};
            } else if (DatabaseInfoFolderNode.FOLDER_KEY.equals(key)) {
                return new Node[]{new DatabaseInfoFolderNode(this.mInstance)};
            } else {
                return null;
            }
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            updateKeys();
        }
        
        protected void updateKeys() {
            setKeys(KEYS);
        }

        @Override
        protected void removeNotify() {
            java.util.List<String> emptyList = Collections.emptyList();
            setKeys(emptyList);
            super.removeNotify();
        }

        public void stateChanged(ChangeEvent e) {
            Object source = e.getSource();
            if (source == this.mInstance) {
                // updateKeys();
            }
        }
    }
}
