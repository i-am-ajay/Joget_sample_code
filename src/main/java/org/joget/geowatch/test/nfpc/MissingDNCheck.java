package org.joget.geowatch.test.nfpc;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MissingDNCheck {
    static boolean status = false;
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        Connection con = null;
        Set invoiceSet = null;
        try {
            // retrieve connection from the default datasource
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            // execute SQL query
            if (!con.isClosed()) {
                invoiceSet = MissingDNCheck.getInvoices(con);
                PreparedStatement stmt = null;

                String query = "SELECT * from app_fd_delivery_noteinvoice note_inv LEFT JOIN app_fd_delivery_note dn ON dn.c_doc_no = note_inv.c_doc_no  where note_inv.c_invoice_no = ? and (dn.c_dn_file is NULL OR dn.c_dn_file = '')";
                LogUtil.info("Size of ResultSet",""+invoiceSet.size());
                int counter = 0;
                for (Object invoice_no : invoiceSet) {

                    if(counter >= 20 || status == true ){
                        break;
                    }
                    counter += 1;
                    stmt = con.prepareStatement(query);
                    stmt.setString(1, invoice_no.toString());
                    ResultSet dnResultSet = stmt.executeQuery();
                    if (dnResultSet.next()) {
                        LogUtil.info(""+counter,"Result Set has missing records"+invoice_no);
                    }
                    else{
                        LogUtil.info("Counter",""+counter);
                        updateInvoice(con,invoice_no.toString());
                    }
                }
                // update rank in db.

            }
        } catch (Exception e) {
            LogUtil.error("Missing DN Check Error", e, e.getMessage());
        } finally {
            //always close the connection after used
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {

            }
        }
        return null;
    }
    public static Set getInvoices(Connection con) throws SQLException {
        Set invoiceSet = new HashSet();
        LocalDateTime time = LocalDateTime.now();
        //time = time.minusHours(2); // change with environment variable
        time = time.minusDays(10);
        LogUtil.info("Envrionment Variable for time variance","#envVariable.hour_difference#");

        String query = "SELECT invoice.id from app_fd_delivery_note dn \n" +
                "INNER JOIN app_fd_delivery_noteinvoice note_inv ON dn.c_doc_no = note_inv.c_doc_no \n" +
                "INNER JOIN app_fd_invoice invoice ON invoice.id= note_inv.c_invoice_no " +
                "where invoice.c_invoice_status = ?" +
                "and dn.dateCreated >= ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1,"Not Matched");
        stmt.setTimestamp(2, Timestamp.valueOf(time));
        //stmt.setObject(1, "BID-0117");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            invoiceSet.add(rs.getString(1));
        }
        stmt.close();

        return invoiceSet;
    }

    public static void updateInvoice(Connection con, String invoiceId ) throws SQLException {
        //status = true;
        LogUtil.info("Record for Update",""+invoiceId);
        String query = "Update app_fd_invoice set c_invoice_status = ? WHERE id = ?";
        //String query = "Select c_invoice_status from app_fd_invoice WHERE id = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1,"Matched");
        stmt.setString(2,invoiceId);
        stmt.executeUpdate();
       /* ResultSet set = stmt.executeQuery();
        while(set.next()){
            LogUtil.info("Id of Invoice"+set.getString(1),"Status of Invoice "+set.getString(2));
        }*/
        stmt.close();
    }
}
MissingDNCheck.execute(workflowAssignment, appDef, request);
