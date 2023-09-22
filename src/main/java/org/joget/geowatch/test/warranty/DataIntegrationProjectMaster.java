package org.joget.geowatch.test.warranty;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import java.sql.DriverManager;
import java.time.LocalDate;

import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;


public class DataIntegrationProjectMaster {
    public static void dataIntegrationProjects(WorkflowAssignment assignment) {
        LogUtil.info("Project Master integration starts","");
        //LocalDate today = LocalDate.now();
        //LocalDate previousDay = today.minusDays(1);

        LocalDate today = LocalDate.of(2023,4,30);
        LocalDate previousDay = LocalDate.of(2023,4,1);
        Connection con = null;
        try {
            //Build Url for connecting to Oracle DB
            String url = "jdbc:oracle:thin:#envVariable.jdbcURL#";
            String username = "#envVariable.jdbcUserName#";
            String password = "#envVariable.jdbcPassword#";
            //Register Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //Get Connection Using DriverManager
            con = DriverManager.getConnection(url, username, password);

            // execute SQL query
            if (!con.isClosed()) {
                //Populate Project Master Form
                //String ProjectMasterQuery = "select * from apps.XXOIC_JOGETIN_SO_DETAILS_V where SO_NUMBER IN ('SO-135773')";
                String ProjectMasterQuery = "select * from apps.XXOIC_JOGETIN_SO_DETAILS_V where LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement projectstmt = con.prepareStatement(ProjectMasterQuery);
                projectstmt.setString(1,previousDay.toString());
                projectstmt.setString(2,today.toString());
                ResultSet projectrs = projectstmt.executeQuery();
                int count = 0;
                int batchNo = 0;
                String formIdProject = "projectMaster";
                String tableNameProject = "project_master";
                String fieldIDProject = "SO_Number";
                FormRowSet rowsProjectMasterData = new FormRowSet();
                FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                while (projectrs.next()) {

                    FormRow row = new FormRow();

                    //Set values
                    //Set Id using Primary Keys
                    String strSoHeader = (projectrs.getString("SO_HEADER_ID") != null) ? projectrs.getString("SO_HEADER_ID").toString() : "";
                    String strSoLine = (projectrs.getString("SO_LINE_ID") != null) ? projectrs.getString("SO_LINE_ID").toString() : "";
                    String prjMasterId = strSoHeader + "_" + strSoLine;
                    row.setProperty("id", prjMasterId);

                    //(projectrs.getString("ORDERED_ITEM")!= null)?projectrs.getString("ORDERED_ITEM").toString():"");

                    String strPrjSONumber = (projectrs.getString("SO_NUMBER") != null) ? projectrs.getString("SO_NUMBER").toString() : "";
                    row.setProperty("SO_Number", strPrjSONumber);

                    String strSODate = (projectrs.getString("SO_DATE") != null) ? projectrs.getString("SO_DATE").toString() : "";
                    row.setProperty("SO_date", strSODate);

                    String strCustNumber = (projectrs.getString("CUSTOMER_NUMBER") != null) ? projectrs.getString("CUSTOMER_NUMBER").toString() : "";
                    row.setProperty("customer_number", strCustNumber);

                    String strCustName = (projectrs.getString("CUSTOMER_NAME") != null) ? projectrs.getString("CUSTOMER_NAME").toString() : "";
                    row.setProperty("customer_name", strCustName);


                    String strSalesRepName = (projectrs.getString("SALESREP_NAME") != null) ? projectrs.getString("SALESREP_NAME").toString() : "";
                    row.setProperty("salesRep_name", strSalesRepName);

                    String strPrjItemcode = (projectrs.getString("ORDERED_ITEM") != null) ? projectrs.getString("ORDERED_ITEM").toString() : "";
                    row.setProperty("prjitem_code", strPrjItemcode);

                    String strPrjItemDesc = (projectrs.getString("ITEM_DESCRIPTION") != null) ? projectrs.getString("ITEM_DESCRIPTION").toString() : "";
                    row.setProperty("prjitem_desc", strPrjItemDesc);

                    String strPrjUOM = (projectrs.getString("UOM") != null) ? projectrs.getString("UOM").toString() : "";
                    row.setProperty("prj_uom", strPrjUOM);

                    String strPrjOrdQty = (projectrs.getString("ORDER_QTY") != null) ? projectrs.getString("ORDER_QTY").toString() : "";
                    row.setProperty("order_qty", strPrjOrdQty);

                    String strPrjCustId = (projectrs.getString("CUSTOMER_ID") != null) ? projectrs.getString("CUSTOMER_ID").toString() : "";
                    row.setProperty("customer_id", strPrjCustId);

                    String strPrjNum = (projectrs.getString("PROJECT_NUMBER") != null) ? projectrs.getString("PROJECT_NUMBER").toString() : "";
                    row.setProperty("project_number", strPrjNum);

                    String strPrjSalesRepId = (projectrs.getString("SALESREP_ID") != null) ? projectrs.getString("SALESREP_ID").toString() : "";
                    row.setProperty("salesrep_id", strPrjSalesRepId);

                    String strPrjInvItemId = (projectrs.getString("INVENTORY_ITEM_ID") != null) ? projectrs.getString("INVENTORY_ITEM_ID").toString() : "";
                    row.setProperty("inventory_item_id", strPrjInvItemId);

                    String strPrjName = (projectrs.getString("PROJECT_NAME") != null) ? projectrs.getString("PROJECT_NAME").toString() : "";
                    row.setProperty("project_name", strPrjName);

                    String strPrjConsName = (projectrs.getString("CONSULTANT_NAME") != null) ? projectrs.getString("CONSULTANT_NAME").toString() : "";
                    row.setProperty("consultant_name", strPrjConsName);

                    String strPrjEmirates = (projectrs.getString("EMIRATES") != null) ? projectrs.getString("EMIRATES").toString() : "";
                    row.setProperty("emirates", strPrjEmirates);


                    row.setProperty("so_header_id", strSoHeader);

                    String strPrjOrgId = (projectrs.getString("ORG_ID") != null) ? projectrs.getString("ORG_ID").toString() : "";
                    row.setProperty("org_id", strPrjOrgId);

                    row.setProperty("so_line_id", strSoLine);




                    //Populate the form
                    rowsProjectMasterData.add(row);
                    if(count == 100){
                        batchNo++;
                        count = 0;
                        formDataDao.saveOrUpdate(formIdProject, tableNameProject, rowsProjectMasterData);
                        rowsProjectMasterData.clear();
                        LogUtil.info("Batch No.",batchNo+"");
                    }
                    count++;

                }
                formDataDao.saveOrUpdate(formIdProject, tableNameProject, rowsProjectMasterData);
                AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
                WorkflowManager wm = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
                wm.activityVariable(assignment.getActivityId(),"run_status", "Success");
            }
        }
        catch (Exception e) {
            LogUtil.error("Exception: ",e, e.getMessage());
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowManager wm = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
            wm.activityVariable(assignment.getActivityId(),"run_status", "Error Thrown");
        }
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (SQLException e) {
            }
        }
        LogUtil.info("Project Integration","Ends");
    }
}
DataIntegrationProjectMaster.dataIntegrationProjects(workflowAssignment);
