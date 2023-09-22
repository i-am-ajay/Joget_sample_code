package org.joget.geowatch.test;

import org.joget.apps.form.model.*;
import org.joget.apps.form.service.*;
import org.joget.apps.app.service.*;
import org.joget.commons.util.LogUtil;

import java.sql.*;
import java.text.NumberFormat;
import java.util.*;
import javax.sql.*;


public class FabricationProductionPlanning {

    String grossWeight = "0";
    String grossArea = "0";

            //Shape
    String shapeBalance = "0";
            // Plain
    String plainBalance = "0";
    // Stairtread
    String stairtreadBalance = "0";
    // Structure
    String structureBalance = "0";
    // Press Lock Grating
    String presslockGratingBalance = "0";
    // Accessories
    String accessoriesBalance = "0";
    // FRP Grating
    String frpGratingBalance = "0";
    // FRP Stairtread
    String fRPStairtreadsBalance = "0";
    // Plasma
    String plasma = "0";
    String teamName = "#requestParam.teamName#";
    //String teamName = "Team A";

    String productionBalance = "0";
    String sawCuttingBalance = "0";
    String preparationBalance = "0";
    String stdStartDate = "";
    String stdEndDate = "";

    double cuttingPercentage = 0;

    FormRowSet f = new FormRowSet();

