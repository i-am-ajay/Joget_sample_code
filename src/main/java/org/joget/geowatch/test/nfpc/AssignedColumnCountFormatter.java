package org.joget.geowatch.test.nfpc;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRow;
import org.joget.commons.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssignedColumnCountFormatter {
    public String format(){
        String assignCount = "0";
        String totalCount = "0";

        String billToCode = (String)row.get("invoice_e_status");
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        try{
            LogUtil.info("RateValidator","Running Query");
            con = ds.getConnection();
            if (!con.isClosed()) {
                // find total count

                String sql = "select count(u1.c_bill_to_code) as con from app_fd_user_master as u1 where u1.c_bill_to_code=? group by u1.c_bill_to_code";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, billToCode);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    LogUtil.info("TotalCount rs next","Running Query");

                totalCount = rs.getString(1);
                }
                stmt.close();
                //get total count from table.
                sql = "select count(ac.c_bill_to_code) as coun from app_fd_create_parent_acc as ac join app_fd_association as aa on aa.c_bill_to_code=ac.c_bill_to_code where ac.c_bill_to_code=?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, billToCode);
                rs = stmt.executeQuery();
                if(rs.next()){
                    assignCount = rs.getString(1);
                }
            }
            int totalCounti = 0;
            int assignCounti = 0;

            if(!totalCount.equals("0") && !assignCount.equals("0")){
                totalCounti = Integer.valueOf(totalCount);
                assignCounti = Integer.valueOf(assignCount);
                assignCount = Integer.toString(totalCounti - assignCounti);
            }
        }
        catch(SQLException ex){
            LogUtil.error("Assign Count Not Found",ex,ex.getMessage());
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
        return assignCount ;
    }
}
