package org.joget.geowatch.test.warranty;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.service.WorkflowManager;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

public class AutoReloadWarranty {
    static Map projectsToBeReloaded = new HashMap();
    static Set soNumberSet = new HashSet();
    static String lastRunDate = null;

    static String formId = "last_run_scheduler";
    static String tableName = "last_run_scheduler";
    static String primaryKey = "Warranty_Reload";
    static FormDataDao lastRunTable = null;
    static FormRow lastRunDateRow = null;
    /**
     * For auto reload there can be two scenarios,
     * - Do updates after last run of scheduler: find all sos and find projects against these SOs
     *     Find warranties of these project and reload those.
     * - Project Update : May DO was updated and a new SO was added, but nobody added that SO agains
     *                   project. So when scheduler runs it won't find any project against that SO
     *                   But later someone added SO against the project, now there is no DO or SO update
     *                   these are old, but added against project just now. Therefore we need to
     *                   Check and pick any project that updated after last run of scheduler*/
    public static void doBasedWarrantiesForReload() throws ClassNotFoundException {
        LogUtil.info("-------","Auto Reload Process Starts -----------");
        // get oracle connection
        Connection oracleConnection = null;
        String url = "jdbc:oracle:thin:#envVariable.jdbcURL#";
        String username = "#envVariable.jdbcUserName#";
        String password = "#envVariable.jdbcPassword#";
        //Register Oracle JDBC Driver
        try {

            // get last run date
            lastRunTable = (FormDataDao)AppUtil.getApplicationContext().getBean("formDataDao");
            lastRunDateRow = lastRunTable.load(formId,tableName,primaryKey);
            lastRunDate = lastRunDateRow.getProperty("last_run_date");


            // get details from oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");
            oracleConnection = DriverManager.getConnection(url, username, password);
            String DOMasterQuery = "select * from apps.XXOIC_JOGETIN_DO_SHIPMENT_V " +
                    "where LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD')";
            PreparedStatement dostmt = oracleConnection.prepareStatement(DOMasterQuery);
            dostmt.setString(1, lastRunDate);
            ResultSet dors = dostmt.executeQuery();

            while (dors.next()) {
                String strSONumber = (dors.getString("SO_NUMEBR") != null) ? dors.getString("SO_NUMEBR").toString() : "";
                if(strSONumber != null && !strSONumber.isEmpty()){

                    soNumberSet.add(strSONumber);
                }
            }
            // get project related to these SO for which DOs created after last run
            getProjectDetails();
        }
        catch(ClassNotFoundException ex){
            LogUtil.error(AutoReloadWarranty.class.getName(),ex,ex.getMessage());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        finally{
            try{
                if(oracleConnection != null && !oracleConnection.isClosed()) {
                    oracleConnection.close();
                }
            }
            catch(SQLException ex){

            }
        }
        // get project those are updated after last run of scheduler.
        projectBasedWarrantiesForReload();
        startAutoReload(lastRunDate);
        printProjectDetails();
        // update last run date to current date.
        updateLastRunDate();
        LogUtil.info("-------","Auto Reload Process Ends -----------");
    }

    private static void updateLastRunDate() {
        lastRunDateRow.setProperty("last_run_date",LocalDate.now().toString());
        FormRowSet set = new FormRowSet();
        set.add(lastRunDateRow);
        lastRunTable.saveOrUpdate(formId,tableName,set);
    }

    public static void getProjectDetails(){
        // get connection
        Connection con = null;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
            con = ds.getConnection();
            // Find projects updated after last run and has active warranties.
            String query = "SELECT c_pno,app_fd_review_form.id FROM app_fd_projects " +
                    "INNER JOIN app_fd_review_form ON " +
                    "app_fd_projects.c_projectNumber = app_fd_review_form.c_pno and " +
                    "c_rev_status = ? where c_selectedSONumbers LIKE ? " +
                    "AND c_Status_manager_approval = ?";
            for(Object so : soNumberSet) {
                //LogUtil.info("SO Number",so+"");
                String soString = (String)so;
                PreparedStatement projectStatement = con.prepareStatement(query);
                projectStatement.setString(1, "Active");
                projectStatement.setString(2, "%"+so+"%");
                projectStatement.setString(3,"Approved");
                ResultSet set = projectStatement.executeQuery();
                if(set == null)
                    continue;
                while(set.next()) {
                    String project = set.getString(1);
                    String warrantyId = set.getString(2);
                    projectsToBeReloaded.put(project,warrantyId);
                }

            }
        }
        catch(SQLException ex){
            LogUtil.error(AutoReloadWarranty.class.getName(),ex,ex.getMessage());
        }
        finally{
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            }
            catch(SQLException ex){
            }
        }
        // iterate over so set

