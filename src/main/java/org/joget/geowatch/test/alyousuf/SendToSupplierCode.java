
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;

public class SendToSupplierCode {

    public static FormRowSet load(Element element, String username, FormData formData) {
        FormRowSet rows = new FormRowSet();
        Connection con = null;
        LogUtil.info("","Tis is test");
        try {
            // retrieve connection from the default datasource
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            LogUtil.info("","("+AppUtil.getCurrentAppDefinition().getAppId()+") -dbcon."+con);

            // execute SQL query
            if(!con.isClosed()) {

                String selected="#form.swift_request.selected#";
                //selected="#requestParam.status_id#";
                selected = selected.split(";")[0];

                String query = "SELECT bng.id,bng.c_currency,bn.c_exchange_rate,bn.c_payment_due_date,bng.c_supplier,bng.c_catergory,bng.c_sc_sn_no,bng.c_pi_qty,bng.c_pi_total_value,bng.c_product_line,bng.c_country_of_supplier,bng.c_oc_no,bng.c_pi_no,bng.c_business,(select c_bank_name from app_fd_gbft_bank_grid  bg where bg.c_fg=bn.id order by dateCreated desc limit 1) as bank_nominated FROM app_fd_bank_nomination bn INNER JOIN app_fd_bank_nomination_grid bng ON bn.id = bng.c_grid  WHERE   c_request_number = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1,"#form.swift_request.status_id#");
                LogUtil.info("Bank REquest number",selected);
                LogUtil.info("","("+AppUtil.getCurrentAppDefinition().getAppId()+") - Record Insert Successfully."+stmt);

                ResultSet rs = stmt.executeQuery();
                String supplier = "";
                HashSet categorySet = new HashSet();
                String countryOfSupply = "";
                HashSet scSnNo = new HashSet();
                double totalPiValue = 0.0;
                String currency = "";
                String bankName = "";
                while(rs.next()){
                    supplier = rs.getObject("c_supplier") != null? rs.getObject("c_supplier").toString():"";
                    categorySet.add(rs.getObject("c_catergory") != null ? rs.getObject("c_catergory").toString():"");
                    countryOfSupply = rs.getObject("c_country_of_supplier") != null ? rs.getObject("c_country_of_supplier").toString():"";
                    scSnNo.add(rs.getObject("c_sc_sn_no") != null ? rs.getObject("c_sc_sn_no").toString():"");
                    //totalPiValue += rs.getObject("c_pi_total_value") != null? Double.parseDouble(rs.getObject("c_pi_total_value").toString()):0;
                    currency = rs.getObject("c_currency") != null ? rs.getObject("c_currency").toString():"";
                    bankName = rs.getObject("bank_nominated") != null ?rs.getObject("bank_nominated").toString():"";
                }
                StringBuilder category = new StringBuilder();
                for(Object obj : categorySet){
                    category.append(obj.toString()).append("/");
                }
                String catString = "";
                LogUtil.info("","Before category bilder");
                if(category.length() > 0) {
                    category.deleteCharAt(category.length() - 1);
                    catString = category.toString();
                }

                StringBuilder scNo = new StringBuilder();
                for(Object obj : scSnNo) {
                    scNo.append(obj.toString()).append("/");
                }
                String scNoStr = "";
                LogUtil.info("","Before SC bilder");
                if(scNo.length() > 0) {
                    scNo.deleteCharAt(scNo.length() - 1);
                    scNoStr = scNo.toString();
                }

                FormRow row = new FormRow();
                row.setProperty("supplier",supplier);
                row.setProperty("category", catString);
                row.setProperty("country_of_supply",countryOfSupply);
                row.setProperty("sc_sn_no", scNoStr);
                //row.setProperty("total_pi_value",Double.toString(totalPiValue));
                row.setProperty("curr", currency);
                row.setProperty("bankname",bankName );

                /*while (rs.next()) {
                    FormRow row = new FormRow();
                    System.out.println(rs.getObject("id") );
                    LogUtil.info("ID of record" , rs.getObject("id") );
                    ////   row.setId(rs.getObject("id")!=null ? rs.getObject("id").toString():"");
                    row.setProperty("oc_no", (rs.getObject("c_oc_no") != null)?rs.getObject("c_oc_no").toString():"");
                    row.setProperty("product_line", (rs.getObject("c_product_line") != null)?rs.getObject("c_product_line").toString():"");
                    //    row.setProperty("MFG", (rs.getObject("c_mfg") != null)?rs.getObject("c_mfg").toString():"");
                    //	row.setProperty("CATEGORY", (rs.getObject("c_Category") != null)?rs.getObject("c_Category").toString():"");
                    row.setProperty("country_of_supplier", (rs.getObject("c_country_of_supplier") != null)?rs.getObject("c_country_of_supplier").toString():"");
                    //	row.setProperty("ORDER_YEAR", (rs.getObject("c_ORDER_YEAR") != null)?rs.getObject("c_ORDER_YEAR").toString():"");
                    //	row.setProperty("ORDER_MONTH", (rs.getObject("c_ORDER_MONTH") != null)?rs.getObject("c_ORDER_MONTH").toString():"");
                    row.setProperty("pi_no", (rs.getObject("c_pi_no") != null)?rs.getObject("c_pi_no").toString():"");
                    row.setProperty("sc_sn_no", (rs.getObject("c_sc_sn_no") != null)?rs.getObject("c_sc_sn_no").toString():"");
                    row.setProperty("pi_qty", (rs.getObject("c_pi_qty") != null)?rs.getObject("c_pi_qty").toString():"");
                    row.setProperty("pi_total_value", (rs.getObject("c_pi_total_value") != null)?rs.getObject("c_pi_total_value").toString():"");
                    row.setProperty("currency", (rs.getObject("c_currency") != null)?rs.getObject("c_currency").toString():"");
                    row.setProperty("supplier", (rs.getObject("c_supplier") != null)?rs.getObject("c_supplier").toString():"");
                    row.setProperty("payment_due_date", (rs.getObject("c_payment_due_date") != null)?rs.getObject("c_payment_due_date").toString():"");
                    row.setProperty("exchange_rate", (rs.getObject("c_exchange_rate") != null)?rs.getObject("c_exchange_rate").toString():"");
                    row.setProperty("bank_nominated", (rs.getObject("bank_nominated") != null)?rs.getObject("bank_nominated").toString():"");
                    row.setProperty("business", (rs.getObject("c_business") != null)?rs.getObject("c_business").toString():"");
                    String totalValue = (rs.getObject("c_pi_total_value") != null)?rs.getObject("c_pi_total_value").toString():"0";
                    String exchangeRate = (rs.getObject("c_exchange_rate") != null)?rs.getObject("c_exchange_rate").toString():"0";
                    double tV = Double.parseDouble(totalValue.replaceAll(",",""));
                    double eR = Double.parseDouble(exchangeRate.replaceAll(",",""));
                    tV = tV * eR;*/
                //row.setProperty("total_lc_value",Double.toString(tV));
                rows.add(row);
            }
        }
        catch(Exception e) {
            LogUtil.error("Sample app - Form 1", e, "Error loading user data in load binder");
        } finally {
            //always close the connection after used
            try {
                if(con != null) {
                    con.close();
                }
            } catch(SQLException e) {/* ignored */}
        }

        return rows;
    }
}
//call load method with injected variable
return SendToSupplierCode.load(element, primaryKey, formData);

