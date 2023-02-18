package org.joget.geowatch.test.warranty;
import java.text.*;
import java.util.*;
import java.lang.*;

import java.util.Formatter;
import org.apache.commons.lang.StringUtils;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.*;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.joget.commons.util.UuidGenerator;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormStoreBinder;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.joget.commons.util.LogUtil;
import java.lang.Object;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.time.*;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;
import javax.servlet.http.HttpServletRequest;

public class LoadWarrantyNew {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        System.out.println("hello");
        String orderType = "#variable.ordertype#";
        System.out.println(orderType);
        String warrantynumber = "W-";
        int revision = 0;
        String projectNum = "";
        String client = "";
        String contractor = "";
        String consultant = "";
        String projectName = "";
        String location = "";
        String emirates = "";
        String landmark = "";
        String strbrand = "Rheem";
        Map salesorders = new HashMap();
        Map doNumbers = new HashMap();
        Map invoices = new HashMap();
        SimpleDateFormat ft = new SimpleDateFormat ("dd MMMM yyyy");
        Date warrantyDate = new Date();
        //#process.recordId#
        //UuidGenerator.getInstance().getUuid();
        String id = "#process.recordId#";
        StringBuilder strbSOQueryParam = new StringBuilder();
        StringBuilder strbDOQueryParam = new StringBuilder();
        Boolean bChangeFlag = false;
        String strOrgWarStatus = "";
        String strOrgRevStatus = "";
        LogUtil.info("$$$$$$------$$$$$",orderType);

