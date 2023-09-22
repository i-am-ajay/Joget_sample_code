package org.joget.geowatch.test.alyaumi;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.Employment;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class GetHOD {
    public static void getHod(){
        String userName = "admin";
        DirectoryManager manager = (DirectoryManager) AppUtil.getApplicationContext().getBean("directoryManager");
        User user = manager.getUserById(userName);
        Set getApprover = getApproverManager(user);
        for(Object str : getApprover){
            LogUtil.info("User",str.toString());
        }

    }
    public static Set getApproverManager(User appr){
        LogUtil.info("Calling","getApprover");
        Set emp = appr.getEmployments();
        Set approverSet = new HashSet();
        for(Object empObj : emp){
            Employment employment = (Employment) empObj;
            LogUtil.info("employment",employment.getEmploymentReportTo().getReportTo().getUser().getUsername());
            if(employment.getEndDate() != null){
                Date now = new Date();
                if(employment.getEndDate().after(now)){
                    approverSet.add(employment.getEmploymentReportTo().getReportTo().getUser().getUsername());
                }
            }
            else {
                approverSet.add(employment.getEmploymentReportTo().getReportTo().getUser().getUsername());
            }
        }
        return approverSet;
    }
}
return GetHOD.getHod();