        // get Project for each SO
    }
    public static void projectBasedWarrantiesForReload(){
        Connection appDbCon = null;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
            appDbCon = ds.getConnection();
            String query = "SELECT c_pno, app_fd_review_form.id, app_fd_review_form.c_sales_orders FROM app_fd_projects INNER JOIN app_fd_review_form " +
                    "ON app_fd_projects.c_projectNumber = app_fd_review_form.c_pno " +
                    "and c_rev_status = ? " +
                    "where app_fd_projects.dateModified >= ? AND " +
                    "c_Status_manager_approval = ?";
            PreparedStatement projectStatement = appDbCon.prepareStatement(query);
            projectStatement.setString(1, "Active");
            projectStatement.setString(2, lastRunDate);
            projectStatement.setString(3,"Approved");
            ResultSet set = projectStatement.executeQuery();
            if(set == null)
                return;
            while(set.next()){
                String projectNumber = set.getString(1);
                String warrantyId = set.getString(2);
                String salesOrder = set.getString(3);
                //LogUtil.info("Project Number",projectNumber);
                if(testProjectChange(projectNumber,appDbCon,salesOrder)) {
                    projectsToBeReloaded.put(projectNumber, warrantyId);
                }
            }
        }
        catch(SQLException ex){
            LogUtil.error(AutoReloadWarranty.class.getName(),ex,ex.getMessage());
        }
        finally{
            try{
                if(appDbCon != null && !appDbCon.isClosed()){
                    appDbCon.close();
                }
            }
            catch (SQLException ex){

            }
        }

    }

    public static boolean testProjectChange(String projectNumber, Connection con, String salesOrder) throws SQLException{
        boolean changeFlag = false;
        boolean salesOrderBlank = false;
        String query = "SELECT DISTINCT s.c_SO_Number from app_fd_projects p join app_fd_so_number s on p.id = s.c_projectNum join app_fd_do_master d on s.c_SO_Number = d.c_so_number where p.c_projectNumber = ?";
        PreparedStatement poStatement = con.prepareStatement(query);
        poStatement.setString(1,projectNumber);
        ResultSet soSet = poStatement.executeQuery();
        String[] soArray = null;
        Set soCollectionSet = new HashSet();
        if(salesOrder != null && !salesOrder.isEmpty()) {
            soArray = salesOrder.split(",");
            for (String soObj : soArray) {
                soCollectionSet.add(soObj.trim());
            }
        }
        else{
            salesOrderBlank = true;
        }
        while(soSet.next()){
            if(salesOrderBlank){
                changeFlag = true;
                break;
            }
            if(!soCollectionSet.contains(soSet.getString(1))){
                changeFlag = true;
                break;
            }
        }
        return changeFlag;
    }

    public static void printProjectDetails(){
        /*for(Object projectNo : projectsToBeReloaded.keySet()){
            //LogUtil.info(projectNo+"",projectsToBeReloaded.get(projectNo)+"");
        }*/
    }
    public static void startAutoReload(String lastRunDate){
        LocalDate lastRunDateObj = LocalDate.parse(lastRunDate,DateTimeFormatter.ISO_DATE);
        FormDataDao warrantyDao = (FormDataDao)AppUtil.getApplicationContext().getBean("formDataDao");
        String formId = "warranty_review_form";
        String tableName = "review_form";
        for(Object projectNo : projectsToBeReloaded.keySet()){
            FormRow row = warrantyDao.load(formId,tableName,projectsToBeReloaded.get(projectNo).toString());
            LocalDate modificationDate = null;
            boolean dateFlag = false;
            if(row != null){
                if(row.getDateModified() != null){
                    dateFlag = true;
                }
            }
            if(dateFlag == true) {
                modificationDate = LocalDate.ofInstant(row.getDateModified().toInstant(), ZoneId.systemDefault());
            }
            else{
                modificationDate = lastRunDateObj.plusDays(-1);
            }
            if(modificationDate.isBefore(lastRunDateObj)) {
                Map variables = new HashMap();
                variables.put("ordertype", "Project");
                variables.put("projectnumber", projectNo.toString());
                variables.put("invoicenumber", "");
                variables.put("processType", "Reload");
                variables.put("rId", projectsToBeReloaded.get(projectNo));
                WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
                String processDefId = "warrantyApplication#latest#warrantyLoadProcess";

                //Start a process with existing record
                org.joget.workflow.model.WorkflowProcessResult processResult = workflowManager.processStart(processDefId, variables);
            }
        }
    }
}
return AutoReloadWarranty.doBasedWarrantiesForReload();
