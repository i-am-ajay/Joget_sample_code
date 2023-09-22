package org.joget.geowatch.test;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MessageValidator {
    public static boolean validate(Element element, FormData formData, String [] values) {
        FormDataDao dao = null;

        Form form = FormUtil.findRootForm(element);
        Element threadIdElement = FormUtil.findElement("thread_id", form, formData);
        String threadIdValue = FormUtil.getElementPropertyValue(threadIdElement, formData);

        Element fullNameElement = FormUtil.findElement("full_name", form, formData);
        String fullNameValue = FormUtil.getElementPropertyValue(fullNameElement, formData);

        Element usernameElement = FormUtil.findElement("username", form, formData);
        String usernameValue = FormUtil.getElementPropertyValue(usernameElement, formData);

        Element messageElement = FormUtil.findElement("message", form, formData);
        String messageValue = FormUtil.getElementPropertyValue(messageElement, formData);

        Element typeUserElement = FormUtil.findElement("typeUser", form, formData);
        String typeUserValue = FormUtil.getElementPropertyValue(fullNameElement, formData);
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        try{
            LogUtil.info("Thread Id",threadIdValue);
            LogUtil.info("Message",messageValue);
            LogUtil.info("Username",usernameValue);
            LogUtil.info("Type User",typeUserValue);
            con = ds.getConnection();
            if (!con.isClosed()) {
                //Here you can query from one or multiple tables using JOIN etc
                String sql = "SELECT c_message,dateCreated FROM app_fd_message_data_chat dc "+
                        "WHERE dc.c_thread_id=? Order By dateCreated DESC";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setString(1, threadIdValue);

                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    String dbMessage = rs.getString(1);
                    String dbCreationTime = rs.getString(2);
                    LocalDateTime dbDateTime = LocalDateTime.parse(dbCreationTime, DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long mins = ChronoUnit.MINUTES.between(currentDateTime, dbDateTime);
                    if(mins < 15 && messageValue.equals(dbMessage)){
                        return false;
                    }
                    return true;
                }
            }
        }
        catch(SQLException ex){
            LogUtil.error("RateValidation",ex,ex.getMessage());
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
        return true;
    }
}
MessageValidator.validate(element, formData, values);
