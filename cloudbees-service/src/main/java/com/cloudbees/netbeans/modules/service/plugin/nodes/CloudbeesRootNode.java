package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.netbeans.modules.service.plugin.CloudbeesInstanceManager;
import com.cloudbees.netbeans.modules.service.plugin.actions.CustomizeInstanceAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.NewInstanceAction;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 * 
 * 
 * @author David BRASSELY
 */
@ServicesTabNodeRegistration(name=CloudbeesRootNode.CLOUDBEES_ROOT_NAME, displayName="#LBL_CloudbeesNode", shortDescription="#LBL_CloudbeesNode", iconResource=CloudbeesRootNode.ICON_BASE, position=488)
public class CloudbeesRootNode extends AbstractNode {

    public static final String CLOUDBEES_ROOT_NAME = "Cloudbees"; // NOI18N
    static final String ICON_BASE = "com/cloudbees/netbeans/modules/service/plugin/nodes/resources/cloudbees.png"; // NOI18N
    
    private CloudbeesRootNode() {
        super(new RootNodeChildren());
        setName(CLOUDBEES_ROOT_NAME);
        setDisplayName(NbBundle.getMessage(CloudbeesRootNode.class, "LBL_CloudbeesNode"));
        setIconBaseWithExtension(ICON_BASE);
    }
    
    public @Override Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new NewInstanceAction());
        return actions.toArray(new Action[actions.size()]);
    }

    private static final class RootNodeChildren extends Children.Keys<CloudbeesInstance> 
        implements PropertyChangeListener, ChangeListener {
        
        public RootNodeChildren() {
            CloudbeesInstanceManager mgr = CloudbeesInstanceManager.getInstance();
            PropertyChangeListener l = WeakListeners.propertyChange(this, mgr);
            mgr.addPropertyChangeListener(l);
        }
        
        private void updateKeys() {
            List<CloudbeesInstance> list = CloudbeesInstanceManager.getInstance().listCloudbeesInstances();
            CloudbeesInstance[] keys = list.toArray(new CloudbeesInstance[0]);
            setKeys(keys);
        }

        protected Node[] createNodes(CloudbeesInstance key) {
            // FujiServerInstance obj = (FujiServerInstance) key;
//            ChangeListener cl = WeakListeners.change(this, obj);
//            obj.addChangeListener(cl);
////            PropertyChangeListener l = WeakListeners.propertyChange(this, obj);
////            obj.addPropertyChangeListener(l);
            return new Node[]{new CloudbeesInstanceNode(key)};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            updateKeys();
        }

        @Override
        protected void removeNotify() {
            java.util.List<CloudbeesInstance> emptyList = Collections.emptyList();
            setKeys(emptyList);
            super.removeNotify();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            CloudbeesInstanceManager mgr = CloudbeesInstanceManager.getInstance();
            Object source = evt.getSource();
            if (source == mgr) {
                updateKeys();
            } else if ( source != null && source instanceof CloudbeesInstance ) {
                refreshKey((CloudbeesInstance)source);
            }
        }

        public void stateChanged(ChangeEvent e) {
            Object source = e.getSource();
            if ( source != null && source instanceof CloudbeesInstance) {
                refreshKey((CloudbeesInstance)source);
            }
        }

    }
}