        if(orderType.equals("Project")){
            LogUtil.info("--- RecordId", " ------- \n"+id);
            String sprojectNumber = "#variable.projectnumber#";
            String sprojectEndDate = "#variable.projectenddate#";
            LogUtil.info("--- ProjectNumber", " ------- \n"+sprojectNumber);
            LogUtil.info("--- ProjectEnd Date", " ------- \n"+sprojectEndDate);
            String sPrjQuery = "select * from app_fd_projects where c_projectNumber in ('"+sprojectNumber+"')";

            String sSOQuery = "select p.c_projectNumber, s.c_SO_Number, d.c_do_Number, d.c_invoiceNumber,p.c_warranty_start_date from app_fd_projects p join app_fd_so_number s on p.id = s.c_projectNum join app_fd_do_master d on s.c_SO_Number = d.c_so_number where p.c_projectNumber in ('"+sprojectNumber+"')";
            LogUtil.info("--- Query for sales orders", " ------- \n"+sSOQuery);
            Connection connection = null;
            Statement statement = null;
            LogUtil.info("--- Outside try block", " ------- \n");
            try {
                DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
                connection = ds.getConnection();
                Statement statementSo = null;
                // execute SQL query

                if(!connection.isClosed()) {
                    statementSo = connection.createStatement();
                    ResultSet rsSO = statementSo.executeQuery(sSOQuery);
                    LogUtil.info("--- Query Executed", " ------- \n");
                    LogUtil.info("--- Outside resultset of sales orders", " ------- \n"+rsSO.toString());
                    while (rsSO.next()) {
                        //LogUtil.info("--- Inside resultset of sales orders", " ------- \n"+rsSO.toString());
                        String projNumber = rsSO.getString("c_projectNumber");
                        String salesOrder = rsSO.getString("c_SO_Number");
                        String doNumber = rsSO.getString("c_do_Number");
                        String invoiceNumber = rsSO.getString("c_invoiceNumber");

                        if(salesorders.containsKey(projNumber))
                        {
                            ArrayList SOs = (ArrayList) salesorders.get(projNumber);
                            if(!SOs.contains(salesOrder)){
                                SOs.add(salesOrder);
                                strbSOQueryParam.append("'"+salesOrder +"',");
                                salesorders.put(projNumber, SOs);
                            }
                        }
                        else
                        {
                            ArrayList SOs = new ArrayList();
                            SOs.add(salesOrder);
                            strbSOQueryParam.append("'"+salesOrder +"',");
                            System.out.println(salesOrder);
                            salesorders.put(projNumber, SOs);
                        }

                        if(doNumbers.containsKey(projNumber))
                        {
                            ArrayList DOs = (ArrayList) doNumbers.get(projNumber);
                            if(!DOs.contains(doNumber)){
                                DOs.add(doNumber);
                                doNumbers.put(projNumber, DOs);
                            }
                        }
                        else
                        {
                            ArrayList DOs = new ArrayList();
                            DOs.add(doNumber);
                            System.out.println(doNumber);
                            doNumbers.put(projNumber, DOs);
                        }

                        if(invoices.containsKey(projNumber))
                        {
                            ArrayList INs = (ArrayList) invoices.get(projNumber);
                            if(!INs.contains(invoiceNumber)){
                                INs.add(invoiceNumber);
                                invoices.put(projNumber, INs);
                            }
                        }
                        else
                        {
                            ArrayList INs = new ArrayList();
                            INs.add(invoiceNumber);
                            System.out.println(invoiceNumber);
                            invoices.put(projNumber, INs);
                        }
                    }



                    LogUtil.info("--- Before Executing Query", " ------- \n");
                    statement = connection.createStatement();
                    ResultSet warrantyrs = statement.executeQuery(sPrjQuery);
                    while (warrantyrs.next()) {

                        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                        String key = sprojectNumber;
                        FormRowSet rows = new FormRowSet();
                        FormRow newRow = new FormRow();
                        FormRowSet rowsWarrantyReviewData = formDataDao.find("warranty_review_form", "review_form", "where c_pno =? and c_rev_status=?", new Object[] {key,"Active"}, null, null, null, null);
                        rowsWarrantyReviewData.setMultiRow(true);
                        LogUtil.info("--- Before If Else Loop", " ------- \n" + rowsWarrantyReviewData);
                        //there is a warranty record for the project number retrieve it and display
                        //if there are more than one warranty record, get the latest revision
                        if(rowsWarrantyReviewData != null && !rowsWarrantyReviewData.isEmpty()){
                            LogUtil.info("--- If loop ", " ------- \n"+ id);
                            for(FormRow	row:rowsWarrantyReviewData)
                            {
                                LogUtil.info("Inside For Loop","");
                                newRow.putAll(row);
                                //Capture Other fields and increment the revision if there is a change in if there is a change in
                                //invoice section, DO section, SO sections, quantity change, Item change or warranty dates
                                StringBuilder strbSalesorders = new StringBuilder();
                                List orders = (List) salesorders.get(sprojectNumber);
                                if(orders != null && orders.size() >0){
                                    for (Object strSO : orders) {
                                        strbSalesorders.append(strSO + ", ");
                                    }
                                }
                                String universalSoNum = "";
                                String strSalesorders = strbSalesorders.toString();
                                strSalesorders = strSalesorders.substring(0, strSalesorders.length()-2);
                                String strOriginalSOs = (row.get("sales_orders")!= null)?row.get("sales_orders").toString():"";
                                //LogUtil.info("--- Old Sales Orders ", " ------- \n"+ strOriginalSOs);
                                //LogUtil.info("--- New Sales Orders ", " ------- \n"+ strSalesorders);
                                if(!strSalesorders.equals(strOriginalSOs)){
                                    bChangeFlag = true;
                                    newRow.put("sales_orders", strSalesorders);
                                    universalSoNum = strSalesorders;
                                }
                                else {
                                    row.put("sales_orders", strOriginalSOs);
                                    universalSoNum = strOriginalSOs;
                                }


                                StringBuilder strbDeliveryorders = new StringBuilder();
                                List dorders = (List) doNumbers.get(sprojectNumber);
                                if(dorders != null && dorders.size() > 0){
                                    for (Object strDO : dorders) {
                                        strbDeliveryorders.append(strDO + ", ");
                                    }
                                }
                                String strDeliveryorders = strbDeliveryorders.toString();
                                strDeliveryorders = strDeliveryorders.substring(0, strDeliveryorders.length()-2);
                                String strOriginalDOs = (row.get("do_no")!= null)?row.get("do_no").toString():"";
                                //LogUtil.info("--- Old Delivery Orders ", " ------- \n"+ strOriginalDOs);
                                //LogUtil.info("--- New Delivery Orders ", " ------- \n"+ strDeliveryorders);
                                if(!strDeliveryorders.equals(strOriginalDOs)){
                                    bChangeFlag = true;
                                    newRow.put("do_no", strDeliveryorders);
                                }
                                else {
                                    row.put("do_no", strOriginalDOs);
                                }

                                StringBuilder strBInvoices = new StringBuilder();
                                List invs = (List) invoices.get(sprojectNumber);
                                if(invs != null && invs.size() > 0){
                                    for (Object strIn : invs) {
                                        strBInvoices.append(strIn + ", ");
                                    }
                                }
                                String strInvoices = strBInvoices.toString();
                                strInvoices = strInvoices.substring(0, strInvoices.length()-2);
                                String strOriginalInvs = (row.get("invoices")!= null)?row.get("invoices").toString():"";
                                //LogUtil.info("--- Old Invoices ", " ------- \n"+ strInvoices);
                                //LogUtil.info("--- New Invoices ", " ------- \n"+ strOriginalInvs);
                                if(!strInvoices.equals(strOriginalInvs)){
                                    bChangeFlag = true;
                                    newRow.put("invoices", strInvoices);
                                }
                                else {
                                    row.put("invoices", strOriginalInvs);
                                }


                                //revision, increment it if there is change in the fields
                                revision = (row.get("rev")!= null)?Integer.valueOf(row.get("rev").toString()):0;
                                String processType = "#variable.processType#";
                                LogUtil.info("Process Type",processType);
                                String oldId = (String) row.get("id");
                                if(bChangeFlag){
                                    // if new warranty record is generated then set that new id in process variable.]
                                    PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
                                    WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
                                    wm.activityVariable(assignment.getActivityId(),"rId", "#process.recordId#");
                                    LogUtil.info("--- Change Flag", " ------- \n");

                                    //increment the revision only if the warranty status was in authorized status and move it to draft
                                    strOrgWarStatus = (String) row.get("warranty_status");
                                    if(strOrgWarStatus.equals("Authorized")){
                                        revision += 1;
                                    }


                                    newRow.setId(id);
                                    newRow.put("rev_status", "Active");
                                    newRow.put("oldId", row.get("id"));
                                    newRow.put("newid", id);
                                    newRow.put("rev",String.valueOf(revision));
                                    newRow.put("warranty_status", "Draft");
                                    LogUtil.info("--- New Row ", " ------- \n"+ newRow);


                                    row.put("rev_status", "Inactive");
                                    LogUtil.info("--- Old Row ", " ------- \n"+ row);
                                    LogUtil.info("--- Old Id for Line Item details ", " ------- \n"+oldId);

                                    // get new warranty details
                                    // get so numbers.
                                    LogUtil.info("Universal SO Num",universalSoNum);
                                    String[] universalSoArray = universalSoNum.split(",");
                                    LogUtil.info("Universal Array",""+universalSoArray);
                                    StringBuilder universalSoBuilder = new StringBuilder();
                                    for(String str : universalSoArray){
                                        universalSoBuilder.append("'"+str.trim()).append("',");
                                    }
                                    universalSoBuilder.deleteCharAt(universalSoBuilder.length()-1);
                                    universalSoNum = universalSoBuilder.toString();

                                    FormRowSet f = new FormRowSet();
                                    f.setMultiRow(true);
                                    String strSOQueryParam= strbSOQueryParam.toString();
                                    String sLineItemsQuery = "SELECT distinct w.id, w.c_warrantyItem,w.c_equipment, w.c_warranty, w.c_warranty_status FROM app_fd_warranty w WHERE w.c_equipment IN (SELECT i.c_item_category FROM app_fd_do_master d join app_fd_items_master i ON d.c_item_code = i.c_item_code WHERE d.c_so_number in ("+universalSoNum+"))";
                                    LogUtil.info("---Warranty Query ---", " ------- \n"+sLineItemsQuery);

                                    String startDate = warrantyrs.getString("c_warranty_start_date")!= null?warrantyrs.getString("c_warranty_start_date").toString():"";
                                    //(warrantyrs.getString("c_warranty_start_date")!= null)?warrantyrs.getString("c_projectStartDate").toString():"";
                                    LogUtil.info("---Start Date: ---", " ------- \n"+startDate);
                                    //calculate end date using warranty
                                    Date dstrDate = new SimpleDateFormat("yyyy-mm-dd").parse(startDate);
                                    //LocalDate.parse(startDate);//
                                    //LocalDate date = LocalDate.parse("2020-05-03");
                                    // Displaying date

                                    Statement statementLineItem = null;
                                    LogUtil.info("---Before Executing Warranty Query: ---", " ------- \n");
                                    statementLineItem = connection.createStatement();
                                    ResultSet rsLineItem = statementLineItem.executeQuery(sLineItemsQuery);
                                    while (rsLineItem.next()) {
                                        LogUtil.info("---After Executing Warranty Query: ---", " ------- \n"+rsLineItem.getString(1));
                                        FormRow r1 = new FormRow();
                                        String newitemid = UuidGenerator.getInstance().getUuid();
                                        r1.put("id", newitemid);
                                        r1.put("warrantyItem", rsLineItem.getString(2));
                                        r1.put("warranty_ref",id);
                                        r1.put("equipment", rsLineItem.getString(3));
                                        r1.put("startDate", startDate);
                                        r1.put("warranty",rsLineItem.getString(4));
                                        int months = Integer.valueOf(rsLineItem.getString(4));
                                        LogUtil.info("---ResultSet: ---", " ------- \n"+rsLineItem.getString(2)+rsLineItem.getString(3));
                                        // Convert Date to Calendar
                                        Calendar c = Calendar.getInstance();
                                        c.setTime(dstrDate);
                                        c.add(Calendar.MONTH, months);
                                        Date newDate = c.getTime();
                                        LogUtil.info("---New Date: ---", " ------- \n"+newDate.toString());
                                        String endDate = new SimpleDateFormat("yyyy-mm-dd").format(newDate);
                                        LogUtil.info("---End Date: ---", " ------- \n"+endDate);
                                        r1.put("endDate", endDate);
                                        r1.put("warranty_status", rsLineItem.getString(5));
                                        f.add(r1);
                                    }
                                    //Populate the form
                                    formDataDao.saveOrUpdate("projectItemsWarranty", "proj_items_warranty", f);


 /*FormRowSet rowsLineItemsData = formDataDao.find("projectItemsWarranty", "proj_items_warranty", "where c_warranty_ref =?", new Object[] {oldId}, null, null, null, null);
 rowsLineItemsData.setMultiRow(true);
 LogUtil.info("--- Before Line Item Details", " ------- \n" + rowsLineItemsData);
 FormRow newItemRow = new FormRow();
 if(rowsLineItemsData != null && !rowsLineItemsData.isEmpty()){

 LogUtil.info("--- Inside Line Item details ", " ------- \n"+oldId);
 //System.out.println(rowsWarrantyReviewData);
 for(FormRow	itemrow:rowsLineItemsData)
 {
 String newItemid = UuidGenerator.getInstance().getUuid();
 newItemRow.putAll(itemrow);
 newItemRow.setId(newItemid);
 newItemRow.put("warranty_ref", id);
 }
 rowsLineItemsData.add(newItemRow);
 LogUtil.info("--- After Line Item Details", " ------- \n" + rowsLineItemsData);
 //Populate the form
 formDataDao.saveOrUpdate("projectItemsWarranty", "proj_items_warranty", rowsLineItemsData);
 }*/

                                }
                                else if(bChangeFlag == false && processType != null && processType.equals("Reload")){
                                    String startDate = warrantyrs.getString("c_warranty_start_date")!= null?warrantyrs.getString("c_warranty_start_date").toString():"";
                                    String[] universalSoArray = strOriginalSOs.split(",");
                                    LogUtil.info("Original array",""+universalSoArray);
                                    StringBuilder universalSoBuilder = new StringBuilder();
                                    for(String str : universalSoArray){
                                        universalSoBuilder.append("'"+str.trim()).append("',");
                                    }
                                    universalSoBuilder.deleteCharAt(universalSoBuilder.length()-1);
                                    String soQueryParam = universalSoBuilder.toString();
                                    LoadWarrantyNew.newItemUploadForProject(soQueryParam,startDate,connection,oldId,formDataDao);
                                }

                            }

                            String formId = "warranty_review_form";
                            String tableName = "review_form";
                            String fieldIDProject ="id";
                            //Populate the form
                            //rows.add(row);
                            if(bChangeFlag){
                                rowsWarrantyReviewData.add(newRow);
                            }
                            formDataDao.saveOrUpdate(formId, tableName, rowsWarrantyReviewData);
                        }
                        //there is no warranty generated, generate a new one and save it in db and then load the form
                        else{
                            //Set values
                            LogUtil.info("--- Else Loop", " ------- \n"+id);
                            System.out.println("Else Loop");
                            FormRow row = new FormRow();
                            row.setProperty("id",id);

                            projectNum = (warrantyrs.getString("c_projectNumber")!= null)?warrantyrs.getString("c_projectNumber").toString():"";
                            row.setProperty("pno",projectNum);

                            projectName = (warrantyrs.getString("c_projectName")!= null)?warrantyrs.getString("c_projectName").toString():"";
                            row.setProperty("project",projectName);

                            contractor = (warrantyrs.getString("c_mainContractor")!= null)?warrantyrs.getString("c_mainContractor").toString():"";
                            row.setProperty("main_contractor",contractor);

                            consultant = (warrantyrs.getString("c_consultant")!= null)?warrantyrs.getString("c_consultant").toString():"";
                            row.setProperty("con",consultant);

                            row.setProperty("brand",strbrand);

                            location = (warrantyrs.getString("c_location")!= null)?warrantyrs.getString("c_location").toString():"";
                            row.setProperty("loc",location);

                            emirates = (warrantyrs.getString("c_emirates")!= null)?warrantyrs.getString("c_emirates").toString():"";
                            row.setProperty("emir",emirates);

                            landmark = (warrantyrs.getString("c_nearestLandmark")!= null)?warrantyrs.getString("c_nearestLandmark").toString():"";
                            row.setProperty("nearest_landmark",landmark);

                            client = (warrantyrs.getString("c_custName")!= null)?warrantyrs.getString("c_custName").toString():"";
                            row.setProperty("client",client);
                            LogUtil.info("--- after setting properties", " ------- \n");

                            StringBuilder strbSalesorders = new StringBuilder();
                            List orders = (List) salesorders.get(sprojectNumber);
                            if(orders != null && orders.size() >0){
                                for (Object strSO : orders) {
                                    strbSalesorders.append(strSO + ", ");
                                }

                                String strSalesorders = strbSalesorders.toString();
                                LogUtil.info("--- salesorders", " ------- \n"+strSalesorders);
                                row.setProperty("sales_orders", strSalesorders.substring(0, strSalesorders.length()-2));
                            }

                            StringBuilder strbDeliveryorders = new StringBuilder();
                            List dorders = (List) doNumbers.get(sprojectNumber);
                            if(dorders != null && dorders.size() > 0){
                                for (Object strDO : dorders) {
                                    strbDeliveryorders.append(strDO + ", ");
                                }
                                String strDeliveryorders = strbDeliveryorders.toString();
                                row.setProperty("do_no", strDeliveryorders.substring(0, strDeliveryorders.length()-2));
                            }


                            StringBuilder strBInvoices = new StringBuilder();
                            List invs = (List) invoices.get(sprojectNumber);
                            if(invs != null && invs.size() > 0){
                                for (Object strIn : invs) {
                                    strBInvoices.append(strIn + ", ");
                                }
                                String strInvoices = strBInvoices.toString();
                                row.setProperty("invoices", strInvoices.substring(0, strInvoices.length()-2));
                            }


                            //warranty - assuming this will be a unique number for a project
                            appDef = AppUtil.getCurrentAppDefinition();
                            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");


                            //Create the warranty Number and set it to appropriate column
                            int modifiedWarrantyNumber = environmentVariableDao.getIncreasedCounter("warrantynumber", "Project"+ sprojectNumber, appDef);
                            String strWN = String.format("%03d", new Object[]{modifiedWarrantyNumber});
                            //new Object[]{new Integer(modifiedWarrantyNumber)});
                            System.out.println("warranty number -" + strWN);
                            warrantynumber = warrantynumber + strWN;
                            row.setProperty("wo_no",warrantynumber);

                            //warranty date
                            row.setProperty("warr_date",ft.format(warrantyDate));

                            //revision =0, as there warranty is generated for the first time for this project
                            revision = 0;
                            //environmentVariableDao.getIncreasedCounter("rev-"+ sprojectNumber, "new", appDef);
                            row.setProperty("rev",String.valueOf(revision));

                            row.setProperty("rev_status", "Active");

                            row.setProperty("warranty_status", "Draft");



                            String formId = "warranty_review_form";
                            String tableName = "review_form";

                            //Populate the form
                            rowsWarrantyReviewData.add(row);
                            formDataDao.saveOrUpdate(formId, tableName, rowsWarrantyReviewData);
                            LogUtil.info("---Before Warranty Query ---", " ------- \n");
                            FormRowSet f = new FormRowSet();
                            f.setMultiRow(true);
                            String strSOQueryParam= strbSOQueryParam.toString();
                            String sLineItemsQuery = "SELECT distinct w.id, w.c_warrantyItem,w.c_equipment, w.c_warranty, w.c_warranty_status FROM app_fd_warranty w WHERE w.c_equipment IN (SELECT i.c_item_category FROM app_fd_do_master d join app_fd_items_master i ON d.c_item_code = i.c_item_code WHERE d.c_so_number in ("+strSOQueryParam.substring(0, strSOQueryParam.length()-1)+"))";
                            LogUtil.info("---Warranty Query ---", " ------- \n"+sLineItemsQuery);

                            String startDate = sprojectEndDate;
                            //(warrantyrs.getString("c_projectStartDate")!= null)?warrantyrs.getString("c_projectStartDate").toString():"";
                            LogUtil.info("---Start Date: ---", " ------- \n"+startDate);
                            //calculate end date using warranty
                            Date dstrDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                            //LocalDate.parse(startDate);//
                            //LocalDate date = LocalDate.parse("2020-05-03");
                            // Displaying date

                            Statement statementLineItem = null;
                            LogUtil.info("---Before Executing Warranty Query: ---", " ------- \n");
                            statementLineItem = connection.createStatement();
                            ResultSet rsLineItem = statementLineItem.executeQuery(sLineItemsQuery);
                            while (rsLineItem.next()) {
                                LogUtil.info("---After Executing Warranty Query: ---", " ------- \n"+rsLineItem.getString(1));
                                FormRow r1 = new FormRow();
                                String newitemid = UuidGenerator.getInstance().getUuid();
                                r1.put("id", newitemid);
                                r1.put("warranty_ref","#process.recordId#");
                                r1.put("warrantyItem", rsLineItem.getString(2));
                                r1.put("equipment",rsLineItem.getString(3));
                                r1.put("warranty",rsLineItem.getString(4));
                                r1.put("startDate", startDate);
                                int months = Integer.valueOf(rsLineItem.getString(4));
                                LogUtil.info("---ResultSet: ---", " ------- \n"+rsLineItem.getString(2)+rsLineItem.getString(3));
                                // Convert Date to Calendar
                                Calendar c = Calendar.getInstance();
                                c.setTime(dstrDate);
                                c.add(Calendar.MONTH, months);
                                c.add(Calendar.DAY_OF_MONTH,-1);
                                Date newDate = c.getTime();
                                LogUtil.info("---New Date: ---", " ------- \n"+newDate.toString());
                                String endDate = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
                                LogUtil.info("---End Date: ---", " ------- \n"+endDate);
                                r1.put("endDate", endDate);
                                r1.put("warranty_status", rsLineItem.getString(5));
                                f.add(r1);
                            }
                            LogUtil.info("---After Executing Warranty Query: Outside While Loop ---", " ------- \n"+sLineItemsQuery);
                            //Populate the form
                            formDataDao.saveOrUpdate("projectItemsWarranty", "proj_items_warranty", f);
                        }
                    }
                }
            }
            catch (Exception e) {
                LogUtil.error("--- Catch Exception",e,e.getMessage());

            }
            finally {
                try {
                    if(connection != null) {
                        connection.close();
                    }
                } catch(SQLException e) {
                }
            }

        }
        // Cash Sales Part
        else if(orderType.equals("Cash Sales")){
            String warrantyOriginalId = null;
            LogUtil.info("--- Cash Sales Record Id", " ------- \n"+id);
            String invoiceNumber = "#variable.invoicenumber#";
            String processType = "#variable.processType#";
            String sWarrantyStartDate = "";
            LogUtil.info("--- invoiceNumber", " ------- \n"+invoiceNumber);

            String sInvQuery = "select * from app_fd_do_master where c_invoiceNumber in ('"+invoiceNumber+"')";

            LogUtil.info("--- Query for DO orders", " ------- \n"+sInvQuery);
            Connection connection = null;
            Statement statementSO = null;
            LogUtil.info("--- Outside try block", " ------- \n");
            try {
                DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
                connection = ds.getConnection();
                // execute SQL query

                if(!connection.isClosed()) {
                    statementSO = connection.createStatement();
                    ResultSet rsDO = statementSO.executeQuery(sInvQuery);
                    LogUtil.info("--- Query Executed", " ------- \n");
                    //LogUtil.info("--- Outside resultset of DO orders", " ------- \n"+rsDO.toString());
                    while (rsDO.next()) {
                        LogUtil.info("--- Inside resultset of DO orders", " ------- \n"+rsDO.toString());
                        String doNumber = rsDO.getString("c_do_Number");
                        sWarrantyStartDate = rsDO.getString("c_invoiceDate");
                        LogUtil.info("--- DO number", " ------- \n"+doNumber);
                        if(doNumbers.containsKey(invoiceNumber))
                        {
                            ArrayList DOs = (ArrayList) doNumbers.get(invoiceNumber);
                            if(!DOs.contains(doNumber)){
                                DOs.add(doNumber);
                                strbDOQueryParam.append("'"+doNumber +"',");
                                doNumbers.put(invoiceNumber, DOs);
                            }
                        }
                        else
                        {
                            ArrayList DOs = new ArrayList();
                            DOs.add(doNumber);
                            strbDOQueryParam.append("'"+doNumber +"',");
                            System.out.println(doNumber);
                            doNumbers.put(invoiceNumber, DOs);
                        }

                        if(invoices.containsKey(invoiceNumber))
                        {
                            ArrayList INs = (ArrayList) invoices.get(invoiceNumber);
                            if(!INs.contains(invoiceNumber)){
                                INs.add(invoiceNumber);
                                invoices.put(invoiceNumber, INs);
                            }
                        }
                        else
                        {
                            ArrayList INs = new ArrayList();
                            INs.add(invoiceNumber);
                            System.out.println(invoiceNumber);
                            invoices.put(invoiceNumber, INs);
                        }
                    }


                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                    String key = invoiceNumber;
                    FormRowSet rows = new FormRowSet();
                    FormRow newRow = new FormRow();
                    FormRowSet rowsWarrantyReviewData = formDataDao.find("warranty_review_form", "review_form", "where c_invoices =? and c_rev_status=?", new Object[] {key,"Active"}, null, null, null, null);
                    rowsWarrantyReviewData.setMultiRow(true);
                    LogUtil.info("--- Before If Else Loop", " ------- \n" + rowsWarrantyReviewData);
                    //there is a warranty record for the project number retrieve it and display
                    //if there are more than one warranty record, get the latest revision
                    if(rowsWarrantyReviewData != null && !rowsWarrantyReviewData.isEmpty()){
                        LogUtil.info("--- If loop ", " ------- \n"+ id);
                        for(FormRow	row:rowsWarrantyReviewData)
                        {
                            newRow.putAll(row);
                            //Capture Other fields and increment the revision if there is a change in if there is a change in
                            //invoice section, DO section, SO sections, quantity change, Item change or warranty dates



                            StringBuilder strbDeliveryorders = new StringBuilder();
                            List dorders = (List) doNumbers.get(invoiceNumber);
                            if(dorders != null && dorders.size() > 0){
                                for (Object strDO : dorders) {
                                    strbDeliveryorders.append(strDO + ", ");
                                }
                            }
                            String strDeliveryorders = strbDeliveryorders.toString();
                            strDeliveryorders = strDeliveryorders.substring(0, strDeliveryorders.length()-2);
                            String strOriginalDOs = (row.get("do_no")!= null)?row.get("do_no").toString():"";
                            //LogUtil.info("--- Old Delivery Orders ", " ------- \n"+ strOriginalDOs);
                            //LogUtil.info("--- New Delivery Orders ", " ------- \n"+ strDeliveryorders);
                            String universalDoNo = null;
                            if(!strDeliveryorders.equals(strOriginalDOs)){
                                bChangeFlag = true;
                                newRow.put("do_no", strDeliveryorders);
                                universalDoNo = strDeliveryorders;
                            }
                            else {
                                row.put("do_no", strOriginalDOs);
                                universalDoNo = strOriginalDOs;
                            }

                            StringBuilder strBInvoices = new StringBuilder();
                            List invs = (List) invoices.get(invoiceNumber);
                            if(invs != null && invs.size() > 0){
                                for (Object strIn : invs) {
                                    strBInvoices.append(strIn + ", ");
                                }
                            }
                            String strInvoices = strBInvoices.toString();
                            strInvoices = strInvoices.substring(0, strInvoices.length()-2);
                            String strOriginalInvs = (row.get("invoices")!= null)?row.get("invoices").toString():"";
                            //LogUtil.info("--- Old Invoices ", " ------- \n"+ strInvoices);
                            //LogUtil.info("--- New Invoices ", " ------- \n"+ strOriginalInvs);
                            if(!strInvoices.equals(strOriginalInvs)){
                                bChangeFlag = true;
                                newRow.put("invoices", strInvoices);
                            }
                            else {
                                row.put("invoices", strOriginalInvs);
                            }


                            //revision, increment it if there is change in the fields
                            revision = (row.get("rev")!= null)?Integer.valueOf(row.get("rev").toString()):0;
                            warrantyOriginalId = row.get("id").toString();
                            LogUtil.info("---------------Original Id------------",warrantyOriginalId);
                            if(bChangeFlag){
                                // if new warranty record is generated then set that new id in process variable.]
                                PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
                                WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
                                wm.activityVariable(assignment.getActivityId(),"rId", "#process.recordId#");
                                LogUtil.info("--- Change Flag", " ------- \n");
                                String oldId = (String) row.get("id");
                                //increment the revision only if the warranty status was in authorized status and move it to draft
                                strOrgWarStatus = (String) row.get("warranty_status");
                                if(strOrgWarStatus.equals("Authorized")){
                                    revision += 1;
                                }

                                //warrantyOriginalId = row.get("id").toString();
                                newRow.setId(id);
                                newRow.put("rev_status", "Active");
                                newRow.put("oldId", row.get("id"));
                                newRow.put("newid", id);
                                newRow.put("rev",String.valueOf(revision));
                                newRow.put("warranty_status", "Draft");
                                LogUtil.info("--- New Row ", " ------- \n"+ newRow);


                                row.put("rev_status", "Inactive");
                                LogUtil.info("--- Old Row ", " ------- \n"+ row);
                                LogUtil.info("--- Old Id for Line Item details ", " ------- \n"+oldId);

                                String [] universalDoArray = universalDoNo.split(",");
                                StringBuilder doBuilder = new StringBuilder();
                                for(String doNum : universalDoArray){
                                    doBuilder.append("'").append(doNum.trim()).append("',");
                                }
                                doBuilder.deleteCharAt(doBuilder.length()-1);
                                universalDoNo = doBuilder.toString();
                                // add warranty rows
                                FormRowSet f = new FormRowSet();
                                f.setMultiRow(true);
                                String strDOQueryParam= strbDOQueryParam.toString();
                                String sLineItemsQuery = "SELECT distinct w.id, w.c_warrantyItem,w.c_equipment, w.c_warranty, w.c_warranty_status FROM app_fd_warranty w WHERE w.c_equipment IN (SELECT i.c_item_category FROM app_fd_do_master d join app_fd_items_master i ON d.c_item_code = i.c_item_code WHERE d.c_do_number in ("+universalDoNo+"))";
                                LogUtil.info("---Warranty Query ---", " ------- \n"+sLineItemsQuery);

                                String startDate = sWarrantyStartDate;
                                //(warrantyrs.getString("c_projectStartDate")!= null)?warrantyrs.getString("c_projectStartDate").toString():"";
                                LogUtil.info("---Start Date: ---", " ------- \n"+startDate);
                                //calculate end date using warranty
                                Date dstrDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                                // ------------ Test Code -----------------
     /*Calendar c = Calendar.getInstance();
     c.setTime(dstrDate);
     c.add(Calendar.MONTH, 18);
     Date newDate = c.getTime();
     LogUtil.info("---New Date: ---", " ------- \n"+newDate.toString());
     String endDate = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
     LogUtil.info("---End Date: ---", " ------- \n"+endDate);*/

                                // ------------ End Test Code -------------
                                //LocalDate.parse(startDate);//
                                //LocalDate date = LocalDate.parse("2020-05-03");
                                // Displaying date


                                LogUtil.info("---Before Executing Warranty Query: ---", " ------- \n");
                                Statement statementLineItem = connection.createStatement();
                                ResultSet rsLineItem = statementLineItem.executeQuery(sLineItemsQuery);

                                while (rsLineItem.next()) {
                                    LogUtil.info("---After Executing Warranty Query: ---", " ------- \n"+rsLineItem.getString(1));
                                    FormRow r1 = new FormRow();
                                    String newitemid = UuidGenerator.getInstance().getUuid();
                                    r1.put("id", newitemid);
                                    r1.put("warrantyItem", rsLineItem.getString(2));
                                    r1.put("equipment",rsLineItem.getString(3));
                                    r1.put("warranty",rsLineItem.getString(4));
                                    r1.put("warranty_ref","#process.recordId#");
                                    r1.put("startDate", startDate);
                                    //r1.put("warranty_ref",id);
                                    int months = Integer.valueOf(rsLineItem.getString(4));
                                    LogUtil.info("---ResultSet: ---", " ------- \n"+rsLineItem.getString(2)+rsLineItem.getString(3));
                                    // Convert Date to Calendar
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(dstrDate);
                                    c.add(Calendar.MONTH, months);
                                    c.add(Calendar.DAY_OF_MONTH,-1);
                                    Date newDate = c.getTime();
                                    LogUtil.info("---New Date: ---", " ------- \n"+newDate.toString());
                                    String endDate = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
                                    LogUtil.info("---End Date: ---", " ------- \n"+endDate);
                                    r1.put("endDate", endDate);
                                    r1.put("warranty_status", rsLineItem.getString(5));
                                    f.add(r1);
                                }
                                System.out.println(sLineItemsQuery);
                                //Populate the form
                                formDataDao.saveOrUpdate("projectItemsWarranty", "proj_items_warranty", f);



 /*FormRowSet rowsLineItemsData = formDataDao.find("projectItemsWarranty", "proj_items_warranty", "where c_warranty_ref =?", new Object[] {oldId}, null, null, null, null);
 rowsLineItemsData.setMultiRow(true);
 LogUtil.info("--- Before Line Item Details", " ------- \n" + rowsLineItemsData);
 FormRow newItemRow = new FormRow();
 if(rowsLineItemsData != null && !rowsLineItemsData.isEmpty()){

 LogUtil.info("--- Inside Line Item details ", " ------- \n"+oldId);
 //System.out.println(rowsWarrantyReviewData);
 for(FormRow	itemrow:rowsLineItemsData)
 {
 String newItemid = UuidGenerator.getInstance().getUuid();
 newItemRow.putAll(itemrow);
 newItemRow.setId(newItemid);
 newItemRow.put("warranty_ref", id);
 }
 rowsLineItemsData.add(newItemRow);
 LogUtil.info("--- After Line Item Details", " ------- \n" + rowsLineItemsData);
 //Populate the form
 formDataDao.saveOrUpdate("projectItemsWarranty", "proj_items_warranty", rowsLineItemsData);
}*/                    }
                            else if(bChangeFlag == false && processType != null && processType.equals("Reload")){
                                String[] universalSoArray = strOriginalDOs.split(",");
                                LogUtil.info("Original array",""+universalSoArray);
                                StringBuilder universalSoBuilder = new StringBuilder();
                                for(String str : universalSoArray){
                                    universalSoBuilder.append("'"+str.trim()).append("',");
                                }
                                universalSoBuilder.deleteCharAt(universalSoBuilder.length()-1);
                                String doQueryParam = universalSoBuilder.toString();
                                LoadWarrantyNew.newItemUploadForCash(doQueryParam,sWarrantyStartDate,connection,warrantyOriginalId,formDataDao);
                            }

                        }

                        String formId = "warranty_review_form";
                        String tableName = "review_form";
                        String fieldIDProject ="id";
                        //Populate the form
                        //rows.add(row);
                        if(bChangeFlag){
                            rowsWarrantyReviewData.add(newRow);
                        }
                        formDataDao.saveOrUpdate(formId, tableName, rowsWarrantyReviewData);
                    }
                    //there is no warranty generated, generate a new one and save it in db and then load the form
                    else{
                        //Set values
                        LogUtil.info("--- Else Loop", " ------- \n"+id);
                        System.out.println("Else Loop");
                        FormRow row = new FormRow();
                        row.setProperty("id",id);


                        row.setProperty("brand",strbrand);

                        row.setProperty("project","Cash Sales");

                        StringBuilder strbDeliveryorders = new StringBuilder();
                        List dorders = (List) doNumbers.get(invoiceNumber);
                        if(dorders != null && dorders.size() > 0){
                            for (Object strDO : dorders) {
                                strbDeliveryorders.append(strDO + ", ");
                            }
                            String strDeliveryorders = strbDeliveryorders.toString();
                            row.setProperty("do_no", strDeliveryorders.substring(0, strDeliveryorders.length()-2));
                        }


                        StringBuilder strBInvoices = new StringBuilder();
                        List invs = (List) invoices.get(invoiceNumber);
                        if(invs != null && invs.size() > 0){
                            for (Object strIn : invs) {
                                strBInvoices.append(strIn + ", ");
                            }
                            String strInvoices = strBInvoices.toString();
                            row.setProperty("invoices", strInvoices.substring(0, strInvoices.length()-2));
                        }


                        //warranty - assuming this will be a unique number for a project
                        appDef = AppUtil.getCurrentAppDefinition();
                        EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");


                        //Create the warranty Number and set it to appropriate column
                        int modifiedWarrantyNumber = environmentVariableDao.getIncreasedCounter("warrantynumber", "Invoice"+ invoiceNumber, appDef);
                        String strWN = String.format("%03d", new Object[]{modifiedWarrantyNumber});
                        //new Object[]{new Integer(modifiedWarrantyNumber)});
                        System.out.println("warranty number -" + strWN);
                        warrantynumber = warrantynumber + strWN;
                        row.setProperty("wo_no",warrantynumber);

                        //warranty date
                        row.setProperty("warr_date",ft.format(warrantyDate));

                        //revision =0, as there warranty is generated for the first time for this project
                        revision = 0;
                        //environmentVariableDao.getIncreasedCounter("rev-"+ sprojectNumber, "new", appDef);
                        row.setProperty("rev",String.valueOf(revision));

                        row.setProperty("rev_status", "Active");

                        row.setProperty("warranty_status", "Draft");
                        if("#variable.processType#".equals("Reload")){
                            row.setDateModified(new Date());
                        }
                        else{
                            row.setDateCreated(new Date());
                            row.setDateModified(new Date());
                        }

                        String formId = "warranty_review_form";
                        String tableName = "review_form";

                        //Populate the form
                        rowsWarrantyReviewData.add(row);
                        formDataDao.saveOrUpdate(formId, tableName, rowsWarrantyReviewData);
                        LogUtil.info("---Before Warranty Query ---", " ------- \n");
                        FormRowSet f = new FormRowSet();
                        f.setMultiRow(true);
                        String strDOQueryParam= strbDOQueryParam.toString();
                        String sLineItemsQuery = "SELECT distinct w.id, w.c_warrantyItem,w.c_equipment, w.c_warranty, w.c_warranty_status FROM app_fd_warranty w WHERE w.c_equipment IN (SELECT i.c_item_category FROM app_fd_do_master d join app_fd_items_master i ON d.c_item_code = i.c_item_code WHERE d.c_do_number in ("+strDOQueryParam.substring(0, strDOQueryParam.length()-1)+"))";
                        LogUtil.info("---Warranty Query ---", " ------- \n"+sLineItemsQuery);

                        String startDate = sWarrantyStartDate;
                        //(warrantyrs.getString("c_projectStartDate")!= null)?warrantyrs.getString("c_projectStartDate").toString():"";
                        LogUtil.info("---Start Date: ---", " ------- \n"+startDate);
                        //calculate end date using warranty
                        Date dstrDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                        // ------------ Test Code -----------------
 /*Calendar c = Calendar.getInstance();
 c.setTime(dstrDate);
 c.add(Calendar.MONTH, 18);
 Date newDate = c.getTime();
 LogUtil.info("---New Date: ---", " ------- \n"+newDate.toString());
 String endDate = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
 LogUtil.info("---End Date: ---", " ------- \n"+endDate);*/

                        // ------------ End Test Code -------------
                        //LocalDate.parse(startDate);//
                        //LocalDate date = LocalDate.parse("2020-05-03");
                        // Displaying date


                        LogUtil.info("---Before Executing Warranty Query: ---", " ------- \n");
                        Statement statementLineItem = connection.createStatement();
                        ResultSet rsLineItem = statementLineItem.executeQuery(sLineItemsQuery);

                        while (rsLineItem.next()) {
                            LogUtil.info("---After Executing Warranty Query: ---", " ------- \n"+rsLineItem.getString(1));
                            FormRow r1 = new FormRow();
                            String newitemid = UuidGenerator.getInstance().getUuid();
                            r1.put("id", newitemid);
                            r1.put("warrantyItem", rsLineItem.getString(2));
                            r1.put("equipment",rsLineItem.getString(3));
                            r1.put("warranty",rsLineItem.getString(4));
                            r1.put("warranty_ref","#process.recordId#");
                            r1.put("startDate", startDate);
                            int months = Integer.valueOf(rsLineItem.getString(4));
                            LogUtil.info("---ResultSet: ---", " ------- \n"+rsLineItem.getString(2)+rsLineItem.getString(3));
                            // Convert Date to Calendar
                            Calendar c = Calendar.getInstance();
                            c.setTime(dstrDate);
                            c.add(Calendar.MONTH, months);
                            c.add(Calendar.DAY_OF_MONTH,-1);
                            Date newDate = c.getTime();
                            LogUtil.info("---New Date: ---", " ------- \n"+newDate.toString());
                            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
                            LogUtil.info("---End Date: ---", " ------- \n"+endDate);
                            r1.put("endDate", endDate);
                            r1.put("warranty_status", rsLineItem.getString(5));
                            f.add(r1);
                        }
                        System.out.println(sLineItemsQuery);
                        //Populate the form
                        formDataDao.saveOrUpdate("projectItemsWarranty", "proj_items_warranty", f);
                    }

                }
            }
            catch (Exception e) {
                e.printStackTrace();
                LogUtil.info("--- Catch Exception =", " ------- \n"+e.getMessage());

            }
            finally {
                try {
                    if(connection != null) {
                        connection.close();
                    }
                } catch(SQLException e) {
                }
            }

        }
        return null;
    }

    public static void newItemUploadForProject(String soNum, String startDate,Connection connection, String id,FormDataDao formDataDao) throws SQLException, ParseException {
        LogUtil.info("Update Warranty Program","Update Warranty Program");
        LogUtil.info("Warranty Item Id",id);
        // delete old warranty items.
        String formId = "projectItemsWarranty";
        String tableName = "proj_items_warranty";
        FormRowSet warrantyItemSet =
                formDataDao.find(formId,tableName,"where c_warranty_ref=?", new Object[]{id},null,false,null,null);
        formDataDao.delete(formId,tableName,warrantyItemSet);
        // existing warranty
        FormRowSet f = new FormRowSet();
        f.setMultiRow(true);
        String sLineItemsQuery = "SELECT distinct w.id, w.c_warrantyItem,w.c_equipment, w.c_warranty, w.c_warranty_status FROM app_fd_warranty w WHERE w.c_equipment IN (SELECT i.c_item_category FROM app_fd_do_master d join app_fd_items_master i ON d.c_item_code = i.c_item_code WHERE d.c_so_number in ("+soNum+"))";
        LogUtil.info("---Warranty Query ---", " ------- \n"+sLineItemsQuery);

        //(warrantyrs.getString("c_warranty_start_date")!= null)?warrantyrs.getString("c_projectStartDate").toString():"";
        LogUtil.info("---Start Date: ---", " ------- \n"+startDate);
        //calculate end date using warranty
        Date dstrDate = new SimpleDateFormat("yyyy-mm-dd").parse(startDate);
        //LocalDate.parse(startDate);//
        //LocalDate date = LocalDate.parse("2020-05-03");
        // Displaying date

        Statement statementLineItem = null;
        LogUtil.info("---Before Executing Warranty Query: ---", " ------- \n");
        statementLineItem = connection.createStatement();
        ResultSet rsLineItem = statementLineItem.executeQuery(sLineItemsQuery);
        while (rsLineItem.next()) {
            LogUtil.info("---After Executing Warranty Query: ---", " ------- \n"+rsLineItem.getString(1));
            FormRow r1 = new FormRow();
            String newitemid = UuidGenerator.getInstance().getUuid();
            r1.put("id", newitemid);
            r1.put("warrantyItem", rsLineItem.getString(2));
            r1.put("warranty_ref",id);
            r1.put("equipment", rsLineItem.getString(3));
            r1.put("startDate", startDate);
            r1.put("warranty",rsLineItem.getString(4));
            int months = Integer.valueOf(rsLineItem.getString(4));
            LogUtil.info("---ResultSet: ---", " ------- \n"+rsLineItem.getString(2)+rsLineItem.getString(3));
            // Convert Date to Calendar
            Calendar c = Calendar.getInstance();
            c.setTime(dstrDate);
            c.add(Calendar.MONTH, months);
            Date newDate = c.getTime();
            LogUtil.info("---New Date: ---", " ------- \n"+newDate.toString());
            String endDate = new SimpleDateFormat("yyyy-mm-dd").format(newDate);
            LogUtil.info("---End Date: ---", " ------- \n"+endDate);
            r1.put("endDate", endDate);
            r1.put("warranty_status", rsLineItem.getString(5));
            f.add(r1);
        }
        //Populate the form
        formDataDao.saveOrUpdate(formId,tableName , f);

    }

    public static void newItemUploadForCash(String doNum, String startDate,Connection connection, String id,FormDataDao formDataDao) throws ParseException, SQLException {
        // delete old records
        String formId = "projectItemsWarranty";
        String tableName = "proj_items_warranty";
        FormRowSet warrantyItemSet =
                formDataDao.find(formId,tableName,"where c_warranty_ref=?", new Object[]{id},null,false,null,null);
        formDataDao.delete(formId,tableName,warrantyItemSet);

        LogUtil.info("Cash Sale Warranty Load","Without Change");
        // create new records
        FormRowSet f = new FormRowSet();
        f.setMultiRow(true);
        String sLineItemsQuery = "SELECT distinct w.id, w.c_warrantyItem,w.c_equipment, w.c_warranty, w.c_warranty_status FROM app_fd_warranty w WHERE w.c_equipment IN (SELECT i.c_item_category FROM app_fd_do_master d join app_fd_items_master i ON d.c_item_code = i.c_item_code WHERE d.c_do_number in ("+doNum+"))";
        LogUtil.info("Qery",sLineItemsQuery);
        LogUtil.info("DO NUMBER ----------",doNum);
        //(warrantyrs.getString("c_projectStartDate")!= null)?warrantyrs.getString("c_projectStartDate").toString():"";
        LogUtil.info("---Start Date: ---", " ------- \n"+startDate);
        //calculate end date using warranty
        Date dstrDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        // ------------ Test Code -----------------
        LogUtil.info("---Before Executing Warranty Query: ---", " ------- \n");
        Statement statementLineItem = connection.createStatement();
        ResultSet rsLineItem = statementLineItem.executeQuery(sLineItemsQuery);

        while (rsLineItem.next()) {
            LogUtil.info("---After Executing Warranty Query: ---", " ------- \n"+rsLineItem.getString(1));
            FormRow r1 = new FormRow();
            String newitemid = UuidGenerator.getInstance().getUuid();
            r1.put("id", newitemid);
            r1.put("warrantyItem", rsLineItem.getString(2));
            r1.put("equipment",rsLineItem.getString(3));
            r1.put("warranty",rsLineItem.getString(4));
            r1.put("startDate", startDate);
            r1.put("warranty_ref",id);
            int months = Integer.valueOf(rsLineItem.getString(4));
            LogUtil.info("---ResultSet: ---", " ------- \n"+rsLineItem.getString(2)+rsLineItem.getString(3));
            // Convert Date to Calendar
            Calendar c = Calendar.getInstance();
            c.setTime(dstrDate);
            c.add(Calendar.MONTH, months);
            c.add(Calendar.DAY_OF_MONTH,-1);
            Date newDate = c.getTime();
            LogUtil.info("---New Date: ---", " ------- \n"+newDate.toString());
            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
            LogUtil.info("---End Date: ---", " ------- \n"+endDate);
            r1.put("endDate", endDate);
            r1.put("warranty_status", rsLineItem.getString(5));
            f.add(r1);
        }
        //Populate the form
        formDataDao.saveOrUpdate(formId, tableName, f);
    }
}
return LoadWarrantyNew.execute(workflowAssignment, appDef, request);

