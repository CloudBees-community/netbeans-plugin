package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.netbeans.modules.service.plugin.actions.OpenUrlAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.RestartApplicationAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.StartApplicationAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.StopApplicationAction;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author David BRASSELY
 */
public class ApplicationInfoNode extends AbstractNode {

    private static final Image RUNNING_BADGE_ICON = ImageUtilities.loadImage("com/cloudbees/netbeans/modules/service/plugin/nodes/resources/running.png", true); // NOI18N
    
    private CloudbeesInstance mInstance;
    private ApplicationInfo mApplicationInfo;

    public ApplicationInfoNode(CloudbeesInstance instance, ApplicationInfo info) {
        super(Children.LEAF, Lookups.fixed(instance, info));
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
        Image img = ImageUtilities.loadImage("com/cloudbees/netbeans/modules/service/plugin/nodes/resources/" + mApplicationInfo.getSettings().get("containerType") + ".png", true);
        if (this.mApplicationInfo.getStatus().equals("active")) {
            img = ImageUtilities.mergeImages(img, RUNNING_BADGE_ICON, 12, 0);
        }
        return img;
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] baseActions = super.getActions(context);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new StartApplicationAction());
        actions.add(new RestartApplicationAction());
        actions.add(new StopApplicationAction());
        actions.add(null);
        actions.add(OpenUrlAction.forOpenable(mApplicationInfo));
        actions.addAll(Arrays.asList(baseActions));
        return actions.toArray(new Action[actions.size()]);
    }
}
