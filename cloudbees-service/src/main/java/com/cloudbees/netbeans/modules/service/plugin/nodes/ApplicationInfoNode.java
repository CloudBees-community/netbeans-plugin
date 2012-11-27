package com.cloudbees.netbeans.modules.service.plugin.nodes;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.netbeans.modules.service.plugin.actions.DeleteApplicationAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.OpenUrlAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.RestartApplicationAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.StartApplicationAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.StopApplicationAction;
import com.cloudbees.netbeans.modules.service.plugin.actions.ViewApplicationLogAction;
import com.cloudbees.netbeans.modules.service.plugin.model.CloudbeesInstance;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();
        Sheet.Set settings = Sheet.createPropertiesSet();
        settings.setDisplayName("Settings");
        settings.setName("settings");

        final ApplicationInfo application = getLookup().lookup(ApplicationInfo.class);

    //    Property idProp = PropertySupport.Reflection(application, String.class, "id");
        try {
            Property prop = new PropertySupport.ReadOnly<String>("Id", String.class, "Id", "Application ID") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return application.getId();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        try {
            Property prop = new PropertySupport.ReadOnly<String>("Title", String.class, "Title", "Application Title") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return application.getTitle();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        try {
            Property prop = new PropertySupport.ReadOnly<String>("Status", String.class, "Status", "Application Status") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return application.getStatus();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        try {
            Property prop = new PropertySupport.ReadOnly<Date>("Created", Date.class, "Created On", "Creation Date") {
                @Override
                public Date getValue() throws IllegalAccessException, InvocationTargetException {
                    return application.getCreated();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        try {
            Property prop = new PropertySupport.ReadOnly<String[]>("URL", String[].class, "Url", "Url") {
                @Override
                public String[] getValue() throws IllegalAccessException, InvocationTargetException {
                    return application.getUrls();
                }
            };
            set.put(prop);
        } catch (Exception ex) {
        }

        // Settings properties
        for (final Map.Entry<String, String> entry : application.getSettings().entrySet()) {
            try {
                Property prop = new PropertySupport.ReadOnly<String>(entry.getKey(), String.class, entry.getKey(), entry.getKey()) {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return entry.getValue();
                    }
                };
                settings.put(prop);
            } catch (Exception ex) {
            }
        }

        sheet.put(set);
        sheet.put(settings);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] baseActions = super.getActions(context);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new StartApplicationAction());
        actions.add(new RestartApplicationAction());
        actions.add(new StopApplicationAction());
        actions.add(null);
        actions.add(new DeleteApplicationAction());
        actions.add(null);
        actions.add(new ViewApplicationLogAction());
        actions.add(null);
        actions.add(OpenUrlAction.forOpenable(mApplicationInfo));
        actions.addAll(Arrays.asList(baseActions));
        return actions.toArray(new Action[actions.size()]);
    }
}
