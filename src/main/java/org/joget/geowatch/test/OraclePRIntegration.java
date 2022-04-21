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
    static Connection jogetConnection;
    static PreparedStatement prepStatement;
    static PreparedStatement jogetPrepStatement;
    public static void oracleDataIntegration() throws ClassNotFoundException, SQLException {
        Connection con = null;
        Class.forName("oracle.jdbc.driver.OracleDriver");
        con = DriverManager.getConnection("jdbc:oracle:thin:#envVariable.jdbcurlIP#", "#envVariable.jdbcuserName#", "#envVariable.jdbcPassword#");
        LogUtil.info("View call","ODS OMM Item MaSTER Order DSN connection sussessfully created.*******");
        if (!con.isClosed()) {
            LogUtil.info("Connection Status",""+con.isClosed());
            String query = "SELECT PR_NUMBER,JOB_NUMBER, PR_LINE_NUMBER, ITEM_NUMBER, ITEM_DESCRIPTION, UOM, QUANTITY, QUANTITY_DELIVERED, QUANTITY_DUE, INVENTORY_ITEM_ID, ORDER_NUM FROM apps.XXOIC_ORAEBS_JOGET_INT_V";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            LogUtil.info("Result Set Data","");
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            FormRowSet rowsFileMetaData = new FormRowSet();
            FormRowSet softAllocationRowSet = new FormRowSet();
            try {
                while (rs.next()) {
                    LogUtil.info("Traversing Rows", rs.getString("PR_NUMBER"));
                    FormRow row = new FormRow();
                    String uuid = UuidGenerator.getInstance().getUuid();
                    //String uidvalues = rs.getString("segment1");
                    //************ Start the process to insert the values from oracle to app_fd_itemmaster table

                    // String uniquevalues=rs.getString("ORG_ID")+"-"+rs.getString("PROJECT_NUMBER");

                    //segment1,description,primary_uom_code,creation_date
                    row.setProperty("id", uuid);
                    //row.setProperty("org_id", (processColumn(rs,"ORG_ID")));
                    //row.setProperty("organization_id", (processColumn(rs,"ORGANIZATION_ID")));
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
                    row.setProperty("qty_for_allocation", (processColumn(rs, "QUANTITY")));

                    /*if(processColumn(rs,"JOB_NUMBER").equals("")){
                        row.setProperty("quantity", "20.00");
                    }
                    else{
                        row.setProperty("qty_for_allocation", (processColumn(rs, "QUANTITY")));
                    }*/
                    //row.setProperty("quantity", "20.00");
                    //row.setProperty("quantity_delivered", (processColumn(rs, "QUANTITY_DELIVERED")));
                    row.setProperty("quantity_delivered", "2.0");
                    //row.setProperty("quantity_due", (processColumn(rs, "QUANTITY_DUE")));
                    row.setProperty("quantity_due", "8.0");
                    /*if(processColumn(rs,"JOB_NUMBER").equals("")){
                        row.setProperty("quantity_due", "10.0");
                    }
                    else{
                        row.setProperty("quantity_due", "0.0");
                    }*/
                    //row.setProperty("order_num", (processColumn(rs, "ORDER_NUM")));
                    row.setProperty("order_num", "PO1");
                    //row.setProperty("Line_num", (processColumn(rs,"LINE_NUM")));
                    //row.setProperty("supplier_name", (processColumn(rs,"SUPPLIER_NAME")));
                    //row.setProperty("vendor_site_code", (processColumn(rs,"VENDOR_SITE_CODE")));
                    //row.setProperty("receipt_num", (processColumn(rs,"RECEIPT_NUM")));
                    //row.setProperty("transaction_date", (processColumn(rs,"TRANSACTION_DATE")));
                    //row.setProperty("receiver", (processColumn(rs,"RECEIVER")));
                    row.setProperty("inventory_item_id", (processColumn(rs, "INVENTORY_ITEM_ID")));
                    row.setProperty("completed", "N");

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
        // check if record needs to be updated in integration table and soft allocation table
        if(set != null && !(set.isClosed())){
            if(set.next()) {
                String oraclePoNumber = row.getProperty("order_num");
                String poNumberJoget = set.getString("c_order_num");
                double quantityDueJoget = Double.parseDouble(set.getString("c_quantity_due"));
                double quantityJoget = Double.parseDouble(set.getString("c_quantity"));
                String quantityForAllocation = set.getString("c_qty_for_allocation");
                row.setProperty("qty_for_allocation",processColumn(quantityForAllocation));

                if (quantityDue != quantityDueJoget || quantityOracle != quantityJoget || (oraclePoNumber != null && !oraclePoNumber.equals(poNumberJoget))) {
                    statusForSoftAllocation = "Update";
                    if (quantityOracle != quantityJoget) {
                        double qtyToBeUpdated = quantityOracle - quantityJoget;
                        double qtyForAllocation = row.getProperty("qty_for_allocation").equals("") ? 0.0 : Double.parseDouble(row.getProperty("qty_for_allocation"));
                        qtyForAllocation = qtyForAllocation + qtyToBeUpdated;
                        row.setProperty("qty_for_allocation", Double.toString(qtyForAllocation));
                    }
                    // record needs to be updated
                    LogUtil.info("Database id",set.getString("id"));
                    row.setProperty("id", set.getString("id"));
                    row.setProperty("quantity_due", Double.toString(quantityDue));
                    row.setDateCreated(row.getDateCreated());
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
            String oldQtyDelievered = row.getProperty("old_quantity_delievered");

            if (jobNumber == null || jobNumber.equals("")) {
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
        String poNumber = row.getProperty("order_num");
        double delieveredQty = 0.0;
        double oldQty = 0.0;
        if(status.equalsIgnoreCase("discard")){
            return;
        }
        else if(status.equalsIgnoreCase("new")){
            LogUtil.info("Running soft allocation for new record",""+softAllocation.size());
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
                        String pendingQty = set.getString("c_pending_for_allocation");
                        String previousStatus = set.getString("c_status_of_allocation");
                        previousStatus = previousStatus != null ? previousStatus : "";
                        double pendingQtyS = 0.0;
                        row.setProperty("id", set.getString("id"));
                        if (pendingQty != null && !pendingQty.equals("")) {
                            LogUtil.info("Update status",""+pendingQty);
                            pendingQtyS = Double.parseDouble(pendingQty);
                            if (pendingQtyS <= delieveredQty) {
                                LogUtil.info("Pending Qty is less","");
                                row.setProperty("pending_for_allocation", "0.0");
                                row.setProperty("status_of_allocation", previousStatus.concat(" Qty " + pendingQtyS + " is available for hard allocation ( "+ LocalDate.now()+" )."));
                                delieveredQty = delieveredQty - pendingQtyS;
                                if (delieveredQty == 0.0) {
                                    softAllocation.add(row);
                                    break;
                                }
                            } else if (pendingQtyS > delieveredQty) {
                                LogUtil.info("Pending Qty is More","");
                                row.setProperty("pending_for_allocation", Double.toString(pendingQtyS - delieveredQty));
                                row.setProperty("status_of_allocation", previousStatus.concat(" Qty " + delieveredQty + " is available for hard allocation ( "+ LocalDate.now()+" )."));
                            }
                        }
                        softAllocation.add(row);
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
}
OraclePRIntegration.oracleDataIntegration();
