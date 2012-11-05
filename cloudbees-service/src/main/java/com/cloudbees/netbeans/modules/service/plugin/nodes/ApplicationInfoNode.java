package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.awt.Image;
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
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author David BRASSELY
 */
public class ApplicationInfoNode extends AbstractNode {

    private static final Image SERVICE_ICON = ImageUtilities.loadImage("com/cloudbees/netbeans/modules/service/plugin/nodes/resources/application.png", true); // NOI18N

    private CloudbeesInstance mInstance;
    private ApplicationInfo mApplicationInfo;

    public ApplicationInfoNode(CloudbeesInstance instance, ApplicationInfo info) {
        super(new ApplicationInfoNodeChildren(instance, info), Lookups.fixed(instance, info));
        this.mInstance = instance;
        this.mApplicationInfo = info;
        
        this.setName(this.mApplicationInfo.getId());
        this.setDisplayName(mApplicationInfo.getTitle());
        
        String desc = this.mApplicationInfo.getStatus();
        if (desc != null) {
            this.setShortDescription(desc);
        }
    }

    @Override
    public Image getIcon(int type) {
        Image img = computeIcon(false, type);
        return (img != null) ? img : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image img = computeIcon(true, type);
        return (img != null) ? img : super.getIcon(type);
    }

    private Image computeIcon(boolean opened, int type) {
        return SERVICE_ICON;
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] baseActions = super.getActions(context);
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(Arrays.asList(baseActions));
        return actions.toArray(new Action[actions.size()]);
    }

    private static final class ApplicationInfoNodeChildren extends Children.Keys <String>
            implements ChangeListener {

        private CloudbeesInstance mInstance;
        private ApplicationInfo mApplicationInfo;

        public ApplicationInfoNodeChildren(CloudbeesInstance instance, ApplicationInfo info) {
            this.mInstance = instance;
            ChangeListener cl = WeakListeners.change(this, this.mInstance);
            this.mInstance.addChangeListener(cl);
        }

        private void updateKeys() {
            String[] keys = {};
            setKeys(keys);
        }

        protected Node[] createNodes(String key) {
            return new Node[0];
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            updateKeys();
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
                updateKeys();
            }
        }
    }
}
