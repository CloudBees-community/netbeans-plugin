package com.cloudbees.netbeans.modules.service.plugin.actions;

import com.cloudbees.api.ApplicationInfo;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.HtmlBrowser;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author David BRASSELY
 */
@ActionID(category = "Team", id = "com.cloudbees.netbeans.modules.service.plugin.actions.OpenUrlAction")
@ActionRegistration(displayName = "#LBL_OpenInBrowserAction", iconInMenu = false)
@ActionReference(path = "todo: changeme", position = 600)
@NbBundle.Messages("LBL_OpenInBrowserAction=&Open in Browser")
public class OpenUrlAction extends AbstractAction {

    public static Action forOpenable(ApplicationInfo application) {
        return new OpenUrlAction(application);
    }
    private final ApplicationInfo application;

    public OpenUrlAction(ApplicationInfo application) {
        super("&Open in Browser");
        this.application = application;
    }

    @Override
    public boolean isEnabled() {
        return application.getStatus().equals("active");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURL(createApplicationUrl());
        } catch (MalformedURLException x) {
            Exceptions.printStackTrace(x);
        }
    }

    private URL createApplicationUrl() throws MalformedURLException {
        return new URL("http://" + application.getUrls()[0]);
    }
}
