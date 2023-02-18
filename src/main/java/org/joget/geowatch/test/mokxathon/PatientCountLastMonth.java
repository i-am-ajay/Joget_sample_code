package org.joget.geowatch.test.mokxathon;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
public class PatientCountLastMonth {
    public static String getRevenue() throws SQLException {
        Connection con = null;
        String revenue = "0";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        LocalDate date = LocalDate.now();
        try {
            con = ds.getConnection();
            String query = "SELECT count(id) FROM app_fd_patient_appointment where" +
                    " Month(c_admission_date) = ?";
            String month = Integer.toString(date.getMonth().getValue());
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1,month);
            ResultSet set = stmt.executeQuery();
            set.next();
            revenue = set.getString(1);
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
return PatientCountLastMonth.getRevenue();
