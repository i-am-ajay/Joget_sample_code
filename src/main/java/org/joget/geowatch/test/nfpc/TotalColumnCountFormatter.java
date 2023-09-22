package org.joget.geowatch.test.nfpc;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joget.apps.form.model.FormRow;
import javax.sql.DataSource;

public class TotalColumnCountFormatter {
    public String format(FormRow row){
        String totalCount = "0";
        String billToCode = (String)row.get("invoice_e_status");
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        try{
            LogUtil.info("RateValidator","Running Query");
            con = ds.getConnection();
            if (!con.isClosed()) {
                //get total count from table.
                String sql = "select count(u1.c_bill_to_code) as count  from app_fd_user_master as u1 where u1.c_bill_to_code=?\n" +
                                "group by u.c_bill_to_code";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, billToCode);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    totalCount = rs.getString(1);
                }
            }
        }
        catch(SQLException ex){
            LogUtil.error("Total Count Not Found",ex,ex.getMessage());
        }
        finally{
            try{
                if(con != null && !con.isClosed()){
                    con.close();
                }
            }
            catch(SQLException ex){
            }
        }
        return totalCount ;
    }
}
