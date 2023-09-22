package org.joget.geowatch.test.warranty;

import org.joget.apps.app.service.AppUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;

public class GetUserEmail {
    public String getEmail(){
        DirectoryManager dm = (DirectoryManager) AppUtil.getApplicationContext().getBean("direcotryManager");
        User user = dm.getUserById("");
        user.getEmail();
    }
}