    public FormRowSet load(Element element, String username, FormData formData) {

        LogUtil.info("Fabrication Panel ", " ##### Production Planning Loading #####");
        // LogUtil.info("Team Name ", teamName);
        f.setMultiRow(true);
        Form parentForm = FormUtil.findRootForm(element);
        //Form form = FormUtil.findRootForm(element);
        Element storeDataFieldElement = FormUtil.findElement("storeData", parentForm, formData);
        String[] storeDataFieldValueArray = FormUtil.getElementPropertyValues(storeDataFieldElement, formData);
        //String storeData = storeDataFieldValueArray[0];
        LogUtil.info("Store Data Status ",  String.valueOf(storeDataFieldValueArray));
        if (teamName != null && teamName.length() > 0 && storeDataFieldValueArray.length == 0) {
        loadFabricationWorkOrder();
        }
        if(storeDataFieldValueArray.length > 0){
        String storeData = storeDataFieldValueArray[0];
        LogUtil.info("Store Data Status ",  storeData);
        }
        LogUtil.info("------------ End of Fabrication Panel ---------------", "------------------");
        return f;
    }

/**
 * This method is used to get the fabrication work order details
 */
    public void loadFabricationWorkOrder() {
        LogUtil.info(" Fabrication Panel ", " Getting work order details ");
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet workOrderRs = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
            con = ds.getConnection();
            if (!con.isClosed()) {

                String query = "select app_fd_ods_wo_approval.c_joborderno as 'c_work_orderno', max(app_fd_ods_work_order.c_crossbar_size) as 'c_crossbar_size',sum(cast(app_fd_ods_work_order.c_pending_quantity as float)) as 'c_pending_quantity',max(app_fd_ods_work_order.c_wo_approvedDate) as 'c_wo_approvedDate', " +
                "max(app_fd_ods_work_order.c_thickness) as 'c_thickness',max(app_fd_ods_work_order.c_loadbartype) as 'c_loadbartype',max(app_fd_ods_work_order.c_loadbar_pitch) as 'c_loadbar_pitch' ,sum(cast(app_fd_ods_work_order.c_scheduled_quantity as float)) as 'c_scheduled_quantity'," +
                "max(app_fd_ods_joborder_fab.c_weldingdetails) as 'c_weldingdetails' , max(app_fd_ods_wo_approval.c_esabPlasmaStatus) as 'c_esabPlasmaStatus', max(app_fd_ods_wo_approval.c_programStatus) as 'c_programStatus', max(app_fd_ods_wo_approval.c_nestingStatus) as 'c_nestingStatus', " +
                "max(app_fd_ods_joborder_fab.c_weldingdetails_other) as 'c_weldingdetails_other',max(app_fd_ods_wo_approval.c_nosingStatus) as 'c_nosingStatus',max(app_fd_ods_wo_approval.c_deliveryDate) as 'c_deliveryDate',max(app_fd_ods_wo_approval.c_ma_status) as 'c_ma_status', " +
                "app_fd_ods_wo_approval.c_part_no as 'c_part_no',max(app_fd_ods_wo_approval.c_customer_name) as 'c_customer_name' from app_fd_ods_work_order RIGHT JOIN app_fd_ods_wo_approval " +
                "ON app_fd_ods_work_order.c_woApprovalId = app_fd_ods_wo_approval.id " +
                "RIGHT JOIN app_fd_ods_joborder_fab ON app_fd_ods_joborder_fab.id = app_fd_ods_work_order.c_fabricationJobOrderId " +
                "where app_fd_ods_work_order.c_workOrderType='Fabrication' " +
                "and app_fd_ods_wo_approval.c_wo_status='Approved' and app_fd_ods_joborder_fab.c_activeStatus='Release' and app_fd_ods_work_order.c_woApprovalId<>'' and app_fd_ods_work_order.c_jobtype != 'Grating Clips' " +
                "group by app_fd_ods_wo_approval.c_joborderno,app_fd_ods_wo_approval.c_part_no order by CAST(app_fd_ods_wo_approval.c_joborderno AS int) desc";

                pstmt = con.prepareStatement(query);
                workOrderRs = pstmt.executeQuery();
                // LogUtil.info(" Fabrication PP Query ", query);
                int i = 1;
                while (workOrderRs.next()) {
                    FormRow r1 = new FormRow();
                    String workOrderNumber = checkNull(workOrderRs.getString("c_work_orderno"));
                    String partNo = checkNull(workOrderRs.getString("c_part_no"));
                    String customerName = checkNull(workOrderRs.getString("c_customer_name"));
                    String height = checkNull("");
                    String thickness = checkNull(workOrderRs.getString("c_thickness"));
                    String pitch = checkNull(workOrderRs.getString("c_loadbar_pitch"));
                    String cross = checkNull(workOrderRs.getString("c_crossbar_size"));
                    String type = checkNull(workOrderRs.getString("c_loadbartype"));
                    String finish = checkNull("");
                    String pendingQuantity = checkNull(workOrderRs.getString("c_pending_quantity"));
                    String scheduledQuantity = checkNull(workOrderRs.getString("c_scheduled_quantity"));
                    String woApprovedDate = checkNull(workOrderRs.getString("c_wo_approvedDate"));
                    String welding = checkNull(workOrderRs.getString("c_weldingdetails"));
                    String esabPlasmaStatus = checkNull(workOrderRs.getString("c_esabPlasmaStatus"));
                    String programStatus = checkNull(workOrderRs.getString("c_programStatus"));
                    String nestingStatus = checkNull(workOrderRs.getString("c_nestingStatus"));
                    String nosingStatus = checkNull(workOrderRs.getString("c_nosingStatus"));
                    String deliveryDate = checkNull(workOrderRs.getString("c_deliveryDate"));
                    String maStatus = checkNull(workOrderRs.getString("c_ma_status"));

                    stdStartDate = "0";
                    stdEndDate = "0";

                    if ("Other".equals(welding)) {
                        welding = checkNull(workOrderRs.getString("c_weldingdetails_other"));
                    }
                    getGrossDetails(workOrderNumber, partNo, con);
                    cuttingPercentage = (Double.parseDouble(plainBalance) / Double.parseDouble(productionBalance)) * 100;
                    cuttingPercentage = Math.round(cuttingPercentage * 100.0) / 100.0;
                    r1.put("priority", "");
                    r1.put("work_orderno", workOrderNumber);
                    r1.put("part_no", partNo);
                    r1.put("customer", customerName);
                    r1.put("height", height);
                    r1.put("thickness", thickness);
                    r1.put("pitch", pitch);
                    r1.put("pending_quantity", pendingQuantity);
                    r1.put("scheduled_quantity", scheduledQuantity);
                    r1.put("cross", cross);
                    r1.put("type", type);
                    r1.put("finish", finish);
                    r1.put("gross_weight", grossWeight);
                    r1.put("gross_area", grossArea);

                    // Shape
                    r1.put("shapeAllocation", shapeBalance);
                    // Profile
                    r1.put("plainAllocation", plainBalance);
                    // Stairtread
                    r1.put("stair_treadAllocation", stairtreadBalance);
                    // Structure
                    r1.put("structureAllocation", structureBalance);
                    // PressLock Grating
                    r1.put("presslock_gratingAllocation", presslockGratingBalance);
                    // Accessories
                    r1.put("accessoriesAllocation", accessoriesBalance);
                    //FRP Grating
                    r1.put("frp_gratingAllocation", frpGratingBalance);
                    // FRP Stairtread
                    r1.put("frp_stairtreadsAllocation", fRPStairtreadsBalance);

                    r1.put("ma_status", maStatus);
                    r1.put("prod_balance", productionBalance);
                    //  For below Four fields data is coming from work order
                    r1.put("esab_plasma", esabPlasmaStatus);
                    r1.put("program", programStatus);
                    r1.put("nesting", nestingStatus);
                    r1.put("nosing", nosingStatus);
                    r1.put("std_start_date", stdStartDate);
                    r1.put("std_end_date", stdEndDate);
                    r1.put("sawcutting_balance", sawCuttingBalance);
                    r1.put("preparation_balance", preparationBalance);
                    r1.put("end_date", "");
                    r1.put("bd7", "200");
                    r1.put("total_hours", "");
                    r1.put("friday", "");
                    r1.put("holiday", "");
                    r1.put("completion_date", deliveryDate);
                    r1.put("standard", welding);
                    r1.put("cutting_qty", String.valueOf(cuttingPercentage));
                    r1.put("shift_hours", "20");
                    r1.put("wo_issue_date", woApprovedDate);

                    if(!shapeBalance.equals("0") || !frpGratingBalance.equals("0") || !plainBalance.equals("0") || !stairtreadBalance.equals("0") || !structureBalance.equals("0") || !presslockGratingBalance.equals("0") || !accessoriesBalance.equals("0") || !fRPStairtreadsBalance.equals("0")){
                        f.add(r1);
                    }
                    i = i + 1;
                }
            }
        }
        catch (Exception ex) {
            LogUtil.error("Fabrication Production Planning", ex, "Work order Loading");
        }
        finally {
            try {
                if (con != null)
                con.close();
            }
            catch (SQLException e) {
            }
        }
    }

