package org.joget.geowatch.test;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.UuidGenerator;

import javax.lang.model.element.Element;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Iterator;

public class StoreBinderWithRestCall {
    /*public saveGridRows(Element element, FormRowSet rows, FormData formData) {
        String recordId = null;
        Connection con = null;

        try {
            //Get Joget's current datasource configs
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            if(!con.isClosed()) {
                //To generate new record IDs for storing into child table
                UuidGenerator uuid = UuidGenerator.getInstance();
                //Iterate to add new records
                Iterator i= rows.iterator();
                while (i.hasNext()) {
                    FormRow row = (FormRow) i.next();

                    String pId = uuid.getUuid();
                    String gridColumn1 = row.get("gridColumn1");
                    String gridColumn2 = row.get("gridColumn2");
                    String gridColumn3 = row.get("gridColumn3");

                    String insertSql = "INSERT INTO your_table_name (id,gridColumn1,gridColumn2,gridColumn3) VALUES (?,?,?,?);";

                    PreparedStatement stmtInsert = con.prepareStatement(insertSql);

                    stmtInsert.setString(1, pId);
                    stmtInsert.setString(2, gridColumn1);
                    stmtInsert.setString(3, gridColumn2);
                    stmtInsert.setString(4, gridColumn3);

                    //Execute SQL statement
                    stmtInsert.executeUpdate();
                }
            }
        } catch (Exception ex) {
            LogUtil.error("Your App/Plugin Name", ex, "Error storing using jdbc");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error("Your App/Plugin Name", ex, "Error closing the jdbc connection");
            }
        }
    }

    //Process and store grid rows
    saveGridRows(element, rows, formData);*/
}
