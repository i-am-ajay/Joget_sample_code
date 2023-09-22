package org.joget.geowatch.test.user;

import org.joget.apps.app.service.AppUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.ExtDirectoryManager;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserManagement {
    public static List<User> getUserList(){
        // get directoryManager bean
        ExtDirectoryManager directoryManager = AppUtil.getApplicationContext().getBean("directoryManager",ExtDirectoryManager.class);
        // get list of all users
        Collection<User> userList = directoryManager.getUserList();
        // filter only active users and add them to list, return that list
        return (userList.stream().filter(e -> e.getActive() == 1).collect(Collectors.toList()));
    }
}
