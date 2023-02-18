package org.joget.geowatch.test.einvoice;

import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.workflow.model.service.WorkflowManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class JobIntegration {
    static volatile String globalProcessStatus = "";
    static String currencyCode = null;
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

        String id = "#requestParam.id#";
        String companyId = "#requestParam.companyId#";
        String uuid = "#requestParam.uuid#";
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
        FormRow oldRow = dao.load(formId, tableName, "J_"+id);

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
                    wm.activityVariable("#assignment.activityId#","invoice_id", headerId);
                }
                if(!isWorkflowVariableSet) {
                    wm.activityVariable("#assignment.activityId#", "uuid", "#requestParam.uuid#");
                    wm.activityVariable("#assignment.activityId#", "companyId", "#requestParam.companyId#");
                }


                currencyCode = processColumn(set.getString(56));
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
            JobIntegration.InvoiceItemIntegration.dbCall(id,headerId,recordUpdated,currencyCode);
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

        //row.setProperty("Invoice_total_vat_amount_accounting_currency",processColumn(set.getString(13)));

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
        row.setProperty("document_allowance_indicator",processColumn(set.getString(46))); // true or false if it's charge and false for discount
        row.setProperty("document_allowance_perc",processColumn(set.getString(47)));
        row.setProperty("document_allowance_wo_vat",processColumn(set.getString(48))); // allowance amount without vat
        row.setProperty("document_allowance_currency",currencyCode);
        row.setProperty("document_allowance_base_amount",processColumn(set.getString(49)));
        row.setProperty("document_allowance_base_amount_currency",currencyCode);
        row.setProperty("document_allowance_vat_code",processColumn(set.getString(50))); // what vat amount is applied on document allowance
        row.setProperty("document_allowance_vat_rate",processColumn(set.getString(51)));
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
        row.setProperty("invoice_total_vat_accounting_currency",currencyCode);
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
        return "SELECT 'J_'||JIH_SYS_ID as header_id, 'reporting:1.0' as process_control,JIH_COMP_CODE || '/' || JIH_TXN_CODE || '/' || JIH_NO as invoice_identifier,  \n" +
                "                JIH_UUID as UUID, JIH_DT as issue_date,JIH_TYPE_CODE type_code, \n" +
                "                JIH_TXN_CODE_STRING as transaction_code,JIH_ANNOTATION as notes,'' special_tax_treatment, \n" +
                "                JIH_FLEX_14 purchase_order, '' as billing_ref_id, '' billing_ref_date,  \n" +
                "                JIH_FLEX_14 as contract_id, JIH_NO invoice_counter,'' as seller_group_vat,  \n" +
                "                FM_COMPANY.COMP_FLEX_08 as other_seller_id, JOG_OM_ADDRESS.ADDR_LINE_2 seller_street_address, '' as additional_seller_street,  \n" +
                "                JOG_OM_ADDRESS.ADDR_FLEX_15 seller_building_number, JOG_OM_ADDRESS.ADDR_CITY_CODE seller_city, JOG_OM_ADDRESS.ADDR_ZIP_POSTAL_CODE seller_postal, \n" +
                "                JOG_OM_ADDRESS.ADDR_EMAIL seller_email,JOG_OM_ADDRESS.ADDR_PROVINCE_CODE seller_state,JOG_OM_ADDRESS.ADDR_FLEX_16 seller_district, \n" +
                "                JOG_OM_ADDRESS.ADDR_COUNTRY_CODE seller_country,FM_COMPANY.COMP_FLEX_02 seller_vat_number, FM_COMPANY.COMP_NAME seller_name,  \n" +
                "                '' as buyer_group_vat, BuyerAddress.ADDR_FLEX_20 other_buyer_id,BuyerAddress.ADDR_LINE_2 buyer_street,  \n" +
                "                '' as buyer_additional_stree, BuyerAddress.ADDR_FLEX_15 buyer_building_number, '' as buyer_additional_number, \n" +
                "                BuyerAddress.ADDR_CITY_CODE buyer_city, BuyerAddress.ADDR_ZIP_POSTAL_CODE buyer_postal_code,BuyerAddress.ADDR_PROVINCE_CODE buyer_state,  \n" +
                "                BuyerAddress.ADDR_FLEX_16 buyer_district, BuyerAddress.ADDR_COUNTRY_CODE buyer_country, BuyerAddress.ADDR_FLEX_20 buyer_vat, \n" +
                "                JIH_CUST_NAME buyer_name, JOG_OT_JOB_INVOICE_HEAD.JIH_FLEX_08 as supply_date, JOG_OT_JOB_INVOICE_HEAD.JIH_FLEX_08 as supply_end_date,  \n" +
                "                'Instrument not defined' as payment_means_type_code, '' as dc_note_reason, JIH_TERM_CODE as payment_terms, \n" +
                "                'false' allowance_indication, JIH_DISC_PERC disc_per, JIH_FC_DISC_VAL * JIH_EXGE_RATE disc_val,  \n" +
                "                '0.0' as base_amount, 'S' as allowance_vat_code, (SELECT DISTINCT ITED_TED_RATE FROM JOG_OT_JOB_INVOICE_ITEM_TED WHERE ITED_H_SYS_ID = JOG_OT_JOB_INVOICE_HEAD.JIH_SYS_ID AND ITED_TED_TYPE_NUM = 1) as allowance_vat_rate,  \n" +
                "                'VAT' as tax_scheme, (SELECT SUM(JII_ADVANCE_DEDN_LC_AMT) FROM JOG_OT_JOB_INVOICE_DETAIL WHERE JII_JIH_SYS_ID = JIH_SYS_ID) advance_paid_amount, '' as vat_exemption_code, \n" +
                "                '' as vat_exemption_reason,JIH_CURR_CODE currency_code, JIH_COMP_CODE as company_code,  \n" +
                "                'TIN' as buyer_id_scheme, FM_COMPANY.COMP_FLEX_07 as other_seller_id_scheme, '' seller_additional_number\n" +
                "                 FROM  \n" +
                "                JOG_OT_JOB_INVOICE_HEAD \n" +
                "                LEFT JOIN JOG_OM_ADDRESS ON JOG_OT_JOB_INVOICE_HEAD.JIH_ADDR_CODE = JOG_OM_ADDRESS.ADDR_CODE  \n" +
                "                LEFT JOIN JOG_OM_ADDRESS BuyerAddress ON JOG_OT_JOB_INVOICE_HEAD.JIH_COMP_CODE = BuyerAddress.ADDR_CODE  \n" +
                "                LEFT JOIN FM_COMPANY ON JOG_OT_JOB_INVOICE_HEAD.JIH_COMP_CODE = FM_COMPANY.COMP_CODE \n" +
                "                 WHERE  JIH_SYS_ID = ?\n";
    }

    // Invoice Item Upload
    public static class InvoiceItemIntegration {
        public static void dbCall(String id,String headerUuid, boolean recordUpdated, String currencyCode) throws ClassNotFoundException {
            LogUtil.info("Calling Item Code","----");
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
                String query = lineItemQuery();
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1,id);
                //stmt.setString(1, "2518");
                ResultSet set = stmt.executeQuery();
                while(set.next()){
                    String headerId = id;
                    FormRow row = populateRowFromRecord(set,currencyCode,recordUpdated,headerUuid);
                    rowSet.add(row);
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
        public static FormRow populateRowFromRecord(ResultSet set,String currencyCode,boolean recordUpdated, String headerUuid) throws SQLException {
            // Calculate item level discount
            FormRow row = new FormRow();
            insertVat(row,set,currencyCode);
            itemPriceAndDiscountAndNet(row, set, currencyCode);
            row.setProperty("id",processColumn(set.getString("id")));
            row.setProperty("il_identifier",processColumn(set.getString("id")));
            if(recordUpdated){
                row.setId(UuidGenerator.uuidGenerator.getUuid());
                row.setProperty("INVI_INVH_SYS_ID",headerUuid);
            }
            else {
                row.setProperty("INVI_INVH_SYS_ID", processColumn(set.getString("HEADER_ID")));
            }
            //row.setProperty("il_quantity",processColumn(set.getString("QTY")));
            row.setProperty("il_quantity","1");
            row.setProperty("il_uom",processColumn(set.getString("ITEM_UOM")));
            //row.setProperty("c_il_net",processColumn(set.getString(6)));
            row.setProperty("il_item_name",processColumn(set.getString("DESCRIPTION")));
            //row.setProperty("il_stadnard_item_code",processColumn(set.getString(11)));
            row.setProperty("il_item_buyer_item_code",processColumn(set.getString("JII_ACTH_CODE")));
            row.setProperty("il_item_seller_item_code",processColumn(set.getString("JII_ACTH_CODE")));
            row.setDateCreated(new Date());
            return row;
        }
        public static void insertVat(FormRow row, ResultSet set, String currencyCode) throws SQLException{
            double amount = processForDouble(set.getString("Net_Amount"));
            double rate =processForDouble(set.getString("VAT_PERC"));
            double vatAmount = amount * rate /100;
            //row.setProperty("il_vat_amount",processColumn(set.getString("VAT_AMT")));
            row.setProperty("il_vat_amount",Double.toString(vatAmount));
            row.setProperty("il_vat_rate",processColumn(set.getString("VAT_PERC")));
            row.setProperty("il_amount_with_vat",processColumn(set.getString("Amount_with_VAT")));
            row.setProperty("il_vat_currency",currencyCode);
            row.setProperty("il_amount_with_vat_currency",currencyCode);
            row.setProperty("il_tax_scheme_id",processColumn(set.getString("VAT_CODE")));
            //row.setProperty("il_vat_category_code",processColumn(set.getString(13)));
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
            row.setProperty("il_price_base_qty",baseQuantity+"");
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
            if(unitDiscount != 0.0) {
                row.setProperty("il_price_allowance_indicator","false");
                row.setProperty("il_price_discount",unitDiscount+"");
                row.setProperty("il_price_discount_currency",currencyCode);
            }
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


        public static String lineItemQuery(){
            return "SELECT 'J_' || JII_SYS_ID AS id, JII_SYS_ID, 'J_' || JII_JIH_SYS_ID HEADER_ID,QTY, ITEM_UOM,Net_Amount,'false' discount_indicator,discount_perc,DISCOUNT,VAT_AMT,Amount_with_VAT,DESCRIPTION,JII_ACTH_CODE,RATE-0 item_net_price,  \n" +
                    "                     'S' VAT_CODE, VAT_PERC,'VAT',QTY AS item_price_base_qty,ITEM_UOM base_uom,'false' unit_discount_indicator, 0 item_price_discount,RATE as item_gross_price \n" +
                    "                       FROM (SELECT\n" +
                    "                    (\n" +
                    "                           SELECT (JII_QTY/IU_MAX_LOOSE_1)*IU_CONV_FACTOR FROM OM_ITEM, OM_ITEM_UOM \n" +
                    "                            WHERE ITEM_CODE  = IU_ITEM_CODE AND ITEM_UOM_CODE = IU_UOM_CODE AND  JII_ACTH_CODE = ITEM_CODE\n" +
                    "                          ) QTY,  \n" +
                    "\n" +
                    "                    JOG_OT_JOB_INVOICE_DETAIL.JII_SYS_ID,JOG_OT_JOB_INVOICE_DETAIL.JII_JIH_SYS_ID, JOG_OT_JOB_INVOICE_DETAIL.JII_ACTH_CODE, \n" +
                    "                    JOG_OT_JOB_INVOICE_DETAIL.JII_FLEX_02 AS ITEM_UOM, '' RATE, \n" +
                    "                    JOG_OT_JOB_INVOICE_DETAIL.JII_FLEX_03 AS DESCRIPTION,   \n" +
                    "                    TED.VAT_RATE AS VAT_PERC, \n" +
                    "                    TED.DISC AS DISCOUNT, '' discount_perc,\n" +
                    "                    TED.VAT_AMOUNT AS VAT_AMT,\n" +
                    "                    (JII_SALE_FC_VAL_AFT_H_DISC * JIH_EXGE_RATE + TED.VAT_AMOUNT ) AS Amount_with_VAT,\n" +
                    "                    (JII_SALE_FC_VAL_AFT_H_DISC * JIH_EXGE_RATE) AS Net_AMOUNT,\n" +
                    "                          JOG_OT_JOB_INVOICE_DETAIL.JII_QTY\n" +
                    "\n" +
                    "                   /*(JOG_OT_INVOICE_ITEM.INVI_FC_VAL+TED.VAT_AMOUNT-TED.DISC) AS NET_VALUE, \n" +
                    "                    (JOG_OT_INVOICE_ITEM.INVI_FC_VAL-TED.DISC) AS AMT_Without_VAT */\n" +
                    "\n" +
                    "                    FROM JOG_OT_JOB_INVOICE_DETAIL\n" +
                    "                    JOIN JOG_OT_JOB_INVOICE_HEAD ON JOG_OT_JOB_INVOICE_DETAIL.JII_JIH_SYS_ID = JOG_OT_JOB_INVOICE_HEAD.JIH_SYS_ID\n" +
                    "                    JOIN  \n" +
                    "                    (SELECT CASE WHEN TV.ITED_TED_RATE is null THEN 0 ELSE TV.ITED_TED_RATE END VAT_RATE, CASE WHEN TV.VAT_AMT IS NULL THEN 0 ELSE TV.VAT_AMT END VAT_AMOUNT, \n" +
                    "                    CASE WHEN TD.ITED_LC_AMT IS NULL THEN 0 ELSE TD.ITED_LC_AMT END DISC,TV.ITED_I_SYS_ID FROM (SELECT TED_VAT.ITED_TED_RATE, TED_VAT.ITED_LC_AMT VAT_AMT, TED_VAT.ITED_I_SYS_ID FROM  \n" +
                    "                    JOG_OT_JOB_INVOICE_ITEM_TED TED_VAT \n" +
                    "                    WHERE TED_VAT.ITED_TED_TYPE_NUM = 1) TV LEFT JOIN \n" +
                    "                    (SELECT  TED_DESC.ITED_LC_AMT,TED_DESC.ITED_I_SYS_ID FROM  \n" +
                    "                    JOG_OT_JOB_INVOICE_ITEM_TED TED_DESC \n" +
                    "                    WHERE TED_DESC.ITED_TED_TYPE_NUM = 2) TD \n" +
                    "                        ON TV.ITED_I_SYS_ID = TD.ITED_I_SYS_ID \n" +
                    "                   \n" +
                    "                    UNION \n" +
                    "                    SELECT CASE WHEN TV.ITED_TED_RATE IS NULL THEN 0 ELSE TV.ITED_TED_RATE END VAT_RATE, CASE WHEN TV.VAT_AMT IS NULL THEN 0 ELSE TV.VAT_AMT END VAT_AMOUNT, \n" +
                    "                    CASE WHEN TD.ITED_LC_AMT IS NULL  THEN 0 ELSE TD.ITED_LC_AMT END DISC,TD.ITED_I_SYS_ID FROM (SELECT TED_VAT.ITED_TED_RATE, TED_VAT.ITED_LC_AMT VAT_AMT, TED_VAT.ITED_I_SYS_ID FROM  \n" +
                    "                    JOG_OT_JOB_INVOICE_ITEM_TED TED_VAT \n" +
                    "                    WHERE TED_VAT.ITED_TED_TYPE_NUM = 1) TV RIGHT JOIN \n" +
                    "                    (SELECT  TED_DESC.ITED_LC_AMT,TED_DESC.ITED_I_SYS_ID FROM  \n" +
                    "                    JOG_OT_JOB_INVOICE_ITEM_TED TED_DESC \n" +
                    "                    WHERE TED_DESC.ITED_TED_TYPE_NUM = 2) TD \n" +
                    "                        ON TV.ITED_I_SYS_ID = TD.ITED_I_SYS_ID \n" +
                    "                    ) TED  \n" +
                    "                    ON JOG_OT_JOB_INVOICE_DETAIL.JII_SYS_ID = TED.ITED_I_SYS_ID" +
                    " WHERE JOG_OT_JOB_INVOICE_DETAIL.JII_JIH_SYS_ID = ?) main";
        }
    }
}
return JobIntegration.dbCall();
