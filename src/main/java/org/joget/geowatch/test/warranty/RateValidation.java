import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.commons.util.LogUtil;


public class RateValidation {
    public static boolean validate(Element element, FormRowSet rows, FormData formData) {
        FormDataDao dao = null;

        Form form = FormUtil.findRootForm(element);
        Element field1 = FormUtil.findElement("vendor_id", form, formData);

        String vendorId = FormUtil.getElementPropertyValue(field1, formData);
        for(FormRow row : rows){
            String item = row.getProperty("item_name");
            String project = row.getProperty("projectid");
            String cost = row.getProperty("vendor_rate");
            String vendorUsername = row.getProperty("vendor_username");
            LogUtil.info("Vendor Username",vendorUsername);

            String userName = row.getProperty("vendor_username");
            LogUtil.info("Username",userName);

            if(cost == null || cost == ""){
                String id = FormUtil.getElementParameterName(element);
                formData.addFormError(id, "Rate Can't be null.");
                return false;
            }
            /*String projectId = "bid";
            // get root element
            Form form = FormUtil.findRootForm(element);
            Element field1 = FormUtil.findElement(projectId, form, formData);
            String fieldValue = FormUtil.getElementPropertyValue(field1, formData);*/

            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            Connection con = null;
            try{
                LogUtil.info("RateValidator","Running Query");
                LogUtil.info("Vendor Id",vendorId);
                con = ds.getConnection();
                if (!con.isClosed()) {
                    //Here you can query from one or multiple tables using JOIN etc
                    String sql = "SELECT * FROM app_fd_sb_boq_vendor bv JOIN app_fd_sb_rfi_details rd ON " +
                            " bv.c_vendor_username = rd.c_vendor_username and bv.c_projectid = rd.c_projectId "+
                            "WHERE bv.c_item_name=? and bv.c_projectid=? " +
                            "and bv.c_vendor_rate = ? and bv.c_vendor_username <> ? and rd.c_rfi_status <> 'Rejected'";
                    PreparedStatement stmt = con.prepareStatement(sql);
                    stmt.setString(1, item);
                    stmt.setString(2, project);
                    stmt.setString(3, cost);
                    stmt.setString(4,vendorUsername);
                    ResultSet rs = stmt.executeQuery();
                    if(rs.next()){
                        String id = FormUtil.getElementParameterName(element);
                        formData.addFormError(id, "For Item: "+item+" Rate: "+cost+" already quoted. Enter another rate.");
                        return false;
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
        }

        return true;
    }
}
RateValidation.validate(element, rows, formData);