/**
 * This method is used to check the null values
 *
 * @param value
 * @return
 */
    public String checkNull(String value) {
        if (value == null) {
            value = "0";
        }
        return value;
    }

/**
 * This method is used to get the gross details for work order
 * Notes : For Category type Accessories,FRP_grating,FRP_Stairtreads Need to implement the logic
 *
 * @param woNo   - Work order number
 * @param partNo - part number
 */
    public void getGrossDetails(String woNo, String partNo, Connection con1) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
            String[] grossDetails = {"grossWeight", "shaped_profile", "plain_width", "stairtread", "Structure",
            "presslock_Grating", "Accessories", "FRP_grating", "FRP_Stairtreads", "production_balance", "plasma", "std_startDate", "std_endDate"};
            //con1 = ds.getConnection();

            if (!con1.isClosed()) {
                boolean isBomDataAvailable = false;

                for (int i = 0; i < grossDetails.length; i++) {
                    String grossType = grossDetails[i];
                    String query = "";
                    switch (grossType) {

                        case "std_startDate":
                            query = "select TOP 1 dateCreated from app_fd_ods_wo_group_plan where c_work_orderno=? and c_part_no=? and c_workOrderType='Fabrication Panel'";
                            pstmt = con1.prepareStatement(query);
                            pstmt.setString(1, String.valueOf(woNo));
                            pstmt.setString(2, String.valueOf(partNo));
                            rs = pstmt.executeQuery();
                            while (rs.next()) {
                                stdStartDate = checkNull(rs.getString("dateCreated"));
                                //LogUtil.info("stdStartDate inside", stdStartDate);
                                isBomDataAvailable = true;
                            }
                            break;
                        case "std_endDate":
                            query = "select TOP 1 dateCreated from app_fd_ods_productexecute where c_work_orderno=? and c_part_no=? and c_fabType='Fabrication Panel'";
                            pstmt = con1.prepareStatement(query);
                            pstmt.setString(1, String.valueOf(woNo));
                            pstmt.setString(2, String.valueOf(partNo));
                            rs = pstmt.executeQuery();
                            while (rs.next()) {
                            stdEndDate = checkNull(rs.getString("dateCreated"));
                            }
                            break;
                        case "grossWeight":
                            //LogUtil.info("Gross Weight Loading ", "");
                            query = "select sum(cast(c_gross_weight as float)) as 'c_gross_weight' ,sum(cast(c_gross_area as float)) as 'c_gross_area', " +
                            "sum(cast(c_balanceQTY as float)) as 'c_balanceQTY',max(c_cuttingType) as 'c_cuttingType' from app_fd_ods_bom_master where c_work_orderno=? and c_part_no=? and c_cuttingType is not null and cast(c_created_at as date)>=DATEADD(DAY, -1 ,cast(GETDATE() as date)) ";
                            pstmt = con1.prepareStatement(query);
                            pstmt.setString(1, String.valueOf(woNo));
                            pstmt.setString(2, String.valueOf(partNo));
                            rs = pstmt.executeQuery();

                            while (rs.next()) {
                            isBomDataAvailable = true;
                            String quantity = checkNull(rs.getString("c_balanceQTY"));
                            String type = checkNull(rs.getString("c_cuttingType"));
                            if ("Saw Cutting".equals(type)) {
                            sawCuttingBalance = quantity;
                            } else if ("Preparation".equals(type)) {
                            preparationBalance = quantity;
                            } else {
                            sawCuttingBalance = "0";
                            preparationBalance = "0";
                            }

                            double gsWeightNum = Double.parseDouble(checkNull(rs.getString("c_gross_weight")));
                            double gsAreaNum = Double.parseDouble(checkNull(rs.getString("c_gross_area")));
                            gsWeightNum = Math.round(gsWeightNum * 100.0) / 100.0;
                            gsAreaNum = Math.round(gsAreaNum * 100.0) / 100.0;
                            grossWeight = String.valueOf(gsWeightNum);
                            grossArea = String.valueOf(gsAreaNum);
                            }
                            break;
                        case "shaped_profile":
                            shapeBalance = getProductionBalance("Shaped", "profile", woNo, partNo,con1);
                            break;
                        case "plain_width":
                            plainBalance = getProductionBalance("Plain", "Width cutting", woNo, partNo,con1);
                            break;
                        case "stairtread":
                            stairtreadBalance = getProductionBalance("Stairtread", "empty", woNo, partNo,con1);
                            break;
                        case "Structure":
                            structureBalance = getProductionBalance("STR", "empty", woNo, partNo,con1);
                            break;
                        case "GRATING":
                            structureBalance = getProductionBalance("GRATING", "empty", woNo, partNo,con1);
                            break;
                        case "plasma":
                            plasma = getProductionBalance("profile", "empty", woNo, partNo,con1);
                            break;
                        case "production_balance":
                            query = "select sum(cast(c_qty as float)) as 'c_qty' from app_fd_ods_bomappdetails where c_work_orderno=? and c_part_no=? and c_vnd_status='New' and cast(c_created_at as date)>=DATEADD(DAY, -2 ,cast(GETDATE() as date))";
                            pstmt = con1.prepareStatement(query);
                            pstmt.setString(1, String.valueOf(woNo));
                            pstmt.setString(2, String.valueOf(partNo));
                            rs = pstmt.executeQuery();
                            while (rs.next()) {
                            productionBalance = checkNull(rs.getString("c_qty"));
                            }
                            break;
                        default:
                            //LogUtil.info("Not Found", "");
                            break;
                    }

                    if (!isBomDataAvailable) {
                        break;
                    }
                }
            }
        }
        catch (Exception ex) {
            LogUtil.error("Fabrication Production Planning", ex, "Gross details loading");
        }
        finally {
            try {
                pstmt.close();
                rs.close();
            }
            catch (SQLException e) {
            }
        }
    }

