package org.joget.geowatch.test.warranty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import java.sql.DriverManager;
import java.time.LocalDate;

import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;
import javax.servlet.http.HttpServletRequest;


public class DataIntegrationEquipments {

    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        Connection con = null;
        LocalDate today = LocalDate.now();
        LocalDate previousDay = today.minusDays(1);
        try {
            //Build Url for connecting to Oracle DB
            String url = "jdbc:oracle:thin:#envVariable.jdbcURL#";
            String username = "#envVariable.jdbcUserName#";
            String password = "#envVariable.jdbcPassword#";
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //Get Connection Using DriverManager
            con = DriverManager.getConnection(url, username, password);

            // execute SQL query
            if (!con.isClosed()) {

                //Populate Equipment Master Form
                System.out.println("Connected");
                //String equipmtMasterQuery = "select * from apps.XXOIC_JOGETIN_DO_EQUIPMENT_V where DO_NUMBER IN('512800-MAINSTORE', '512801-MAINSTORE', '512802-MAINSTORE', '512802-M2','512803-MAINSTORE','512804-MAINSTORE')";
                String equipmtMasterQuery = "select * from apps.XXOIC_JOGETIN_DO_EQUIPMENT_V where DO_CAPTURE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND DO_CAPTURE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement equipmtstmt = con.prepareStatement(equipmtMasterQuery);
                equipmtstmt.setString(1,previousDay.toString());
                equipmtstmt.setString(2,today.toString());

                ResultSet equipmtrs = equipmtstmt.executeQuery();
                System.out.println("Statement executed");
                while (equipmtrs.next()) {
                    FormRowSet rowsEquipmtMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    //String uuid = UuidGenerator.getInstance().getUuid();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                    //Set values
                    String strEqDONumber = (equipmtrs.getString("DO_NUMBER") != null) ? equipmtrs.getString("DO_NUMBER").toString() : "";
                    String strModelNumber = (equipmtrs.getString("MODEL_NUMBER") != null) ? equipmtrs.getString("MODEL_NUMBER").toString() : "";

                    String strEquipmtNumber = (equipmtrs.getString("EQUIPMENT_NUMBER") != null) ? equipmtrs.getString("EQUIPMENT_NUMBER").toString() : "";
                    row.setProperty("equipment_Number", strEquipmtNumber);

                    //Set Id - using Primary Key
                    String eqpMasterId = strEqDONumber + "_" + strEquipmtNumber;
                    row.setProperty("id", eqpMasterId);

                    row.setProperty("do_Number", strEqDONumber);


                    row.setProperty("model_Number", strModelNumber);


                    String strDOCaptureDate = (equipmtrs.getString("DO_CAPTURE_DATE") != null) ? equipmtrs.getString("DO_CAPTURE_DATE").toString() : "";
                    row.setProperty("do_capture_date", strDOCaptureDate);

                    String formIdEquipmt = "equipment_master";
                    String tableNameEquipmt = "equipment_master";
                    String fieldIDEquipmt = "do_Number";

                    //Populate the form
                    //FormRow row = formDataDao.load(formId, tableName, id);
                    rowsEquipmtMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdEquipmt, tableNameEquipmt, rowsEquipmtMasterData);
                }
            }
        } catch (Exception e) {
            LogUtil.error("Exception: ",e,e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        LogUtil.info("","Test");
        return null;
    }

}
DataIntegrationEquipments.execute(assignment, appDef, request);
