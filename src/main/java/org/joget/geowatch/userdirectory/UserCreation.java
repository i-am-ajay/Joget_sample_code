package org.joget.geowatch.userdirectory;
import java.util.Set;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.directory.model.Role;
import org.joget.directory.model.User;
import org.joget.directory.model.Organization;
import org.joget.directory.model.Employment;
import org.joget.directory.model.Department;
import org.joget.directory.dao.RoleDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.dao.GroupDao;
import org.joget.directory.dao.OrganizationDao;
import org.joget.directory.dao.EmploymentDao;
import org.joget.directory.dao.DepartmentDao;
import org.joget.directory.model.service.DirectoryUtil;
import org.joget.directory.model.service.UserSecurity;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.commons.util.UuidGenerator;

public class UserCreation {
    public static Object createUser() {
        try {

            UserSecurity us = DirectoryUtil.getUserSecurity();
            RoleDao roleDao = (RoleDao) AppUtil.getApplicationContext().getBean("roleDao");
            UserDao userDao = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
            GroupDao groupDao = (GroupDao) AppUtil.getApplicationContext().getBean("groupDao");
            OrganizationDao organizationDao = (OrganizationDao) AppUtil.getApplicationContext().getBean("organizationDao");
            EmploymentDao employmentDao = (EmploymentDao) AppUtil.getApplicationContext().getBean("employmentDao");
            DepartmentDao departmentDao = (DepartmentDao) AppUtil.getApplicationContext().getBean("departmentDao");
            FormDataDao formDatadao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            //AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");

            String userName = "#form.user_request_form.userName#";
            String firstName = "#form.user_request_form.firstName#";
            String lastName = "#form.user_request_form.lastName#";
            String timeZone = "#form.user_request_form.timeZone#";
            String email = "#form.user_request_form.email#";
            String password = "#form.user_request_form.password#";
            String status = "#form.user_request_form.status#";

            LogUtil.info("username", " :" + userName);
            LogUtil.info("firstName", " :" + firstName);
            LogUtil.info("lastName", " :" + lastName);
            LogUtil.info("timeZone", " :" + timeZone);
            LogUtil.info("email", " :" + email);
            LogUtil.info("password", " :" + password);
            LogUtil.info("status", " :" + status);


            User user = new User();
            user.setId(userName);
            user.setUsername(userName);
            user.setTimeZone("0");
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);

            if (status.equals("Active")) {
                user.setActive(1);
            } else {
                user.setActive(0);
            }
            user.setActive(1);

            //     System.out.println(user+"USERS ###");


            //Check if there is user security implementation, using it to encrypt password
            if (us != null) {
                user.setPassword(us.encryptPassword(userName, password));
            } else {
                user.setPassword(StringUtil.md5Base16(password));
            }
            user.setConfirmPassword(password);
            userDao.addUser(user);

            /*
            //set user role
            Set roleSet = new HashSet();
            roleSet.add(roleDao.getRole("ROLE_USER"));
            user.setRoles(roleSet);*/
            /*
            // Dept code
            Department department = new Department();
            department.setId();
            departmentDao.addDepartment(department);
            */
            // get selected organization id
            String orgId = "#form.user_request_form.company#";
            LogUtil.info("organization", " :" + orgId);
            // get organization object using org id.
            Organization org = organizationDao.getOrganization(orgId);
            // get employment set from organization id
            Set employmentSet = org.getEmployments();
            // create Employment object
            Employment employment = new Employment();
            employment.setId(UuidGenerator.uuidGenerator.getUuid());
            employment.setUserId(userName);
            employment.setOrganizationId(orgId);
            // employment.setDepartmentId(departmentId);
            employmentDao.addEmployment(employment);
            LogUtil.info("employmentvalue", " :" + employment);

            employmentSet.add(employment);
            LogUtil.info("employmentSet", "employmentSet");
            org.setEmployments(employmentSet);

            LogUtil.info("org.setEmployment", "org.setEmployment");
            LogUtil.info("status", " :" + org.getId());

            organizationDao.updateOrganization(org);
            LogUtil.info("organizationDao", "organizationDao");


            // Set groupSet = new HashSet();
            // groupSet.add(groupDao.getGroup("customer"));
            // user.setGroups(groupSet);


            //      System.out.println("User Added");
            if (us != null) {
                us.insertUserPostProcessing(user);
            }
            LogUtil.info("end of try block", "user added");
            LogUtil.info("#currentUser.userName#", "#currentUser.userName#");
        } catch (Exception e) {
            LogUtil.error("Sample app - Bulk Create Users form", e, "Store user error!!");
        }
        return null;
    }
}
UserCreation.createUser();
