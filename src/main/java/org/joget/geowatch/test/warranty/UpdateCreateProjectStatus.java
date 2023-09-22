package org.joget.geowatch.test.warranty;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UpdateCreateProjectStatus {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request)  throws SQLException, ClassNotFoundException {
        String gmApprovalRequired = "#variable.require_gm_approval#";
        String id = "#form.projects.id#";
        String tableName = "projects";
        String formName = "createProject";

        if(!gmApprovalRequired.equals("true")) {
            FormDataDao dao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            FormRow createProjectRow = dao.load(formName, tableName, id);
            createProjectRow.setProperty("status", "approved");
            FormRowSet set = new FormRowSet();
            set.add(createProjectRow);
            dao.saveOrUpdate(formName,tableName,set);
        }
        return null;
    }
}
UpdateCreateProjectStatus.execute(workflowAssignment, null, null);
