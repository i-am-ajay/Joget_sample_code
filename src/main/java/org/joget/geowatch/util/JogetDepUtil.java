package org.joget.geowatch.util;

import org.joget.directory.model.Department;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.geowatch.app.AppContext;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class JogetDepUtil {

    public static Department getDepById(String depId) throws Exception {
        if (isEmpty(depId)) return null;
        ExtDirectoryManager directoryManager = AppContext.getBean("directoryManager", ExtDirectoryManager.class);
        return directoryManager.getDepartmentById(depId);
    }
}
