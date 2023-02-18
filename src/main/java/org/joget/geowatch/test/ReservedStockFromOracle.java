import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;

import java.sql.*;

public class ReservedStockFromOracle {
    public static FormRowSet load(Element element, String primaryKey, FormData formData) throws ClassNotFoundException {
        String jobNumber = "#requestParam.jobOrder#";
        String itemNumber = "#requestParam.itemNumber#";
        FormRowSet rowsFileMetaData = new FormRowSet();
        rowsFileMetaData.setMultiRow(true);

        boolean jobNumberFound = false;
        boolean itemNumberFound = false;
        int counter = 1;
        Connection con = null;
        Class.forName("oracle.jdbc.driver.OracleDriver");
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:#envVariable.jdbcurlIP#", "#envVariable.jdbcuserName#", "#envVariable.jdbcPassword#");
            if (!con.isClosed()) {
                String predicate = "";
                String query = "SELECT distinct * FROM apps.XXOIC_ORAEBS_JOGET_MMT_V where 1=1";
                if (jobNumber != null && !jobNumber.equalsIgnoreCase("")) {
                    LogUtil.info("Job No", jobNumber);
                    predicate = predicate.concat(" AND job_no = ?");
                    jobNumberFound = true;
                }
                if (itemNumber != null && !itemNumber.equalsIgnoreCase("")) {
                    predicate = predicate.concat(" AND item_code = ?");
                    itemNumberFound = true;
                }
                query = query.concat(predicate);
                PreparedStatement stmt = con.prepareStatement(query);
                if (jobNumberFound) {
                    LogUtil.info("", query);
                    LogUtil.info("", Integer.toString(counter));
                    stmt.setString(counter, jobNumber);
                    counter += 1;
                }
                if (itemNumberFound) {
                    stmt.setString(counter, itemNumber);
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    FormRow row = new FormRow();
                    //String uuid = UuidGenerator.getInstance().getUuid();
                    row.setProperty("item_code", (processColumn(rs, "item_code")));
                    row.setProperty("job_no", (processColumn(rs, "job_no")));
                    row.setProperty("quantity", (processColumn(rs, "quantity")));
                    row.setProperty("transaction_type", (processColumn(rs, "transaction_type")));
                    row.setProperty("sub_inventory", (processColumn(rs, "sub_inventory")));
                    row.setProperty("unique_transcation_id", (processColumn(rs, "unique_transcation_id")));
                    row.setProperty("inventory_org", (processColumn(rs, "inventory_org")));
                    rowsFileMetaData.add(row);
                }
            }
        }
        catch(Exception ex){
            LogUtil.error("Message : ",ex,ex.getMessage());
        }
        finally{
            if(con != null){
                try{
                    con.close();
                }
                catch(Exception x){
                    LogUtil.error("Message: ",x, x.getMessage());
                }
            }
        }
        LogUtil.info("Size",Integer.toString(rowsFileMetaData.size()));
        return rowsFileMetaData;
    }

    public static String processColumn(ResultSet rs, String name) throws SQLException {
        String val = (rs.getString(name) == null ? "" : rs.getString(name));
        LogUtil.info(name,val);
        return val;
    }
}

ReservedStockFromOracle.load(element,primaryKey,formData);
