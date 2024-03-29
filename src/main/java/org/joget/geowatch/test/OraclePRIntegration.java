import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.Date;

public class OraclePRIntegration {
    //static String transactionUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicmtlint/mtl_transactions_interface/";
    //static String qtyUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oiconhand/query_quantities/";
    //static String costUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicitemcost/get_item_cost/";
    static String authorization = "#envVariable.ommrestapiauthentication#";

    static Connection jogetConnection;
    static PreparedStatement prepStatement;
    static PreparedStatement jogetPrepStatement;
    static PreparedStatement jogetPoNumberUpdateStatement;
    public static void oracleDataIntegration() throws ClassNotFoundException, SQLException {
        Connection con = null;
        Class.forName("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection("jdbc:oracle:thin:#envVariable.jdbcurlIP#", "#envVariable.jdbcuserName#", "#envVariable.jdbcPassword#");
        LogUtil.info("View call","ODS OMM Item MaSTER Order DSN connection sussessfully created.*******");
        if (!con.isClosed()) {
            LogUtil.info("Connection Status",""+con.isClosed());
            String query = "SELECT DISTINCT PR_NUMBER,JOB_NUMBER, PR_LINE_NUMBER, ITEM_NUMBER, ITEM_DESCRIPTION, UOM, QUANTITY, QUANTITY_DELIVERED, QUANTITY_DUE, INVENTORY_ITEM_ID, ORDER_NUM FROM apps.XXOIC_ORAEBS_JOGET_INT_V";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            LogUtil.info("Result Set Data","");
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            FormRowSet rowsFileMetaData = new FormRowSet();
            FormRowSet softAllocationRowSet = new FormRowSet();
            try {
                while (rs.next()) {


                    String jobNumber = processColumn(rs,"JOB_NUMBER");

                    LogUtil.info("Traversing Rows", rs.getString("PR_NUMBER"));
                    FormRow row = new FormRow();
                    String uuid = UuidGenerator.getInstance().getUuid();
                    row.setProperty("id", uuid);
                    String prNumber = processColumn(rs, "PR_NUMBER");
                    String prLineNumber = processColumn(rs, "JOB_NUMBER");
                    if(prNumber.equals("")){
                        continue;
                    }

                    row.setProperty("pr_number", (processColumn(rs, "PR_NUMBER")));
                    row.setProperty("job_number", (processColumn(rs, "JOB_NUMBER")));
                    row.setProperty("pr_line_number", (processColumn(rs, "PR_LINE_NUMBER")));
                    row.setProperty("item_number", (processColumn(rs, "ITEM_NUMBER")));
                    row.setProperty("item_description", (processColumn(rs, "ITEM_DESCRIPTION")));
                    row.setProperty("UOM", (processColumn(rs, "UOM")));
                    row.setProperty("quantity", (processColumn(rs, "QUANTITY")));
                    row.setProperty("quantity_delivered", (processColumn(rs, "QUANTITY_DELIVERED")));
                    row.setProperty("quantity_due", (processColumn(rs, "QUANTITY_DUE")));
                    row.setProperty("order_num", (processColumn(rs, "ORDER_NUM")));
                    row.setProperty("inventory_item_id", (processColumn(rs, "INVENTORY_ITEM_ID")));
                    row.setProperty("completed", "N");
                    if(row.getProperty("pr_number").equals("12570")){
                        LogUtil.info("$$$$$$ Record $$$$$", " PR Number:"+row.getProperty("pr_number")+" Job No: "+row.getProperty("job_number")+" PR Line Number :"+
                                row.getProperty("pr_line_number")+" Item Number: "+row.getProperty("item_number")+" Item Desc: "+row.getProperty("item_description")+" UOM: "+
                                row.getProperty("UOM")+" Quantity : "+row.getProperty("quantity")+" Qty Delivered : "+row.getProperty("quantity_delivered")+" Quantity Due"+
                                row.getProperty("quantity_due")+" Order Num"+row.getProperty("order_num")+" Inventor Id : "+row.getProperty("inventory_item_id"));
                    }
                    /*LogUtil.info("Record","PR Number:"+row.getProperty("pr_number")+" Job No: "+row.getProperty("job_number")+" PR Line Number :"+
                            row.getProperty("pr_line_number")+" Item Number: "+row.getProperty("item_number")+" Item Desc: "+row.getProperty("item_description")+" UOM: "+
                            row.getProperty("UOM")+" Quantity : "+row.getProperty("quantity")+" Qty Delivered"+row.getProperty("quantity_delivered")+" Quantity Due"+
                            row.getProperty("quantity_due")+" Order Num"+row.getProperty("order_num")+" Inventor Id : "+row.getProperty("inventory_item_id"));
*/
                    row = saveOrUpdateRowStatus(row, softAllocationRowSet);
                    if (row != null) {
                        rowsFileMetaData.add(row);
                    }
                    //saveOrUpdate(formId, table, FormRowSet)
                    //rowsFileMetaData.add(row);
                }
            }
            catch(Exception ex){
                LogUtil.error("Error",ex,ex.getMessage());
            }
            LogUtil.info("Integration Table rowset",""+rowsFileMetaData.size());
            LogUtil.info("Soft Allocation Table ",""+softAllocationRowSet.size());

            for(FormRow row : rowsFileMetaData){
                LogUtil.info("id "+row.getId(), "Name "+row.getProperty("item_number"));
            }

            formDataDao.saveOrUpdate("prDetailsOracle", "ods_prdetails", rowsFileMetaData);
            formDataDao.saveOrUpdate("maSoftAllocationDetails","ods_soft_allc_det1",softAllocationRowSet);
            LogUtil.info("View Call :","****ods omm Item Master has been created successfully***");
        }
        con.close();
        closeConnection();
    }

    public static FormRow saveOrUpdateRowStatus(FormRow row, FormRowSet softAllocationRowSet) throws SQLException {

        String prNum = row.getProperty("pr_number");
        String itemCode = row.getProperty("item_number");
        String prLineNumber = row.getProperty("pr_line_number");
        LogUtil.info(prNum,itemCode);
        PreparedStatement pStatement = getPreparedStatement();
        LogUtil.info("",pStatement.toString());
        pStatement.setString(1,prNum);
        pStatement.setString(2, itemCode);
        pStatement.setString(3,prLineNumber);
        ResultSet set = pStatement.executeQuery();
        String statusForSoftAllocation = "New";
        double quantityDue = row.getProperty("quantity_due").equals("") ? 0.0 : Double.parseDouble(row.getProperty("quantity_due"));
        double quantityDelievered = row.getProperty("quantity_delivered").equals("") ? 0.0 : Double.parseDouble(row.getProperty("quantity_delivered"));
        double quantityOracle = row.getProperty("quantity").equals("") ? 0.0 : Double.parseDouble(row.getProperty("quantity"));
        row.setProperty("qty_for_allocation", Double.toString(quantityOracle - quantityDelievered));
        // check if record needs to be updated in integration table and soft allocation table
        if(set != null && !(set.isClosed())){
            if(set.next()) {
                String oraclePoNumber = row.getProperty("order_num");
                String poNumberJoget = set.getString("c_order_num");
                double quantityDueJoget = Double.parseDouble(set.getString("c_quantity_due"));
                double quantityJoget = Double.parseDouble(set.getString("c_quantity"));
                String quantityForAllocation = set.getString("c_qty_for_allocation").trim();
                double allocationQty = 0.0;
                if(quantityForAllocation != null && !quantityForAllocation.equals("")){
                    allocationQty = Double.parseDouble(quantityForAllocation);
                }

                if (quantityDue != quantityDueJoget || quantityOracle != quantityJoget || (oraclePoNumber != null && !oraclePoNumber.equals(poNumberJoget))) {
                    statusForSoftAllocation = "Update";
                    row.setDateModified(new Date());
                    // if po change update po number
                    if(!oraclePoNumber.equals(poNumberJoget)){
                        LogUtil.info("PO Update Code",oraclePoNumber+" - "+poNumberJoget);
                        updatePoNumber(oraclePoNumber, set.getString("id"));
                    }
                    // update qty for allocation
                    if (quantityOracle != quantityJoget) {
                        double qtyToBeUpdated = quantityOracle - quantityJoget;
                        double qtyAvailableInOracle = quantityOracle - quantityDelievered;
                        double qtyAvailableJoget = qtyToBeUpdated + allocationQty;
                        double qtyForAll = Math.min(qtyAvailableJoget,qtyAvailableInOracle);
                        row.setProperty("qty_for_allocation", Double.toString(qtyForAll));
                    }
                    else{
                        double qtyForAllocation = Math.min(allocationQty,(quantityOracle - quantityDelievered));
                        row.setProperty("qty_for_allocation",Double.toString(qtyForAllocation));
                    }
                    // record needs to be updated
                    LogUtil.info("Database id",set.getString("id"));
                    row.setProperty("id", set.getString("id"));
                    row.setProperty("quantity_due", Double.toString(quantityDue));
                    row.setDateCreated(new Date());
                    row.setDateModified(new Date());
                    row.setProperty("old_quantity_delievered", processColumn(set, "c_QUANTITY_DELIVERED"));

                    if (quantityDue == 0) {
                        row.setProperty("completed", "Y");
                    }
                    else{
                        row.setProperty("completed","N");
                    }
                    // update soft allocation record
                } else {
                    statusForSoftAllocation = "Discard";
                    row = null;
                }
            }
        }
        else{
            row.setDateCreated(new Date());
            row.setDateModified(new Date());
        }
        // call for soft allocation opeartion
        // create soft allocation row
        if(row != null) {
            double pendingQty = quantityOracle - quantityDelievered;
            LogUtil.info("Working on Soft allocation","");
            LogUtil.info("Working on Soft allocation Oracle ref",row.getProperty("id"));
            FormRow softAllocationRow = new FormRow();
            softAllocationRow.setProperty("job_number", processColumn(row.getProperty("job_number")));
            softAllocationRow.setProperty("pr_number", prNum);
            softAllocationRow.setProperty("item_number", itemCode);
            softAllocationRow.setProperty("item_description", row.getProperty("item_description"));
            softAllocationRow.setProperty("UOM", row.getProperty("UOM"));
            softAllocationRow.setProperty("quantity", row.getProperty("quantity"));
            softAllocationRow.setProperty("pending_for_allocation", Double.toString(pendingQty));
            softAllocationRow.setProperty("quantity_required", row.getProperty("quantity"));
            softAllocationRow.setProperty("status_of_allocation", "");
            softAllocationRow.setProperty("inventory_item_id", row.getProperty("inventory_item_id"));
            softAllocationRow.setProperty("oracle_reference", row.getProperty("id"));
            softAllocationRow.setProperty("pr_line_number", row.getProperty("pr_line_number"));
            softAllocationRow.setProperty("id", UuidGenerator.getInstance().getUuid());
            softAllocationRow.setProperty("order_num", row.getProperty("order_num"));


            String jobNumber = softAllocationRow.getProperty("job_number");
            String qtyDelievered = row.getProperty("quantity_delivered");
            String qtyDue = row.getProperty("quantity_due");
            String oldQtyDelievered = row.getProperty("old_quantity_delievered");

            if (((jobNumber == null || jobNumber.equals("")) && statusForSoftAllocation.equals("New")) ||
                    (statusForSoftAllocation.equals("New") && (qtyDue == null || qtyDue.equals("") ||qtyDue.equals("0")
                            || qtyDue.equals("0.0")))) {
                statusForSoftAllocation = "Discard";
            }
            else {
                saveOrUpdateSoftAllocationRecord(softAllocationRow, softAllocationRowSet, qtyDelievered, oldQtyDelievered, statusForSoftAllocation);
            }
        }
        //row = null;
        return row;
    }

    public static void saveOrUpdateSoftAllocationRecord(FormRow row, FormRowSet softAllocation, String qtyDelievered, String oldQtyDelievered, String status) throws SQLException {
        LogUtil.info("Soft allocation",status);
        String oracleReference = row.getProperty("oracle_reference");
        LogUtil.info("",oracleReference);
        double delieveredQty = 0.0;
        double oldQty = 0.0;
        if(status.equalsIgnoreCase("discard")){
            return;
        }
        else if(status.equalsIgnoreCase("new")){
            LogUtil.info("Running soft allocation for new record",""+softAllocation.size());
            row.setProperty("quantity_required", row.getProperty("pending_for_allocation"));
            //row.setProperty("quantity_required", row.getProperty("pending_for_allocation"));
            row.setDateCreated(new Date());
            row.setDateModified(new Date());
            softAllocation.add(row);
        }
        else {
            LogUtil.info("Update Part","");
            if (qtyDelievered != null && !qtyDelievered.equals("")) {
                delieveredQty = Double.parseDouble(qtyDelievered);
            }
            if (oldQtyDelievered != null && !oldQtyDelievered.equals("")) {
                oldQty = Double.parseDouble(oldQtyDelievered);
            }
            delieveredQty = Math.abs(delieveredQty - oldQty);
            LogUtil.info("Delivered Qty",""+delieveredQty);
            if (delieveredQty != 0.0) {
                PreparedStatement pStatement = getPreparedStatementSoftAllocation();
                pStatement.setString(1, oracleReference);
                ResultSet set = pStatement.executeQuery();
                if (set != null && !(set.isClosed())) {
                    while (set.next()) {
                        String pendingForAllocation = set.getString("c_pending_for_allocation");
                        if(pendingForAllocation.equals("0.0")){
                            continue;
                        }
                        FormRow tempRow = new FormRow();
                        tempRow.putAll(row);
                        String qtyRString = processColumn(set,"c_quantity_required");
                        LogUtil.info("Job Number At the beginning",set.getString("c_job_number"));
                        tempRow.setProperty("job_number",processColumn(set,"c_job_number"));
                        tempRow.setProperty("pending_for_allocation",processColumn(set, "c_pending_for_allocation"));
                        tempRow.setProperty("quantity_required",qtyRString);
                        tempRow.setProperty("action","Open");
                        tempRow.setDateModified(new Date());
                        double qtyR = Double.parseDouble((qtyRString == null && qtyRString.trim().equals(""))? "0.0" : qtyRString );

                        String pendingQty = set.getString("c_pending_for_allocation");
                        String previousStatus = set.getString("c_action_trail");
                        if(previousStatus == null || previousStatus.equals("")){
                            previousStatus = set.getString("c_status_of_allocation");
                        }
                        String previousAction = set.getString("c_action") == null ? "" : set.getString("c_action");
                        tempRow.setProperty("action","Open");
                        previousStatus = previousStatus != null ? previousStatus : "";
                        double pendingQtyS = 0.0;
                        tempRow.setProperty("id", set.getString("id"));
                        if (pendingQty != null && !pendingQty.equals("")) {
                            LogUtil.info("Update status",""+pendingQty);
                            pendingQtyS = Double.parseDouble(pendingQty);
                            if (pendingQtyS <= delieveredQty) {
                                LogUtil.info("Pending Qty is less","");
                                tempRow.setProperty("pending_for_allocation", "0.0");
                                tempRow.setProperty("status_of_allocation", " Qty " + pendingQtyS + " is available for hard allocation ( "+ LocalDate.now()+" )." );
                                tempRow.setProperty("action_trail",generateStatusTrail(previousStatus,""+pendingQtyS,previousAction));
                                tempRow.setProperty("s_qty_delivered",""+(qtyR));
                                delieveredQty = delieveredQty - pendingQtyS;
                                if (delieveredQty == 0.0) {
                                    softAllocation.add(tempRow);
                                    break;
                                }
                            } else if (pendingQtyS > delieveredQty) {
                                LogUtil.info("Pending Qty is More","");
                                tempRow.setProperty("pending_for_allocation", Double.toString(pendingQtyS - delieveredQty));
                                tempRow.setProperty("status_of_allocation", " Qty " + delieveredQty + " is available for hard allocation ( "+ LocalDate.now()+" )." );
                                tempRow.setProperty("action_trail",generateStatusTrail(previousStatus,""+delieveredQty,previousAction));
                                LogUtil.info("Qty Delievered",(qtyR - (pendingQtyS - delieveredQty))+"");
                                tempRow.setProperty("s_qty_delivered",(qtyR - (pendingQtyS - delieveredQty))+"");
                            }
                        }
                        LogUtil.info("Job Number",processColumn(set,"c_job_number"));
                        softAllocation.add(tempRow);
                    }
                }
            }
        }
    }

    public static void getJogetConnection() throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        jogetConnection = ds.getConnection();
    }

