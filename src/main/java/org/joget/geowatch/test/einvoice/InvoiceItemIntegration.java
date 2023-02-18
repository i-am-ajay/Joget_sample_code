package org.joget.geowatch.test.einvoice;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class InvoiceItemIntegration {
    public static void dbCall() throws ClassNotFoundException {
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
            //stmt.setString(1, "2518");
            ResultSet set = stmt.executeQuery();
            while(set.next()){
                String headerId = set.getString(3);
                String currencyCode = curCodeMap.get(headerId)+"";
                FormRow row = populateRowFromRecord(set,currencyCode);
                rowSet.add(row);
            }
            dao.saveOrUpdate(formId,tableName,rowSet);
        }
        catch(Exception ex){
            LogUtil.error("isReservedStockAvailabe",ex,ex.getMessage());
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
    public static FormRow populateRowFromRecord(ResultSet set,String currencyCode) throws SQLException {
        // Calculate item level discount

        FormRow row = new FormRow();
        insertVat(row,set,currencyCode);
        itemPriceAndDiscountAndNet(row, set, currencyCode);
        row.setProperty("id",processColumn(set.getString(1)));
        row.setProperty("il_identifier",processColumn(set.getString(2)));
        row.setProperty("INVI_INVH_SYS_ID",processColumn(set.getString(3)));
        row.setProperty("il_quantity",processColumn(set.getString(4)));
        row.setProperty("il_uom",processColumn(set.getString(5)));
        //row.setProperty("c_il_net",processColumn(set.getString(6)));
        row.setProperty("il_item_name",processColumn(set.getString(10)));
        row.setProperty("il_stadnard_item_code",processColumn(set.getString(11)));
        //row.setProperty("il_item_buyer_item_code",processColumn(set.getString(11)));
        //row.setProperty("il_item_seller_item_code",processColumn(set.getString(11)));
        row.setDateCreated(new Date());
        return row;
    }
    public static void insertVat(FormRow row, ResultSet set, String currencyCode) throws SQLException{
        row.setProperty("il_vat_amount",processColumn(set.getString(8)));
        row.setProperty("il_vat_rate",processColumn(set.getString(12)));
        row.setProperty("il_amount_with_vat",processColumn(set.getString(9)));
        row.setProperty("il_vat_currency",currencyCode);
        row.setProperty("il_amount_with_vat_currency",currencyCode);
        row.setProperty("il_tax_scheme_id",processColumn(set.getString(13)));
        //row.setProperty("il_vat_category_code",processColumn(set.getString(13)));
    }
    public static void chargeInsertion(FormRow row, ResultSet set, String currencyCode) throws SQLException {
        String chargeIndicator = processColumn(set.getString(9));
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
        else{
            row.setProperty("il_charge_indicator","false");
        }
    }
    public static void itemPriceAndDiscountAndNet(FormRow row, ResultSet set,String currencyCode) throws SQLException{
        double unitDiscount = processForDouble(set.getString(16));
        double itemDiscount = processForDouble(set.getString(7));
        double quantity = processForDouble(set.getString(4));
        double baseQuantity = processForDouble(set.getString(18));
        double rate = processForDouble(set.getString(15));
        double itemNetPrice = processForDouble(set.getString(17));
        double lineNetAmount = (( itemNetPrice / baseQuantity ) * quantity - itemDiscount);
        boolean discountGiven = false;
        Double disc = 0.0;
        Double qty = 0.0;
        Double grossRate = 0.0;
        row.setProperty("il_price_base_qty",baseQuantity+"");
        row.setProperty("il_price_base_qty_unit_code",processColumn(set.getString(5)));
        row.setProperty("il_gross_price",processColumn(set.getString(15)));
        row.setProperty("il_gross_price_currency",currencyCode);
        row.setProperty("il_net_price",itemNetPrice+"");
        row.setProperty("il_price_currency", currencyCode);
        row.setProperty("il_net",lineNetAmount+"");
        row.setProperty("il_currency",currencyCode);
        // for line item discount
        if(itemDiscount != 0.0){
            row.setProperty("il_allowance_indicator","true");
            row.setProperty("il_allowance_amount",processColumn(set.getString(7)));
            //row.setProperty("il_allowance_percentage",processColumn(set.getString(7)));
            row.setProperty("il_allowance_currency",currencyCode);
            //row.setProperty("il_allowance_base_amount",unitDiscount+"");
            row.setProperty("il_base_amount_currency",currencyCode);
            // if allowance needs to be calculate based on base amount and percentage then
            // allowance = base_amount * percentage / 100;
        }
        else {
            row.setProperty("il_allowance_indicator","false");
        }
        if(unitDiscount != 0.0) {
            row.setProperty("il_price_allowance_indicator","true");
            row.setProperty("il_price_discount",unitDiscount+"");
            row.setProperty("il_price_discount_currency",currencyCode);
            // calculate unit discount
        }
        else {
            row.setProperty("il_price_allowance_indicator","false");
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


    public static Map currCode(Connection con) throws SQLException{
        Map headerCurrCodeMap = new HashMap();
        String vatQuery = "SELECT JOG_OT_INVOICE_HEAD.INVH_CURR_CODE,INVH_SYS_ID FROM JOG_OT_INVOICE_HEAD";
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
        return "SELECT INVI_SYS_ID AS id, INVI_SYS_ID, INVI_INVH_SYS_ID,QTY, INVI_UOM_CODE,AMT_Without_VAT,DISCOUNT,VAT_AMT,NET_VALUE,DESCRIPTION,INVI_ITEM_CODE,VAT_PERC,'VAT',ITEM_UOM,RATE, " +
                "0 as item_discount, RATE-0 as item_net_price, 1 as item_price_base_qty\n" +
                "   FROM (SELECT\n" +
                "\tJOG_OT_INVOICE_ITEM.INVI_SYS_ID,JOG_OT_INVOICE_ITEM.INVI_INVH_SYS_ID, JOG_OT_INVOICE_ITEM.INVI_ITEM_CODE,\n" +
                "\tJOG_OT_INVOICE_ITEM.INVI_UOM_CODE,JOG_OT_INVOICE_ITEM.INVI_UOM_CODE AS ITEM_UOM, JOG_OT_INVOICE_ITEM.INVI_RATE RATE,\n" +
                "\tJOG_OT_INVOICE_ITEM.INVI_ITEM_DESC AS DESCRIPTION, \n" +
                "\tF_PACK_AND_LOOSE_QTY_WITH_FMT(\n" +
                "\t\t\tJOG_OT_INVOICE_ITEM.INVI_ITEM_CODE, \n" +
                "\t\t\tJOG_OT_INVOICE_ITEM.INVI_UOM_CODE, \n" +
                "\t\t\tJOG_OT_INVOICE_ITEM.INVI_QTY_BU) AS QTY,\n" +
                "TED.VAT_RATE AS VAT_PERC,\n" +
                "TED.DISC AS DISCOUNT,\n" +
                "TED.VAT_AMOUNT AS VAT_AMT,\n" +
                "(JOG_OT_INVOICE_ITEM.INVI_FC_VAL+TED.VAT_AMOUNT-TED.DISC) AS NET_VALUE,\n" +
                "(JOG_OT_INVOICE_ITEM.INVI_FC_VAL-TED.DISC) AS AMT_Without_VAT\n" +
                "FROM JOG_OT_INVOICE_ITEM \n" +
                "JOIN \n" +
                "(SELECT CASE WHEN TV.ITED_TED_RATE is null THEN 0 ELSE TV.ITED_TED_RATE END VAT_RATE, CASE WHEN TV.VAT_AMT IS NULL THEN 0 ELSE TV.VAT_AMT END VAT_AMOUNT,\n" +
                "CASE WHEN TD.ITED_FC_AMT IS NULL THEN 0 ELSE TD.ITED_FC_AMT END DISC,TV.ITED_I_SYS_ID FROM (SELECT TED_VAT.ITED_TED_RATE, TED_VAT.ITED_FC_AMT VAT_AMT, TED_VAT.ITED_I_SYS_ID FROM \n" +
                "\tJOG_OT_INVOICE_ITEM_TED TED_VAT\n" +
                "\tWHERE TED_VAT.ITED_TED_TYPE_NUM = 1) TV LEFT JOIN\n" +
                "(SELECT  TED_DESC.ITED_FC_AMT,TED_DESC.ITED_I_SYS_ID FROM \n" +
                "\tJOG_OT_INVOICE_ITEM_TED TED_DESC\n" +
                "\tWHERE TED_DESC.ITED_TED_TYPE_NUM = 2) TD\n" +
                "    ON TV.ITED_I_SYS_ID = TD.ITED_I_SYS_ID\n" +
                "UNION\n" +
                "SELECT CASE WHEN TV.ITED_TED_RATE IS NULL THEN 0 ELSE TV.ITED_TED_RATE END VAT_RATE, CASE WHEN TV.VAT_AMT IS NULL THEN 0 ELSE TV.VAT_AMT END VAT_AMOUNT,\n" +
                "CASE WHEN TD.ITED_FC_AMT IS NULL  THEN 0 ELSE TD.ITED_FC_AMT END DISC,TD.ITED_I_SYS_ID FROM (SELECT TED_VAT.ITED_TED_RATE, TED_VAT.ITED_FC_AMT VAT_AMT, TED_VAT.ITED_I_SYS_ID FROM \n" +
                "\tJOG_OT_INVOICE_ITEM_TED TED_VAT\n" +
                "\tWHERE TED_VAT.ITED_TED_TYPE_NUM = 1) TV RIGHT JOIN\n" +
                "(SELECT  TED_DESC.ITED_FC_AMT,TED_DESC.ITED_I_SYS_ID FROM \n" +
                "\tJOG_OT_INVOICE_ITEM_TED TED_DESC\n" +
                "\tWHERE TED_DESC.ITED_TED_TYPE_NUM = 2) TD\n" +
                "    ON TV.ITED_I_SYS_ID = TD.ITED_I_SYS_ID\n" +
                ") TED \n" +
                "ON JOG_OT_INVOICE_ITEM.INVI_SYS_ID = TED.ITED_I_SYS_ID\n" +
                ")";
    }
}
return InvoiceItemIntegration.dbCall();
