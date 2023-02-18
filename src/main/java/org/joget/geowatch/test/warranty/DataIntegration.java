package org.joget.geowatch.test.warranty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import java.sql.DriverManager;
import java.time.LocalDate;

import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;

import javax.servlet.http.HttpServletRequest;

public class DataIntegration {
    static Connection con = null;
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request)  throws SQLException, ClassNotFoundException {
        LogUtil.info("DataIntegration Process Start","");
        LocalDate today = LocalDate.now();
        LocalDate previousDay = today.minusDays(1);
        //LocalDate today = LocalDate.now().plusDays(1);
        //LocalDate previousDay = LocalDate.now();
        //LocalDate today = LocalDate.now();
        //LocalDate previousDay = LocalDate.of(2022,11,22);

        try {
            // retrieve connection from the Custom datasource
            //DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");

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
                //Populate Item Master Form
                String itemMasterQuery = "select * from apps.XXOIC_JOGETIN_ITEMMASTR_CAT_V where "+
                        "LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement itemstmt = con.prepareStatement(itemMasterQuery);
                itemstmt.setString(1,previousDay.toString());
                itemstmt.setString(2,today.toString());
                //itemstmt.setDate(LocalDate.);
                ResultSet itemrs = itemstmt.executeQuery();
                while (itemrs.next()) {
                    FormRowSet rowsItemMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                    //Set values
                    String itemMasterOrgId = (itemrs.getString("ORGANIZATION_ID") != null) ? itemrs.getString("ORGANIZATION_ID").toString() : "";
                    String itemMasterInvId = (itemrs.getString("INVENTORY_ITEM_ID") != null) ? itemrs.getString("INVENTORY_ITEM_ID").toString() : "";
                    String itemMasterId = itemMasterOrgId + "_" + itemMasterInvId;
                    //Set ID - Primary Keys
                    row.setProperty("id", itemMasterId);

                    row.setProperty("organization_id", itemMasterOrgId);
                    row.setProperty("inventory_item_id", itemMasterInvId);
                    String strItemcode = (itemrs.getString("ITEM_CODE") != null) ? itemrs.getString("ITEM_CODE").toString() : "";
                    row.setProperty("item_code", strItemcode);
                    //row.setProperty("field1", "value 1");
                    String strDescription = (itemrs.getString("DESCRIPTION") != null) ? itemrs.getString("DESCRIPTION").toString() : "";
                    row.setProperty("description", strDescription);

                    String strUOM = (itemrs.getString("PRIMARY_UNIT_OF_MEASURE") != null) ? itemrs.getString("PRIMARY_UNIT_OF_MEASURE").toString() : "";
                    row.setProperty("uom", strUOM);

                    String strItemCategory = (itemrs.getString("ITEM_CATEGORY") != null) ? itemrs.getString("ITEM_CATEGORY").toString() : "";
                    row.setProperty("item_category", strItemCategory);

                    String strRegion = (itemrs.getString("REGION") != null) ? itemrs.getString("REGION").toString() : "";
                    row.setProperty("region", strRegion);


                    String formIdItem = "item_master";
                    String tableNameItem = "items_master";
                    String fieldIDItem = "item_code";

                    //Populate the form
                    //FormRow row = formDataDao.load(formIdItem, tableName, id);
                    rowsItemMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdItem, tableNameItem, rowsItemMasterData);
                }

                //Populate DO Master Form
                String DOMasterQuery = "select * from apps.XXOIC_JOGETIN_DO_SHIPMENT_V " +
                        "where LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement dostmt = con.prepareStatement(DOMasterQuery);
                dostmt.setString(1, previousDay.toString());
                dostmt.setString(2, today.toString());
                ResultSet dors = dostmt.executeQuery();
                while (dors.next()) {

                    FormRowSet rowsDOMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    //String uuid = UuidGenerator.getInstance().getUuid();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                    //Set values - Id using Primary Key
                    String strRefId = (dors.getString("REFERENCE_ID") != null) ? dors.getString("REFERENCE_ID").toString() : "";
                    row.setProperty("id", strRefId);
                    row.setProperty("reference_id", strRefId);

                    String strDONumber = (dors.getString("DO_NUMBER") != null) ? dors.getString("DO_NUMBER").toString() : "";
                    //System.out.println(strDONumber);
                    row.setProperty("do_Number", strDONumber);

                    String strDeliveryDate = (dors.getString("DELIVERY_DATE") != null) ? dors.getString("DELIVERY_DATE").toString() : "";
                    row.setProperty("delivery_date", strDeliveryDate);

                    String strSONumber = (dors.getString("SO_NUMEBR") != null) ? dors.getString("SO_NUMEBR").toString() : "";
                    row.setProperty("so_number", strSONumber);

                    String strLineNumber = (dors.getString("LINE_NUMBER") != null) ? dors.getString("LINE_NUMBER").toString() : "";
                    row.setProperty("line_number", strLineNumber);

                    String strItemcode = (dors.getString("ITEM_CODE") != null) ? dors.getString("ITEM_CODE").toString() : "";
                    row.setProperty("item_code", strItemcode);

                    String strItemDescription = (dors.getString("ITEM_DESCRIPTION") != null) ? dors.getString("ITEM_DESCRIPTION").toString() : "";
                    row.setProperty("item_desc", strItemDescription);

                    String strUOM = (dors.getString("UOM") != null) ? dors.getString("UOM").toString() : "";
                    row.setProperty("uom", strUOM);

                    String strShippedQty = (dors.getString("SHIPPED_QTY") != null) ? dors.getString("SHIPPED_QTY").toString() : "";
                    row.setProperty("shipped_qty", strShippedQty);

                    String strInvoiceNumber = (dors.getString("INVOICE_NUMBER") != null) ? dors.getString("INVOICE_NUMBER").toString() : "";
                    row.setProperty("invoiceNumber", strInvoiceNumber);

                    String strInvoiceDate = (dors.getString("INVOICE_DATE") != null) ? dors.getString("INVOICE_DATE").toString() : "";
                    row.setProperty("invoiceDate", strInvoiceDate);


                    String formIdDO = "do_master_form";
                    String tableNameDO = "do_master";
                    String fieldIDDO = "do_Number";

                    //Populate the form
                    //FormRow row = formDataDao.load(formId, tableName, id);
                    rowsDOMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdDO, tableNameDO, rowsDOMasterData);
                }

                //Populate Equipment Master Form
                String equipmtMasterQuery = "select * from apps.XXOIC_JOGETIN_DO_EQUIPMENT_V where DO_CAPTURE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND DO_CAPTURE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement equipmtstmt = con.prepareStatement(equipmtMasterQuery);
                equipmtstmt.setString(1,previousDay.toString());
                equipmtstmt.setString(2,today.toString());
                ResultSet equipmtrs = equipmtstmt.executeQuery();
                while (equipmtrs.next()) {
                    FormRowSet rowsEquipmtMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    //String uuid = UuidGenerator.getInstance().getUuid();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                    //Set values
                    String strEqDONumber = (equipmtrs.getString("DO_NUMBER") != null) ? equipmtrs.getString("DO_NUMBER").toString() : "";
                    String strModelNumber = (equipmtrs.getString("MODEL_NUMBER") != null) ? equipmtrs.getString("MODEL_NUMBER").toString() : "";

                    String strEquipmtNumber = (equipmtrs.getString("EQUIPMENT_NUMBER") != null) ? equipmtrs.getString("EQUIPMENT_NUMBER").toString() : "";
                    row.setProperty("equipment_Number", strEquipmtNumber);

                    //Set Id - using Primary Key
                    String eqpMasterId = strEqDONumber + "_" + strEquipmtNumber;
                    row.setProperty("id", eqpMasterId);

                    row.setProperty("do_Number", strEqDONumber);


                    row.setProperty("model_Number", strModelNumber);


                    String strDOCaptureDate = (equipmtrs.getString("DO_CAPTURE_DATE") != null) ? equipmtrs.getString("DO_CAPTURE_DATE").toString() : "";
                    row.setProperty("do_capture_date", strDOCaptureDate);

                    String formIdEquipmt = "equipment_master";
                    String tableNameEquipmt = "equipment_master";
                    String fieldIDEquipmt = "do_Number";

                    //Populate the form
                    //FormRow row = formDataDao.load(formId, tableName, id);
                    rowsEquipmtMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdEquipmt, tableNameEquipmt, rowsEquipmtMasterData);
                }

                //Populate Stock Master Form
                String stockMasterQuery =  "Select * from apps.XXOIC_JOGETIN_ITEM_COSTSTOCK_V  where LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement stockstmt = con.prepareStatement(stockMasterQuery);
                stockstmt.setString(1,previousDay.toString());
                stockstmt.setString(2,today.toString());
                ResultSet stockrs = stockstmt.executeQuery();
                while (stockrs.next()) {
                    FormRowSet rowsStockMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");

                    //Set values - Id using Primary Key
                    row.setProperty("id", (stockrs.getString("INVENTORY_ITEM_ID") != null) ? stockrs.getString("INVENTORY_ITEM_ID").toString() : "");

                    String strStockItemCode = (stockrs.getString("ITEM_CODE") != null) ? stockrs.getString("ITEM_CODE").toString() : "";
                    row.setProperty("item_code", strStockItemCode);

                    String strAvailableStock = (stockrs.getString("ONHAND_STOCK") != null) ? stockrs.getString("ONHAND_STOCK").toString() : "";
                    row.setProperty("available_stock", strAvailableStock);

                    String strAvgCost = (stockrs.getString("AVERAGE_COST") != null) ? stockrs.getString("AVERAGE_COST").toString() : "";
                    row.setProperty("cost", strAvgCost);

                    String formIdStock = "stock_master";
                    String tableNameStock = "stock_master";
                    String fieldIDStock = "item_code";

                    //Populate the form
                    //FormRow row = formDataDao.load(formId, tableName, id);
                    rowsStockMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdStock, tableNameStock, rowsStockMasterData);
                }


                //Populate Project Master Form
                String ProjectMasterQuery = "select distinct * from apps.XXOIC_JOGETIN_SO_DETAILS_V where "+
                        "LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement projectstmt = con.prepareStatement(ProjectMasterQuery);
                projectstmt.setString(1,previousDay.toString());
                projectstmt.setString(2,today.toString());
                ResultSet projectrs = projectstmt.executeQuery();
                while (projectrs.next()) {
                    FormRowSet rowsProjectMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
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


                    String formIdProject = "projectMaster";
                    String tableNameProject = "project_master";
                    String fieldIDProject = "SO_Number";

                    //Populate the form
                    rowsProjectMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdProject, tableNameProject, rowsProjectMasterData);
                }
            }
        }
        catch(Exception e) {
            LogUtil.error("DataIntegration",e, e.getMessage());
        }
        finally {
            try {
                if(con != null) {
                    con.close();
                }
            }
            catch(SQLException e) {}
        }
        LogUtil.info("DataIntegration Process End","");
        return null;
    }
}
return DataIntegration.execute(workflowAssignment, appDef, request);
