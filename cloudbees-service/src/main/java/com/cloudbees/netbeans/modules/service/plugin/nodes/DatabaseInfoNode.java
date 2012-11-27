package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.api.DatabaseInfo;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;

/**
 *
 * @author David BRASSELY
 */
public class DatabaseInfoNode extends AbstractNode {

    private static final Image RUNNING_BADGE_ICON = ImageUtilities.loadImage("com/cloudbees/netbeans/modules/service/plugin/nodes/resources/running.png", true); // NOI18N
    private static final Image DATABASE_ICON = ImageUtilities.loadImage("com/cloudbees/netbeans/modules/service/plugin/nodes/resources/database.png", true); // NOI18N

    private CloudbeesInstance mInstance;
    private DatabaseInfo mDatabaseInfo;

    public DatabaseInfoNode(CloudbeesInstance instance, DatabaseInfo info) {
        super(Children.LEAF);
        this.mInstance = instance;
        this.mDatabaseInfo = info;
        
        this.setName(this.mDatabaseInfo.getName());
        this.setDisplayName(this.mDatabaseInfo.getName());
        
        String desc = this.mDatabaseInfo.getStatus();
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
        Image img = DATABASE_ICON;
        if (this.mDatabaseInfo.getStatus().equals("active")) {
            img = ImageUtilities.mergeImages(DATABASE_ICON, RUNNING_BADGE_ICON, 12, 0);
        }
        return img;
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] baseActions = super.getActions(context);
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(Arrays.asList(baseActions));
        return actions.toArray(new Action[actions.size()]);
    }
}
