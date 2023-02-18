package org.joget.geowatch.test.mokxathon;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class HighestRevenueDept {
    public static String getRevenue() throws SQLException {
        Connection con = null;
        String revenue = "0";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
            con = ds.getConnection();
            String query = "SELECT c_dept FROM app_fd_in_patientbill group by c_dept " +
                    "order by sum(c_total_bill) desc LIMIT 1";
            PreparedStatement stmt = con.prepareStatement(query);
            //stmt.setString(1,"Medicine");
            ResultSet set = stmt.executeQuery();
            set.next();
            revenue = set.getString(1);
            LogUtil.info("Revenue",revenue);
        }
        catch(SQLException ex){
            LogUtil.error("",ex,ex.getMessage());
        }
        finally{
            if(con != null && !con.isClosed()){
                con.close();
            }
        }
        return revenue;
    }
}
return HighestRevenueDept.getDept();
