package org.joget.geowatch.test;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class StoreBinderForUnreservedPRSoftAllocation {
    static PreparedStatement getRecordStatement;
    static PreparedStatement updateRecordStatement;
    static Connection jogetConnection;

    public static FormRowSet store(Element element, FormRowSet rows, FormData formData) throws IOException,SQLException {
        LogUtil.info("Calling Store Binder","");
        rows.setMultiRow(true);
        String jobNumber = "#requestParam.job_number#";
        FormRowSet softAllocationFormRowSet = new FormRowSet();
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        for(FormRow row : rows){
            LogUtil.info("Oracle Ref",row.getProperty("oracle_reference"));
            String qtyRequired = row.getProperty("quantity_reserved");
            String orderNum = row.getProperty("order_num");
            LogUtil.info("Qty Required",row.getProperty("quantity_reserved"));
            LogUtil.info("PO Number",orderNum);
            if(qtyRequired != null && !qtyRequired.equals("") && !qtyRequired.equals("0")
                    && !qtyRequired.equals("0.0")){
                // get id
                row.setProperty("quantity_required",qtyRequired);
                String integrationRecordId = row.getProperty("oracle_reference");
                // get record from integration table
                int status = getRecordVerifyUpdate(qtyRequired,integrationRecordId);
                if(status > 0){

                    // add record to soft allocation row set
                    row.setProperty("job_number",jobNumber);
                    row.setProperty("id", UuidGenerator.getInstance().getUuid());
                    row.setDateCreated(new Date());
                    row.setDateModified(new Date());
                    softAllocationFormRowSet.add(row);
                }
            }
        }
        LogUtil.info("Size",""+softAllocationFormRowSet.size());
        formDataDao.saveOrUpdate("maSoftAllocationDetails","ods_soft_allc_det1",softAllocationFormRowSet);
        if(jogetConnection != null && !jogetConnection.isClosed()){
            closeConnection();
        }
        return rows;
    }

    public static int getRecordVerifyUpdate(String qtyRequired, String recordId) throws SQLException{
        // get record
        PreparedStatement getRecord = getRecordPrepareStatement();
        int updateStatus = 0;
        getRecord.setString(1,recordId);
        ResultSet set = getRecord.executeQuery();
        if(set != null && !set.isClosed()){
            set.next();
            String qtyForAllocation = set.getString("c_qty_for_allocation");
            if(qtyForAllocation != null && !qtyForAllocation.equals("") && !qtyForAllocation.equals("0")
                    && !qtyForAllocation.equals("0.0")) {
                double allocationQty = Double.parseDouble(qtyForAllocation);
                double requiredQty = Double.parseDouble(qtyRequired);
                if(allocationQty >= requiredQty){
                    allocationQty = allocationQty - requiredQty;
                    PreparedStatement updateRecord = updateRecordPrepareStatement();
                    updateRecord.setString(1,Double.toString(allocationQty));
                    updateRecord.setString(2,recordId);
                    updateStatus = updateRecord.executeUpdate();
                }
            }
        }
        return updateStatus;
    }

    public static PreparedStatement getRecordPrepareStatement() throws SQLException{
        if(getRecordStatement != null){
            return getRecordStatement;
        }
        else {
            getJogetConnection();
            if (jogetConnection != null && !(jogetConnection.isClosed())){
                String query = "SELECT * FROM app_fd_ods_prdetails WHERE 1=1 AND id = ?";
                getRecordStatement = jogetConnection.prepareStatement(query);
            }
            return getRecordStatement;
        }
    }

    public static PreparedStatement updateRecordPrepareStatement() throws SQLException{
        if(updateRecordStatement != null){
            return updateRecordStatement;
        }
        else {
            getJogetConnection();
            if (jogetConnection != null && !(jogetConnection.isClosed())){
                String query = "Update app_fd_ods_prdetails SET c_qty_for_allocation = ? WHERE 1=1 AND id = ?";
                updateRecordStatement = jogetConnection.prepareStatement(query);
            }
            return updateRecordStatement;
        }
    }

    public static void getJogetConnection() throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        jogetConnection = ds.getConnection();
    }

    public static void closeConnection() throws SQLException {
        if(jogetConnection != null && !(jogetConnection.isClosed())){
            jogetConnection.isClosed();
        }
    }
}
StoreBinderForUnreservedPRSoftAllocation.store(element,rows,formData);