    public static PreparedStatement getPreparedStatement() throws SQLException {
        if(prepStatement != null){
            return prepStatement;
        }
        else {
            getJogetConnection();
            if (jogetConnection != null && !(jogetConnection.isClosed())){
                String query = "SELECT * FROM app_fd_ods_prdetails WHERE 1=1 AND c_pr_number = ? AND " +
                        "c_item_number = ? AND c_pr_line_number = ?";
                prepStatement = jogetConnection.prepareStatement(query);
            }
            return prepStatement;
        }
    }

    public static PreparedStatement getPreparedStatementSoftAllocation() throws SQLException {
        if(jogetPrepStatement != null){
            return jogetPrepStatement;
        }
        else {
            getJogetConnection();
            if (jogetConnection != null && !(jogetConnection.isClosed())){
                String query = "SELECT * FROM app_fd_ods_soft_allc_det1 WHERE 1=1 AND c_oracle_reference = ?";
                jogetPrepStatement = jogetConnection.prepareStatement(query);
            }
            return jogetPrepStatement;
        }
    }
    public static PreparedStatement getPreparedUpdatePoNumber() throws SQLException{
        if(jogetPoNumberUpdateStatement != null){
            return jogetPoNumberUpdateStatement;
        }
        else {
            getJogetConnection();
            if (jogetConnection != null && !(jogetConnection.isClosed())){
                String query = "Update app_fd_ods_soft_allc_det1 SET c_order_num = ? WHERE 1=1 AND c_oracle_reference = ?";
                jogetPoNumberUpdateStatement = jogetConnection.prepareStatement(query);
            }
            return jogetPoNumberUpdateStatement;
        }
    }

    public static void updatePoNumber(String poNumber, String oracleRef) throws SQLException {
        LogUtil.info("calling po update",poNumber);
        LogUtil.info("oracle ref",oracleRef);
        PreparedStatement prepStatement = getPreparedUpdatePoNumber();
        prepStatement.setString(1, poNumber);
        prepStatement.setString(2, oracleRef);
        prepStatement.executeUpdate();
        LogUtil.info("update success","");
    }

    public static void closeConnection() throws SQLException {
        if(jogetConnection != null && !(jogetConnection.isClosed())){
            jogetConnection.isClosed();
        }
    }

    public static String processColumn(ResultSet rs, String name) throws SQLException {
        return (rs.getString(name) == null ? "" : rs.getString(name).toString().trim());
    }
    public static String processColumn(String name) throws SQLException {
        return (name == null ? "" : name);
    }

    public static String generateStatusTrail(String previousStatus, String qty, String action){
        previousStatus = previousStatus.concat(" Qty " + qty + " is available for hard allocation ( "+ LocalDate.now()+" ).");
        if(action != null && !action.equals("")){
            previousStatus = previousStatus.concat("Status - ("+action+")");
        }
        return previousStatus;
    }
}
OraclePRIntegration.oracleDataIntegration();
