package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.netbeans.modules.service.plugin.CloudbeesInstanceManager;
import com.cloudbees.netbeans.modules.service.plugin.actions.CustomizeInstanceAction;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author David BRASSELY
 */
public class CloudbeesInstanceNode extends AbstractNode {
    
    private CloudbeesInstance mInstance;
    
    static final String ICON_BASE = "com/cloudbees/netbeans/modules/service/plugin/nodes/resources/cloudbees.png"; // NOI18N
    
    public CloudbeesInstanceNode(final CloudbeesInstance instance) {
        super(new InstanceNodeChildren(instance), Lookups.singleton(instance));
        
        this.mInstance = instance;
        this.setName(this.mInstance.getPreferenceName());
        this.setDisplayName(this.mInstance.getPreferenceName());
        this.setIconBaseWithExtension(ICON_BASE);
    }
    
    @Override
    public Action[] getActions(boolean context) {
        Action[] baseActions = super.getActions(context);
        List<Action> actions = new ArrayList<Action>();
        actions.add(SystemAction.get(CustomizeInstanceAction.class));
        actions.addAll(Arrays.asList(baseActions));
        return actions.toArray(new Action[actions.size()]);
    }
    
    private static final class InstanceNodeChildren extends Children.Keys<String> implements ChangeListener {
        
        private final String[] KEYS = {
                ApplicationInfoFolderNode.FOLDER_KEY,
                DatabaseInfoFolderNode.FOLDER_KEY
        };
        
        private CloudbeesInstance mInstance;
        
        public InstanceNodeChildren(CloudbeesInstance mInstance) {
            this.mInstance = mInstance;
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
