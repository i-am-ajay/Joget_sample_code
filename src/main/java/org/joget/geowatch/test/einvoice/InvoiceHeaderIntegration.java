package org.joget.geowatch.test.einvoice;

import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.joget.workflow.model.service.WorkflowManager;

public class InvoiceHeaderIntegration {
    static volatile String globalProcessStatus = "";
    public static void dbCall() throws InterruptedException {
        // check if there is already a validation under process.
        /*globalProcessStatus = "#envVariable.is_validating#";
        while(globalProcessStatus.equals("1")){
            LogUtil.info("Thread waiting","");
            Thread.sleep(1000);
            globalProcessStatus = null;
            globalProcessStatus = "#envVariable.is_validating#";
            LogUtil.info("Thread wait over","");
        }*/
        // set env variable to true to mark that validation process is running for some invoice.
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        EnvironmentVariableDao envDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
        /*EnvironmentVariable variable = envDao.loadById("is_validating",appDef);
        variable.setValue("1");
        envDao.update(variable);*/

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        FormDataDao dao = (FormDataDao)AppUtil.getApplicationContext().getBean("formDataDao");
        WorkflowManager wm = (WorkflowManager)AppUtil.getApplicationContext().getBean("workflowManager");
        Connection con = null;

        // discount tables
        String discountTableName = "doc_allow_charges";
        String discountFormName = "document_allowance_or_charges";
        FormRowSet discountRowSet = new FormRowSet();
        discountRowSet.setMultiRow(true);

        // header tables
        String tableName = "xml_invoice_header";
        String formId = "e_invoice_xml_invoice_header";
        //String jobId = "#requestParam.work_orderno#";
        FormRowSet rowSet = new FormRowSet();
        rowSet.setMultiRow(true);

        String id = "92229093";
        String companyId = "018";
        String uuid = "f1bc4ef8-5311-3111-e9e0-303190a0a8f3";
        boolean isWorkflowVariableSet = false;
        if (id == null || id.isEmpty()) {
            isWorkflowVariableSet = true;
            id = "#variable.invoice_id#";
            companyId = "#variable.companyId#";
            uuid = "#variable.uuid#";
        }
        LogUtil.info("ID",id);
        boolean recordUpdated = false;
        String headerId = null;
        FormRow oldRow = dao.load(formId, tableName, id);

        if (oldRow != null && !oldRow.isEmpty()) {
            LogUtil.info("Old Row Id",oldRow.getId());
            LogUtil.info("Response",oldRow.getProperty("responseStatus"));
            LogUtil.info("updated_status",oldRow.getProperty("updated_status"));
            String status = oldRow.getProperty("responseStatus");
            if (status != null && !status.isEmpty() && status.equalsIgnoreCase("Reported")) {
                wm.activityVariable("#assignment.activityId#", "invoice_status", "validated");
                return;
            }
            else if (
                    oldRow.getProperty("updated_status") != null &&
                            !oldRow.getProperty("updated_status").isEmpty()
                            && oldRow.getProperty("updated_status").equals("updated")) {
                wm.activityVariable("#assignment.activityId#", "invoice_status", "validated");
                return;
            }
            else if(oldRow.getProperty("update_status")==null){
                recordUpdated = true;
            }
        }


        try {
            // generate counter.

            LogUtil.info("Company Id",companyId);
            EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
            //Create the warranty Number and set it to appropriate column
            int counter = environmentVariableDao.getIncreasedCounter("counter_"+companyId, "Counter", appDef);
            LogUtil.info("Counter",Integer.toString(counter));
            LogUtil.info("Running Query", "");
            con = ds.getConnection();
            String query = headerTableQuery();
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, id);
            LogUtil.info("Get Result Set","Get Result Set");
            //stmt.setString(1, "2518");
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                String tempUuid = null;
                if(recordUpdated) {
                    tempUuid = UuidGenerator.uuidGenerator.getUuid();
                    LogUtil.info("Record",Boolean.toString(recordUpdated));
                    headerId = tempUuid;
                    wm.activityVariable("#assignment.activityId#","invoice_id", headerId);
                }
                else{
                    headerId = set.getString(1);
                    wm.activityVariable("#assignment.activityId#","invoice_id", id);
                }
                if(!isWorkflowVariableSet) {
                    wm.activityVariable("#assignment.activityId#", "uuid", "#requestParam.uuid#");
                    wm.activityVariable("#assignment.activityId#", "companyId", "#requestParam.companyId#");
                }


                String currencyCode = processColumn(set.getString(56));
                FormRow row = populateRowFromRecord(headerId,set, currencyCode,counter);
                if(recordUpdated){
                    row.setProperty("original_sys_id",set.getString(1));
                }
                FormRow discountRow = discountOrCharge(set, currencyCode, headerId);
                rowSet.add(row);
                discountRowSet.add(discountRow);
            }
            dao.saveOrUpdate(formId, tableName, rowSet);
            dao.saveOrUpdate(discountFormName, discountTableName, discountRowSet);
            LogUtil.info("Header part executed","0-----");
            // update items
            InvoiceHeaderIntegration.InvoiceItemIntegration.dbCall(id,headerId,recordUpdated);
        }
        catch(Exception ex){
            LogUtil.error("isReservedStockAvailabe", ex, ex.getMessage());
        }
        finally{
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {

            }
        }
        return;
    }

    public static FormRow populateRowFromRecord(String headerId,ResultSet set, String currencyCode,int counter) throws SQLException {

        FormRow row = new FormRow();
        row.setProperty("id",headerId);
        row.setProperty("process_control",processColumn(set.getString(2))); // not given yet
        row.setProperty("invoice_identifier",processColumn(set.getString(3)));
        row.setProperty("uuid",processColumn(set.getString(4)));
        row.setProperty("invoice_issue_date",processColumn(set.getString(5)));
        row.setProperty("invoice_type_code",processColumn(set.getString(6)));
        row.setProperty("invoice_transaction_code",processColumn(set.getString(7)));
        row.setProperty("notes",processColumn(set.getString(8)));
        row.setProperty("special_tax_treatment",processColumn(set.getString(9))); // not given yet
        row.setProperty("purchase_order_id",processColumn(set.getString(10)));
        String billingRef = processColumn(set.getString(11));
        row.setProperty("billing_reference_id",(billingRef == null || billingRef.isEmpty() ? "0" : billingRef));
        row.setProperty("billing_reference_date",processColumn(set.getString(12)));
        row.setProperty("contract_id",processColumn(set.getString(13)));
        row.setProperty("invoice_counter_value",Integer.toString(counter));
        row.setProperty("previous_invoice_hash","");
        row.setProperty("qr_code","");
        row.setProperty("cryptographic_stamp","");
        row.setProperty("seller_group_vat_number",processColumn(set.getString(15)));
        row.setProperty("other_seller_id",processColumn(set.getString(16)));
        row.setProperty("seller_street_address",processColumn(set.getString(17)));
        row.setProperty("seller_additional_street_address",processColumn(set.getString(18)));
        row.setProperty("seller_building_number",processColumn(set.getString(19)));
        row.setProperty("seller_city",processColumn(set.getString(20)));
        row.setProperty("seller_postal_code",processColumn(set.getString(21)));
        row.setProperty("seller_email",processColumn(set.getString(22)));
        row.setProperty("seller_state",processColumn(set.getString(23)));
        row.setProperty("seller_district",processColumn(set.getString(24)));
        row.setProperty("seller_country_code",processColumn(set.getString(25)));
        row.setProperty("seller_vat_number",processColumn(set.getString(26)));
        row.setProperty("seller_name",processColumn(set.getString(27)));
        row.setProperty("buyer_group_vat_number",processColumn(set.getString(28)));
        row.setProperty("other_buyer_id",processColumn(set.getString(29)));
        row.setProperty("buyer_street",processColumn(set.getString(30)));
        row.setProperty("buyer_additional_street",processColumn(set.getString(31)));
        row.setProperty("buyer_building_number",processColumn(set.getString(32)));
        row.setProperty("buyer_additional_number",processColumn(set.getString(33)));
        row.setProperty("buyer_city",processColumn(set.getString(34)));
        row.setProperty("buyer_postal_code",processColumn(set.getString(35)));
        row.setProperty("buyer_state",processColumn(set.getString(36)));
        row.setProperty("buyer_district",processColumn(set.getString(37)));
        row.setProperty("buyer_country_code",processColumn(set.getString(38)));
        row.setProperty("buyer_vat_number",processColumn(set.getString(39)));
        row.setProperty("buyer_name",processColumn(set.getString(40)));
        row.setProperty("supply_date",processColumn(set.getString(41)));
        row.setProperty("supply_end_date",processColumn(set.getString(42)));
        row.setProperty("payment_means_type_code",processColumn(set.getString(43)));
        row.setProperty("dc_note_reason",processColumn(set.getString(44)));
        row.setProperty("payment_terms",processColumn(set.getString(45)));
        row.setProperty("seller_additional_number",processColumn(set.getString(60)));
        row.setProperty("other_buyer_id_scheme",processColumn(set.getString(58)));
        row.setProperty("other_seller_id_scheme",processColumn(set.getString(59)));
        row.setProperty("company_code",processColumn(set.getString(57)));
        /*
        row.setProperty("document_charge",currencyCode);
        row.setProperty("document_charge_percentage",currencyCode);
        row.setProperty("document_charge_amount",currencyCode);
        row.setProperty("document_charge_currency",currencyCode);
        row.setProperty("document_charge_base_amount",currencyCode);
        row.setProperty("document_charge_base_amount_currency",currencyCode);
        row.setProperty("document_charge_vat_code",currencyCode);
        row.setProperty("document_charge_vat_rate",processColumn(set.getString(1)));
        row.setProperty("document_charge_tax_scheme_id",processColumn(set.getString(2)));*/
        //row.setProperty("invoice_total_net_amount",processColumn(set.getString(3)));

        //row.setProperty("invoice_total_discount",processColumn(set.getString(5)));

        //row.setProperty("invoice_total_charges_amount",processColumn(set.getString(7)));

        //row.setProperty("invoice_total_amount_without_vat",processColumn(set.getString(9)));

        //row.setProperty("invoice_total_vat",processColumn(set.getString(11)));

        //row.setProperty("Invoice_total_vat_accounting_currency_code",processColumn(set.getString(13)));

        //row.setProperty("invoice_total_amount_with_vat",processColumn(set.getString(15)));

        row.setProperty("advance_paid_amount",processColumn(set.getString(53)));
        //row.setProperty("rounding_amount",currencyCode);
        //
        //row.setProperty("vat_category_taxable_amount",currencyCode); // true or false
        //row.setProperty("vat_category_tax_amount",currencyCode);
        //row.setProperty("vat_category_code",currencyCode);
        //row.setProperty("vat_category_percentage",currencyCode);
        row.setProperty("vat_exemption_reason_code",processColumn(set.getString(54)));
        row.setProperty("vat_exemption_reason",processColumn(set.getString(55)));
        row.setProperty("vat_tax_scheme_id","VAT");
        row.setDateCreated(new Date());
        setCurrency(row,currencyCode);
        return row;
    }

    public static FormRow discountOrCharge( ResultSet set,String currencyCode, String headerId) throws SQLException {
        FormRow row = new FormRow();
        row.setId(UuidGenerator.uuidGenerator.getUuid());
        row.setDateCreated(new Date());
        row.setProperty("INVI_INVH_SYS_ID",headerId);
        row.setProperty("doc_record_type","1");
        row.setProperty("doc_all_chg_indicator",processColumn(set.getString(46))); // true or false if it's charge and false for discount
        row.setProperty("doc_all_chg_per",processColumn(set.getString(47)));
        row.setProperty("doc_all_chg_amt_wo_vat",processColumn(set.getString(48))); // allowance amount without vat
        row.setProperty("doc_all_chg_cur",currencyCode);
        row.setProperty("doc_all_chg_base_amt",processColumn(set.getString(49)));
        row.setProperty("doc_all_chg_base_amt_cur",currencyCode);
        row.setProperty("c_document_allowance_vat_code",processColumn(set.getString(50))); // what vat amount is applied on document allowance
        row.setProperty("c_document_allowance_vat_rate",processColumn(set.getString(51)));
        row.setProperty("tax_scheme_id",processColumn(set.getString(52)));
        return row;
    }

    public static void setCurrency(FormRow row, String currencyCode){
        row.setProperty("invoice_currency_code",currencyCode);
        row.setProperty("tax_currency_code",currencyCode);
        row.setProperty("invoice_total_net_currency",currencyCode);
        row.setProperty("invoice_total_discount_currency",currencyCode);
        row.setProperty("invoice_total_charges_currency",currencyCode);
        row.setProperty("invoice_total_amount_without_vat_currency",currencyCode);
        row.setProperty("invoice_total_vat_currency",currencyCode);
        row.setProperty("invoice_total_vat_accounting_currency_code",currencyCode);
        row.setProperty("invoice_total_amount_with_vat_currency",currencyCode);
        row.setProperty("currency_advance_paid_amount",currencyCode);
        row.setProperty("currency_for_rounding_amount",currencyCode);
        row.setProperty("amount_due_for_payment_currency",currencyCode);
        row.setProperty("vat_category_taxable_amount_currency",currencyCode);
        row.setProperty("vat_category_tax_amount_currency",currencyCode);
        //row.setProperty("amount_due_for_payment",currencyCode);
    }
    public static String processColumn(String val) throws SQLException {
        return (val == null ? "" : val);
    }

    public static String headerTableQuery(){
        LogUtil.info("Query Called","");
        return "SELECT c_INVH_SYS_ID as header_id, 'reporting:1.0' as process_control,CONCAT(c_INVH_COMP_CODE, '/', c_INVH_TXN_CODE, '/', c_INVH_NO) as invoice_identifier,   \n" +
                "        c_INVH_UUID as UUID, c_INVH_DT as issue_date,c_INVH_TYPE_CODE,\n" +
                "        c_INVH_TXN_CODE_STRING as transaction_code,c_INVH_ANNOTATION as notes,'' special_tax_treatment,\n" +
                "                c_INVH_FLEX_01 purchase_order, '' as billing_ref_id, '' billing_ref_date,\n" +
                "                c_INVH_LPO_NO as contract_id, c_INVH_NO invoice_counter,'' as seller_group_vat,\n" +
                "        app_fd_fm_company.c_COMP_FLEX_08 as other_seller_id, app_fd_jog_om_address.c_ADDR_LINE_2 seller_street_address, '' as additional_seller_street,\n" +
                "        app_fd_jog_om_address.c_ADDR_FLEX_15 seller_building_number, app_fd_jog_om_address.c_ADDR_CITY_CODE seller_city, app_fd_jog_om_address.c_ADDR_ZIP_POSTAL_CODE seller_postal,\n" +
                "        app_fd_jog_om_address.c_ADDR_EMAIL seller_email,app_fd_jog_om_address.c_ADDR_PROVINCE_CODE seller_state,app_fd_jog_om_address.c_ADDR_FLEX_16 seller_district,\n" +
                "        app_fd_jog_om_address.c_ADDR_COUNTRY_CODE seller_country,app_fd_fm_company.c_COMP_FLEX_02 seller_vat_number, app_fd_fm_company.c_COMP_NAME seller_name,\n" +
                "        '' as buyer_group_vat, BuyerAddress.c_ADDR_FLEX_20 other_buyer_id,BuyerAddress.c_ADDR_LINE_2 buyer_street,\n" +
                "        '' as buyer_additional_stree, BuyerAddress.c_ADDR_FLEX_15 buyer_building_number, '' as buyer_additional_number,\n" +
                "        BuyerAddress.c_ADDR_CITY_CODE buyer_city, BuyerAddress.c_ADDR_ZIP_POSTAL_CODE buyer_postal_code,BuyerAddress.c_ADDR_PROVINCE_CODE buyer_state,\n" +
                "        BuyerAddress.c_ADDR_FLEX_16 buyer_district, BuyerAddress.c_ADDR_COUNTRY_CODE buyer_country, BuyerAddress.c_ADDR_FLEX_20 buyer_vat,\n" +
                "        c_INVH_CUST_NAME buyer_name, app_fd_jog_ot_invoice_head.c_INVH_DEL_DT as supply_date, app_fd_jog_ot_invoice_head.c_INVH_DEL_DT as supply_end_date,\n" +
                "        '1' as payment_means_type_code, '' as dc_note_reason, c_INVH_TERM_CODE as payment_terms,\n" +
                "        'false' allowance_indication, c_INVH_DISC_PERC disc_per, c_INVH_DISC_VAL disc_val,\n" +
                "                '0.0' as base_amount, 'S' as allowance_vat_code, (SELECT DISTINCT c_ITED_TED_RATE FROM app_fd_jog_ot_invoice_ted WHERE c_ITED_H_SYS_ID = app_fd_jog_ot_invoice_head.c_INVH_SYS_ID AND c_ITED_TED_TYPE_NUM = 1) as allowance_vat_rate,\n" +
                "        'VAT' as tax_scheme, c_INVH_ADVANCE advance_paid_amount, '' as vat_exemption_code,\n" +
                "        '' as vat_exemption_reason,c_INVH_CURR_CODE currency_code, c_INVH_COMP_CODE as company_code,\n" +
                "        'TIN' as buyer_id_scheme, app_fd_fm_company.c_COMP_FLEX_07 as other_seller_id_scheme, '' seller_additional_number\n" +
                "                FROM\n" +
                "        app_fd_jog_ot_invoice_head\n" +
                "        LEFT JOIN app_fd_jog_om_address ON app_fd_jog_ot_invoice_head.c_INVH_COMP_CODE  = app_fd_jog_om_address.c_ADDR_CODE\n" +
                "        LEFT JOIN app_fd_jog_om_address BuyerAddress ON app_fd_jog_ot_invoice_head.c_INVH_BILL_TO_ADDR_CODE = BuyerAddress.c_ADDR_CODE\n" +
                "        LEFT JOIN app_fd_fm_company ON app_fd_jog_ot_invoice_head.c_INVH_COMP_CODE = app_fd_fm_company.c_COMP_CODE\n" +
                "        WHERE  c_INVH_SYS_ID = ?";
    }

    // Invoice Item Upload
    public static class InvoiceItemIntegration {
        public static void dbCall(String id,String headerUuid, boolean recordUpdated) throws ClassNotFoundException {
            LogUtil.info("Calling Item Code","----");
            int invoiceItemIdentifier = 1;
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            FormDataDao dao = (FormDataDao)AppUtil.getApplicationContext().getBean("formDataDao");
            String tableName = "xml_invoice_lineitm";
            String formId = "e_invoice_xml_invoice_lineitm";
            //String jobId = "#requestParam.work_orderno#";
            Connection con = null;
            FormRowSet rowSet = new FormRowSet();
            rowSet.setMultiRow(true);
            try{
                con = ds.getConnection();
                Map curCodeMap = currCode(con);
                String query = lineItemQuery();
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1,id);
                //stmt.setString(1, "2518");
                ResultSet set = stmt.executeQuery();
                while(set.next()){
                    String headerId = id;
                    String currencyCode = curCodeMap.get(headerId)+"";
                    FormRow row = populateRowFromRecord(set,currencyCode,recordUpdated,headerUuid,invoiceItemIdentifier);
                    rowSet.add(row);
                    invoiceItemIdentifier++;
                }
                dao.saveOrUpdate(formId,tableName,rowSet);
            }
            catch(Exception ex){
                LogUtil.error("Line Item Error",ex,ex.getMessage());
                LogUtil.info("Cause",ex.getCause().toString());
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
            return;
        }
        public static FormRow populateRowFromRecord(ResultSet set,String currencyCode,boolean recordUpdated, String headerUuid, int invoiceItemIdentifier) throws SQLException {
            // Calculate item level discount

            FormRow row = new FormRow();
            insertVat(row,set,currencyCode);
            itemPriceAndDiscountAndNet(row, set, currencyCode);
            row.setProperty("id",processColumn(set.getString("id")));
            //row.setProperty("id","1");
            //row.setProperty("il_identifier",processColumn(set.getString("INVI_SYS_ID")));
            row.setProperty("il_identifier",invoiceItemIdentifier+"");
            if(recordUpdated){
                row.setId(UuidGenerator.uuidGenerator.getUuid());
                row.setProperty("INVI_INVH_SYS_ID",headerUuid);
            }
            else {
                row.setProperty("INVI_INVH_SYS_ID", processColumn(set.getString("INVI_INVH_SYS_ID")));
            }
            row.setProperty("il_quantity",processColumn(set.getString("QTY")));
            //row.setProperty("il_quantity","1");
            row.setProperty("il_uom",processColumn(set.getString("ITEM_UOM")));
            //row.setProperty("c_il_net",processColumn(set.getString(6)));
            row.setProperty("il_item_name",processColumn(set.getString("DESCRIPTION")));
            //row.setProperty("il_stadnard_c_ITEM_CODE",processColumn(set.getString(11)));
            row.setProperty("il_item_buyer_ITEM_CODE",processColumn(set.getString("INVI_ITEM_CODE")));
            row.setProperty("il_item_seller_ITEM_CODE",processColumn(set.getString("INVI_TEM_CODE")));
            row.setDateCreated(new Date());
            return row;
        }
        public static void insertVat(FormRow row, ResultSet set, String currencyCode) throws SQLException{
            double amount = processForDouble(set.getString("Net_Amount"));
            double rate =processForDouble(set.getString("VAT_PERC"));
            double vatAmount = amount * rate /100;
            //double amountWithVat = amount + vatAmount;
            row.setProperty("il_vat_amount",processColumn(set.getString("VAT_AMT")));
            //row.setProperty("il_vat_amount",Double.toString(vatAmount));
            row.setProperty("il_vat_rate",processColumn(set.getString("VAT_PERC")));
            row.setProperty("il_amount_with_vat",processColumn(set.getString("Amount_with_VAT")));
            //row.setProperty("il_amount_with_vat",amountWithVat+"");

            row.setProperty("il_vat_currency",currencyCode);
            row.setProperty("il_amount_with_vat_currency",currencyCode);
            row.setProperty("il_tax_scheme_id",processColumn(set.getString("Tax_scheme")));
            row.setProperty("il_vat_category_code",processColumn(set.getString("VAT_code")));
        }
        public static void chargeInsertion(FormRow row, ResultSet set, String currencyCode) throws SQLException {
            //String chargeIndicator = processColumn(set.getString());
            String chargeIndicator = processColumn("false");
            boolean chargeFlag = Boolean.getBoolean(chargeIndicator);
            if(chargeFlag){
                row.setProperty("il_charge_indicator","true");
                row.setProperty("il_charge_amount_currency",currencyCode);
                row.setProperty("il_charge_base_amount_currency",currencyCode);
                //row.setProperty("il_charge_percentage",processColumn(set.getString(7)));
                //row.setProperty("il_charge_amount",processColumn(set.getString(7)));
                //row.setProperty("il_charge_amount_currency",processColumn(set.getString(7)));
                //row.setProperty("il_charge_base_amount",processColumn(set.getString(7)));
                //row.setProperty("il_charge_base_amount_currency",processColumn(set.getString(7)));
            }
        }
        public static void itemPriceAndDiscountAndNet(FormRow row, ResultSet set,String currencyCode) throws SQLException{
            double unitDiscount = processForDouble(set.getString("item_price_discount"));
            double itemDiscount = processForDouble(set.getString("DISCOUNT"));
            double quantity = processForDouble(set.getString("QTY"));
            double baseQuantity = processForDouble(set.getString("item_price_base_qty"));
            double rate = processForDouble(set.getString("item_gross_price"));
            double itemNetPrice = processForDouble(set.getString("item_net_price"));
            //double lineNetAmount = (( itemNetPrice / baseQuantity ) * quantity - itemDiscount);
            //double lineNetAmount =
            boolean discountGiven = false;
            Double disc = 0.0;
            Double qty = 0.0;
            Double grossRate = 0.0;
            row.setProperty("il_price_base_qty",processColumn(set.getString("QTY")));
            //row.setProperty("il_price_base_qty","1");
            row.setProperty("il_price_base_qty_unit_code",processColumn(set.getString("ITEM_UOM")));
            row.setProperty("il_gross_price",processColumn(set.getString("item_gross_price")));
            row.setProperty("il_gross_price_currency",currencyCode);
            row.setProperty("il_net_price",processColumn(set.getString("item_net_price")));
            row.setProperty("il_price_currency", currencyCode);
            row.setProperty("il_net",processColumn(set.getString("Net_Amount")));
            row.setProperty("il_currency",currencyCode);
            // for line item discount
            row.setProperty("il_allowance_indicator","false");
            row.setProperty("il_allowance_amount",processColumn(set.getString("DISCOUNT")));
            row.setProperty("il_allowance_percentage",processColumn(set.getString("discount_perc")));
            row.setProperty("il_allowance_currency",currencyCode);
            //row.setProperty("il_allowance_base_amount",unitDiscount+"");
            row.setProperty("il_base_amount_currency",currencyCode);
            // if allowance needs to be calculate based on base amount and percentage then
            // allowance = base_amount * percentage / 100;
            /*if(unitDiscount != 0.0) {
                row.setProperty("il_price_allowance_indicator","false");
                row.setProperty("il_price_discount",unitDiscount+"");
                row.setProperty("il_price_discount_currency",currencyCode);
            }*/
            row.setProperty("il_price_allowance_indicator","false");
            row.setProperty("il_price_discount",unitDiscount+"");
            row.setProperty("il_price_discount_currency",currencyCode);
        }
        public static String processColumn(String val) throws SQLException {
            return (val == null ? "" : val);
        }

        public static double processForDouble(String val) throws SQLException{
            double dval = 0.0;
            if(val != null){
                try{
                    dval = Double.parseDouble(val);
                }
                catch(Exception ex){

                }
            }
            return dval;
        }


        public static Map currCode(Connection con) throws SQLException{
            Map headerCurrCodeMap = new HashMap();
            String vatQuery = "SELECT app_fd_jog_ot_invoice_head.c_INVH_CURR_CODE,c_INVH_SYS_ID FROM app_fd_jog_ot_invoice_head";
            PreparedStatement stmt = con.prepareStatement(vatQuery);
            ResultSet set = stmt.executeQuery();
            while(set.next()){
                headerCurrCodeMap.put(set.getString(2),set.getString(1));
            }
            if(stmt!=null && !stmt.isClosed()) {
                stmt.close();
            }
            return headerCurrCodeMap;
        }

        public static String lineItemQuery(){
            return "SELECT c_INVI_SYS_ID AS id, c_INVI_SYS_ID, c_INVI_INVH_SYS_ID,QTY, ITEM_UOM,Net_Amount,'false' discount_indicator,discount_perc,DISCOUNT,VAT_AMT,Amount_with_VAT,DESCRIPTION,c_INVI_ITEM_CODE,RATE-0 item_net_price,   \n" +
                    "            'S' VAT_CODE, VAT_PERC,'VAT' Tax_Scheme,QTY AS item_price_base_qty,ITEM_UOM base_uom,'false' unit_discount_indicator, 0 item_price_discount,RATE as item_gross_price\n" +
                    "            FROM (SELECT\n" +
                    "                            (\n" +
                    "                                    SELECT (c_INVI_QTY_BU/c_IU_MAX_LOOSE_1)*c_IU_CONV_FACTOR FROM app_fd_om_item, app_fd_om_item_uom\n" +
                    "                                    WHERE c_ITEM_CODE  = c_IU_ITEM_CODE AND c_ITEM_UOM_CODE = c_IU_UOM_CODE AND  c_INVI_ITEM_CODE = c_ITEM_CODE\n" +
                    "                            ) QTY,\n" +
                    "                    app_fd_jog_ot_invoice_item.c_INVI_SYS_ID,app_fd_jog_ot_invoice_item.c_INVI_INVH_SYS_ID, app_fd_jog_ot_invoice_item.c_INVI_ITEM_CODE,\n" +
                    "                    app_fd_jog_ot_invoice_item.c_INVI_UOM_CODE AS ITEM_UOM, app_fd_jog_ot_invoice_item.c_INVI_RATE RATE,\n" +
                    "                    app_fd_jog_ot_invoice_item.c_INVI_ITEM_DESC AS DESCRIPTION,\n" +
                    "                    TED.VAT_RATE AS VAT_PERC,\n" +
                    "                    TED.DISC AS DISCOUNT, c_INVI_DISC_PERC discount_perc,\n" +
                    "                    TED.VAT_AMOUNT AS VAT_AMT,\n" +
                    "                    (c_INVI_FC_VAL_AFT_H_DISC * c_INVH_EXGE_RATE + TED.VAT_AMOUNT ) AS Amount_with_VAT,\n" +
                    "                    (c_INVI_FC_VAL_AFT_H_DISC * c_INVH_EXGE_RATE) AS Net_AMOUNT,\n" +
                    "                    app_fd_jog_ot_invoice_item.c_INVI_QTY_BU \n" +
                    "                       \n" +
                    "                      /*(app_fd_jog_ot_invoice_item.c_INVI_FC_VALTED.VAT_AMOUNT-TED.DISC) AS NET_VALUE,  \n" +
                    "                                        (app_fd_jog_ot_invoice_item.c_INVI_FC_VAL-TED.DISC) AS AMT_Without_VAT */\n" +
                    "                    FROM app_fd_jog_ot_invoice_item\n" +
                    "                    JOIN app_fd_jog_ot_invoice_head ON app_fd_jog_ot_invoice_item.c_INVI_INVH_SYS_ID = app_fd_jog_ot_invoice_head.c_INVH_SYS_ID\n" +
                    "                    JOIN\n" +
                    "                            (SELECT CASE WHEN TV.c_ITED_TED_RATE is null THEN 0 ELSE TV.c_ITED_TED_RATE END VAT_RATE, CASE WHEN TV.VAT_AMT IS NULL THEN 0 ELSE TV.VAT_AMT END VAT_AMOUNT,\n" +
                    "                    CASE WHEN TD.c_ITED_LC_AMT IS NULL THEN 0 ELSE TD.c_ITED_LC_AMT END DISC,TV.c_ITED_I_SYS_ID FROM (SELECT TED_VAT.c_ITED_TED_RATE, TED_VAT.c_ITED_LC_AMT VAT_AMT, TED_VAT.c_ITED_I_SYS_ID FROM\n" +
                    "                    app_fd_jog_ot_invoice_ted TED_VAT\n" +
                    "                    WHERE TED_VAT.c_ITED_TED_TYPE_NUM = 1) TV LEFT JOIN\n" +
                    "                    (SELECT  TED_DESC.c_ITED_LC_AMT,TED_DESC.c_ITED_I_SYS_ID FROM\n" +
                    "                            app_fd_jog_ot_invoice_ted TED_DESC\n" +
                    "                            WHERE TED_DESC.c_ITED_TED_TYPE_NUM = 2) TD\n" +
                    "            ON TV.c_ITED_I_SYS_ID = TD.c_ITED_I_SYS_ID\n" +
                    "            UNION\n" +
                    "            SELECT CASE WHEN TV.c_ITED_TED_RATE IS NULL THEN 0 ELSE TV.c_ITED_TED_RATE END VAT_RATE, CASE WHEN TV.VAT_AMT IS NULL THEN 0 ELSE TV.VAT_AMT END VAT_AMOUNT,\n" +
                    "                    CASE WHEN TD.c_ITED_LC_AMT IS NULL  THEN 0 ELSE TD.c_ITED_LC_AMT END DISC,TD.c_ITED_I_SYS_ID FROM (SELECT TED_VAT.c_ITED_TED_RATE, TED_VAT.c_ITED_LC_AMT VAT_AMT, TED_VAT.c_ITED_I_SYS_ID FROM\n" +
                    "                    app_fd_jog_ot_invoice_ted TED_VAT\n" +
                    "                    WHERE TED_VAT.c_ITED_TED_TYPE_NUM = 1) TV RIGHT JOIN\n" +
                    "                    (SELECT  TED_DESC.c_ITED_LC_AMT,TED_DESC.c_ITED_I_SYS_ID FROM\n" +
                    "                            app_fd_jog_ot_invoice_ted TED_DESC\n" +
                    "                            WHERE TED_DESC.c_ITED_TED_TYPE_NUM = 2) TD\n" +
                    "            ON TV.c_ITED_I_SYS_ID = TD.c_ITED_I_SYS_ID  \n" +
                    "                                        ) TED\n" +
                    "            ON app_fd_jog_ot_invoice_item.c_INVI_SYS_ID = TED.c_ITED_I_SYS_ID\n" +
                    "            WHERE app_fd_jog_ot_invoice_item.c_INVI_INVH_SYS_ID = ? \n" +
                    "            ) main";
        }
    }
}
InvoiceHeaderIntegration.dbCall();