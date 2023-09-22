package org.joget.geowatch.test.alyaumi;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.commons.util.LogUtil;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddLocationAndPayscale {
    public static void addData(){
        String id = "#form.meta_data.id#";
        String location = "#form.meta_data.location#";
        String payScale = "#form.meta_data.payscale#";
        Connection con = null;
        try {
            DataSource dao = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = dao.getConnection();
            createOrUpdateLocation(con,location,id);
            createOrUpdatePaysacle(con,payScale,id);
        }
        catch(Exception ex){
            LogUtil.error("DB connection thrown an error",ex,ex.getMessage());
        }
        finally{
            try {
                if (con != null && !con.isClosed()){
                    con.close();
                }
            }
            catch(SQLException ex){

            }
        }

    }
    public static void createOrUpdateLocation(Connection con, String location, String userId) throws SQLException {
        String key = "location";
        String findLocation = "Select * from dir_user_meta where username = ? and meta_key=?";
        LogUtil.info("UserId",userId);
        PreparedStatement fetchStatement = con.prepareStatement(findLocation);
        fetchStatement.setString(1,userId);
        fetchStatement.setString(2,key);
        ResultSet fetchSet = fetchStatement.executeQuery();
        PreparedStatement executeStatement = null;
        if(fetchSet != null && fetchSet.next()){
            LogUtil.info("Location exists",location);
            String updateLocation = "Update dir_user_meta Set meta_value = ? Where username = ? and meta_key = ?";
            executeStatement = con.prepareStatement(updateLocation);
            executeStatement.setString(1,location);
            executeStatement.setString(2,userId);
            executeStatement.setString(3,"location");
        }
        else{
            LogUtil.info("New Location","-------------");
            String insertLocation = "INSERT INTO dir_user_meta VALUES (?,?,?)";
            executeStatement = con.prepareStatement(insertLocation);
            executeStatement.setString(1,userId);
            executeStatement.setString(2,"location");
            executeStatement.setString(3,location);
        }
        executeStatement.executeUpdate();
    }
    public static void createOrUpdatePaysacle(Connection con, String payscale, String userId) throws SQLException {
        String findPayscale = "Select * from dir_user_meta where username = ? and meta_key=?";
        PreparedStatement fetchStatement = con.prepareStatement(findPayscale);
        fetchStatement.setString(1,userId);
        fetchStatement.setString(2,"payscale");
        //fetchStatement.setString(1,location);
        ResultSet fetchSet = fetchStatement.executeQuery();
        PreparedStatement executeStatement = null;
        if(fetchSet != null && fetchSet.next()){
            String updateLocation = "Update dir_user_meta Set meta_value = ? Where username = ? and meta_key = ?";
            executeStatement = con.prepareStatement(updateLocation);
            executeStatement.setString(1,payscale);
            executeStatement.setString(2,userId);
            executeStatement.setString(3,"payscale");
        }
        else{
            String insertLocation = "INSERT INTO dir_user_meta VALUES (?,?,?)";
            executeStatement = con.prepareStatement(insertLocation);
            executeStatement.setString(1,userId);
            executeStatement.setString(2,"payscale");
            executeStatement.setString(3,payscale);
        }
        executeStatement.executeUpdate();
    }
    // to add or update category type
    public static void createOrUpdateCategoryType(Connection con, String categoryType, String userId) throws SQLException {
        String findPayscale = "Select * from dir_user_meta where username = ? and meta_key=?";
        PreparedStatement fetchStatement = con.prepareStatement(findPayscale);
        fetchStatement.setString(1,userId);
        fetchStatement.setString(2,"customer_type");
        //fetchStatement.setString(1,location);
        ResultSet fetchSet = fetchStatement.executeQuery();
        PreparedStatement executeStatement = null;
        if(fetchSet != null && fetchSet.next()){
            String updateLocation = "Update dir_user_meta Set meta_value = ? Where username = ? and meta_key = ?";
            executeStatement = con.prepareStatement(updateLocation);
            executeStatement.setString(1,categoryType);
            executeStatement.setString(2,userId);
            executeStatement.setString(3,"payscale");
        }
        else{
            String insertLocation = "INSERT INTO dir_user_meta VALUES (?,?,?)";
            executeStatement = con.prepareStatement(insertLocation);
            executeStatement.setString(1,userId);
            executeStatement.setString(2,"payscale");
            executeStatement.setString(3,payscale);
        }
        executeStatement.executeUpdate();
    }
}
AddLocationAndPayscale.addData();