/**
 * This method is used to get the prodcution balance for work order based on category wise
 *
 * @param category1
 * @param category2
 * @param workOrderNo
 * @param partNo
 * @return balance production balance
 */
    public String getProductionBalance(String category1, String category2, String workOrderNo, String partNo, Connection con1) {
        //Connection con1 = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String productionBalance = "";
        //DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
        //con1 = ds.getConnection();
            if (!con1.isClosed()) {
                String query = "select sum(cast(c_bom_quantity as float)) as 'c_qty' from app_fd_ods_bom_master " +
                "where (c_work_orderno = ? and c_part_no = ?) " +
                "and (c_production_category =? or c_production_category = ? ) and ( len(c_productionPlanningStatus) = 0 or c_productionPlanningStatus is null) ";
                pstmt = con1.prepareStatement(query);
                pstmt.setString(1, String.valueOf(workOrderNo));
                pstmt.setString(2, String.valueOf(partNo));
                pstmt.setString(3, String.valueOf(category1));
                pstmt.setString(4, String.valueOf(category2));
                rs = pstmt.executeQuery();
                //LogUtil.info("Production Balance loading query ", query);
                while (rs.next()) {
                    productionBalance = checkNull(rs.getString("c_qty"));
                }
            }
        }
        catch (Exception ex) {
            LogUtil.error("Production Creation ", ex, " Production Balance Loading ");
        }
        finally {

            try {
            pstmt.close();
            rs.close();
                /*if (con1 != null) {
                    //con1.close();
                }*/
            }
            catch (SQLException e) {
            }
        }
        return productionBalance;
    }
}

return load(element, primaryKey, formData);