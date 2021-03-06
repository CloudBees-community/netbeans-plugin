package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author David BRASSELY
 */
public class ApplicationInfoFolderNode extends AbstractNode {

    static final String ICON_BASE = "com/cloudbees/netbeans/modules/service/plugin/nodes/resources/applications.png"; // NOI18N
    public static final String FOLDER_KEY = "ApplicationFolderNode";

    private CloudbeesInstance mInstance;

    public ApplicationInfoFolderNode(CloudbeesInstance instance) {
        super(new ApplicationInfoFolderNodeChildren(instance), Lookups.singleton(instance));
        this.mInstance = instance;
        this.setName(FOLDER_KEY);
        this.setIconBaseWithExtension(ICON_BASE);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(ApplicationInfoFolderNode.class, "LBL_ApplicationFolderNode");
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] baseActions = super.getActions(context);
        List<Action> actions = new ArrayList<Action>();
        // actions.add(SystemAction.get(RefreshAction.class));
        actions.addAll(Arrays.asList(baseActions));
        return actions.toArray(new Action[actions.size()]);
    }

    private static final class ApplicationInfoFolderNodeChildren extends Children.Keys <ApplicationInfo>
        implements ChangeListener {

        private CloudbeesInstance mInstance;

        public ApplicationInfoFolderNodeChildren(CloudbeesInstance instance) {
            this.mInstance = instance;
            ChangeListener cl = WeakListeners.change(this, this.mInstance);
            this.mInstance.addChangeListener(cl);
        }
        
        private ApplicationInfo[] getSortedKeys(List<ApplicationInfo> list) {
            ApplicationInfo[] keys = new ApplicationInfo[0];
            List<ApplicationInfo> sortedList = new LinkedList<ApplicationInfo>();
            sortedList.addAll(list);
            try {
                Collections.sort(sortedList, new Comparator<ApplicationInfo>() {

                    public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                        return o1.getTitle().compareTo(
                            o2.getTitle());
                    }
                });
                keys = sortedList.toArray(new ApplicationInfo[0]);
            } catch (Exception ex) {
                keys = list.toArray(new ApplicationInfo[0]);
            }

            return keys;
        }
        
        private void updateKeys() {
            List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
            list = this.mInstance.listApplicationInfos();
            ApplicationInfo[] keys = getSortedKeys(list);
            setKeys(keys);
        }

        protected Node[] createNodes(ApplicationInfo key) {
            ApplicationInfo info = (ApplicationInfo) key;
            return new Node[]{new ApplicationInfoNode(this.mInstance, info)};
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            updateKeys();
        }

        @Override
        protected void removeNotify() {
            java.util.List<ApplicationInfo> emptyList = Collections.emptyList();
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
