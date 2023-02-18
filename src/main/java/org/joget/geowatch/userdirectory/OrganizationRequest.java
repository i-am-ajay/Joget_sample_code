package org.joget.geowatch.userdirectory;
import java.util.HashSet;
import java.util.Set;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.directory.model.Role;
import org.joget.directory.model.User;
import org.joget.directory.model.Organization;
import org.joget.directory.model.Employment;
import org.joget.directory.dao.RoleDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.dao.GroupDao;
import org.joget.directory.dao.OrganizationDao;
import org.joget.directory.model.service.DirectoryUtil;
import org.joget.directory.model.service.UserSecurity;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class OrganizationRequest {
    public static Object createUser() {
        try {
            UserSecurity us = DirectoryUtil.getUserSecurity();
            RoleDao roleDao = (RoleDao) AppUtil.getApplicationContext().getBean("roleDao");
            UserDao userDao = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
            GroupDao groupDao = (GroupDao) AppUtil.getApplicationContext().getBean("groupDao");
            OrganizationDao organizationDao = (OrganizationDao) AppUtil.getApplicationContext().getBean("organizationDao");

            FormDataDao formDatadao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            //AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
            String organizationId = "#form.organization_form.organizationId#";
            String organizationName = "#form.organization_form.organizationName#";
            String organizationDescription = "#form.organization_form.organizationDescription#";

            LogUtil.info("organizationId", " :" + organizationId);
            LogUtil.info("organizationName", " :" + organizationName);
            LogUtil.info("organizationDescription", " :" + organizationDescription);


            Organization organization = new Organization();
            organization.setId(organizationId);
            organization.setName(organizationName);
            organization.setDescription(organizationDescription);


            String organizationDesc = "#currentUser.organization.id#";

            LogUtil.info("organizationDesc", " :" + organizationDesc);

            organizationDao.addOrganization(organization);


            LogUtil.info("end of try block", "organization added");
            // LogUtil.info("#currentUser.organization.id#", "#currentUser.organization.id#");

        } catch (Exception e) {
            LogUtil.error("Sample app - Bulk Create Users form", e, "Store organization error!!");
        }
        return null;
    }
}
OrganizationRequest.createUser();

