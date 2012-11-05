package com.cloudbees.netbeans.modules.service.plugin.model;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.DatabaseInfo;
import java.util.List;
import javax.swing.event.ChangeListener;

/**
 *
 * @author David BRASSELY
 */
public interface CloudbeesInstance {
    
    List<ApplicationInfo> listApplicationInfos();
    
    List<DatabaseInfo> listDatabaseInfos();
    
    boolean isConnected();
    
    void addChangeListener(ChangeListener l);
    void removeChangeListener(ChangeListener l);
}
