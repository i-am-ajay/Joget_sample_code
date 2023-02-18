package org.joget.geowatch.test.warranty;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyNameLoadBinder {
    public static FormRowSet load(Element element, String username, FormData formData) {
        String field1Id = "field3";
        String companyName = null;
        Form form = FormUtil.findRootForm(element);
        Element field1 = FormUtil.findElement(field1Id, form, formData);
        String[] field1Value = FormUtil.getElementPropertyValues(field1, formData);
        System.out.println("Field1Value "+ field1Value.length);
        if(field1Value != null && field1Value.length > 0){
            companyName = getCompanyName(field1Value);
        }
        System.out.println("Field value"+field1Value);
        System.out.println("Company Name"+companyName);
        FormRowSet rows = new FormRowSet();
        FormRow row = new FormRow();
        if(companyName == null){
            companyName = "test";
        }
        System.out.println("Name of company"+companyName);
        row.setProperty("firstName",companyName);
        rows.add(row);
        return rows;
    }

    public static String getCompanyName(String recordParam){
        Connection con = null;
        String finalResult = null;
        String[] recordId = recordParam.split(",");
        String record = null;

        System.out.println("Value of record Id Is"+recordId);
        try{
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            String wildCardHolder = prepareParameter(recordId);
            if(!con.isClosed()){
                PreparedStatement statement = con.prepareStatement("SELECT c_firstName" +
                        " from app_fd_employee where id in ("+wildCardHolder+") ");
                setPreparedStatementParameters(recordId,statement);
                System.out.println(statement);
                ResultSet set = statement.executeQuery();
                StringBuilder result = new StringBuilder();

                while(set.next()){
                    result.append(set.getString(1)).append(",");
                }
                finalResult = result.deleteCharAt(result.length() - 1).toString();
            }
        }
        catch(Exception ex){
            LogUtil.error(CompanyNameLoadBinder.class.getName(),ex,ex.getMessage());
            ex.printStackTrace();
        }
        finally{
            try {
                if (con != null && con.isClosed()) {

                }
            }
            catch(Exception ex){
                LogUtil.error(CompanyNameLoadBinder.class.getName(),ex,ex.getMessage());
            }
        }
        return finalResult;
    }
    public static String prepareParameter(String[] parameter ) throws SQLException {
        System.out.println("Size of parameter"+parameter.length);
        int countHolder = 1;
        String holder = "";
        for(String str :parameter){
            if(countHolder == 1){
                if(str != null) {
                    holder += "?";
                    countHolder += 1;
                }
            }
            else{
                if(str != null) {
                    holder += ",?";
                }
            }
        }
        System.out.println("Holder $$$$$$$$$$$$$"+holder);
        /*System.out.println("prepareParameter method called");
        StringBuilder builder = new StringBuilder();
        System.out.println(parameter.length);
        for(String str : parameter){
            System.out.println("Parameter"+str);
            builder.append(str).append("','");
            //builder.append(str).append(",");
        }
        String param = builder.substring(0, builder.length()-3);
        System.out.println(param);
        return param;*/
        return holder;
    }
    public static void setPreparedStatementParameters(String[] parameter,PreparedStatement psmt) throws SQLException {
        int countHolder = 1;
        for(String str :parameter){
            System.out.println(str);
            psmt.setString(countHolder,str);
            countHolder += 1;
            System.out.println(countHolder);
        }
    }
}
CompanyNameLoadBinder.load(element, primaryKey, formData);
