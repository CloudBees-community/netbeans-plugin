package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.api.DatabaseInfo;
import com.cloudbees.netbeans.modules.service.plugin.actions.DeleteDatabaseAction;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author David BRASSELY
 */
public class DatabaseInfoNode extends AbstractNode {

    private static final Image RUNNING_BADGE_ICON = ImageUtilities.loadImage("com/cloudbees/netbeans/modules/service/plugin/nodes/resources/running.png", true); // NOI18N
    private static final Image DATABASE_ICON = ImageUtilities.loadImage("com/cloudbees/netbeans/modules/service/plugin/nodes/resources/database.gif", true); // NOI18N
    private CloudbeesInstance mInstance;
    private DatabaseInfo mDatabaseInfo;

    public DatabaseInfoNode(CloudbeesInstance instance, DatabaseInfo info) {
        super(Children.LEAF, Lookups.fixed(instance, info));
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
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();

        final DatabaseInfo database = getLookup().lookup(DatabaseInfo.class);

    //        Property idProp = PropertySupport.Reflection((Object)database, String.class, "id");
        try {
            Property prop = new PropertySupport.ReadOnly<String>("Name", String.class, "Name", "Database Name") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return database.getName();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        try {
            Property prop = new PropertySupport.ReadOnly<String>("Status", String.class, "Status", "Database Status") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return database.getStatus();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        try {
            Property prop = new PropertySupport.ReadOnly<String>("Username", String.class, "Username", "Database Username") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return database.getUsername();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        try {
            Property prop = new PropertySupport.ReadOnly<String>("Owner", String.class, "Owner", "Owner") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return database.getOwner();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }
        
        try {
            Property prop = new PropertySupport.ReadOnly<Date>("Created", Date.class, "Created On", "Creation Date") {
                @Override
                public Date getValue() throws IllegalAccessException, InvocationTargetException {
                    return database.getCreated();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        try {
            Property prop = new PropertySupport.ReadOnly<String>("Master", String.class, "Master", "Master") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return database.getMaster();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }
        
        try {
            Property prop = new PropertySupport.ReadOnly<Integer>("Port", Integer.class, "Port", "Port") {
                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return database.getPort();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }
        
        try {
            Property prop = new PropertySupport.ReadOnly<String[]>("Slaves", String[].class, "Slaves", "Slaves") {
                @Override
                public String[] getValue() throws IllegalAccessException, InvocationTargetException {
                    return database.getSlaves();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        sheet.put(set);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] baseActions = super.getActions(context);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new DeleteDatabaseAction());
        actions.addAll(Arrays.asList(baseActions));
        return actions.toArray(new Action[actions.size()]);
    }
}
