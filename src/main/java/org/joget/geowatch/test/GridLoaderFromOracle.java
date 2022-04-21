import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import java.sql.*;

public class GridLoaderFromOracle {
    public static FormRowSet load(Element element, String primaryKey, FormData formData) throws ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String jobId = "#requestParam.work_orderno#";
        Connection oracleCon = null;
        FormRowSet rowSet = new FormRowSet();
        rowSet.setMultiRow(true);
        try{
            oracleCon = DriverManager.getConnection("jdbc:oracle:thin:#envVariable.jdbcurlIP#", "#envVariable.jdbcuserName#", "#envVariable.jdbcPassword#");
            String query = "SELECT distinct SUM(QUANTITY),INVENTORY_ITEM_ID, SUB_INVENTORY,TRANSACTION_UOM," +
                    "JOB_NO,ITEM_CODE FROM apps.XXOIC_ORAEBS_JOGET_MMT_V where 1=1 and JOB_NO = ?" +
                    " group by JOB_NO,ITEM_CODE,INVENTORY_ITEM_ID,SUB_INVENTORY,TRANSACTION_UOM";
            PreparedStatement stmt = oracleCon.prepareStatement(query);
            stmt.setString(1, "2518");
            ResultSet set = stmt.executeQuery();
            while(set.next()){
                String quantity = set.getString(1);
                String transactionUom = set.getString(4);
                String itemCode = set.getString(6);

                FormRow row = new FormRow();
                row.setProperty("ITEM_CODE",processColumn(itemCode));
                row.setProperty("QUANTITY",processColumn(quantity));
                row.setProperty("TRANSACTION_UOM",processColumn(transactionUom));
                rowSet.add(row);
            }
        }
        catch(Exception ex){
            LogUtil.error("isReservedStockAvailabe",ex,ex.getMessage());
        }
        finally{
            try {
                if (oracleCon != null && !oracleCon.isClosed()) {
                    oracleCon.close();
                }
            }
            catch(SQLException ex){

            }
        }
       return rowSet;
    }

    public static String processColumn(String val) throws SQLException {
        return (val == null ? "" : val);
    }

    public static void main(String [] args){
        try {
            GridLoaderFromOracle.load(null, null, null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
//GridLoaderFromOracle.load(element, primaryKey, formData);

