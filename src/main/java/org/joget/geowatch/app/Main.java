package org.joget.geowatch.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.dao.WorkflowHelper;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Wrong");
    }


    public boolean validate(Element element, FormData formData, String[] values) {

        // Constants
        String routeMapFieldId = "routeMapId";
        String isUniqueValidationMessage = "Trip name must be unique";
        String isBlankValidationMessage = "Missing required value";
        String currentUserDepartmentIdHashSelector = "#currentUser.department.id#";
        String datasourceBeanName = "setupDataSource";

        String selectRouteMapByIdSQLQuery = "SELECT * FROM app_fd_RouteMap WHERE app_fd_RouteMap.id = ?";
//        String selectGeofencesByRouteMapIdSQLQuery = "SELECT app_fd_RouteMap_Geofence.c_number,	app_fd_Geofence.c_lat, app_fd_Geofence.c_lng FROM app_fd_Geofence LEFT OUTER JOIN app_fd_RouteMap_Geofence ON app_fd_RouteMap_Geofence.c_geofenceId = app_fd_Geofence.id LEFT OUTER JOIN app_fd_RouteMap ON app_fd_RouteMap.id = app_fd_RouteMap_Geofence.c_routeMapId WHERE app_fd_RouteMap.id = ?";

        boolean result = true;

        Form form = FormUtil.findRootForm(element);
        Element routeMapElement = FormUtil.findElement(routeMapFieldId, form, formData);

        if (routeMapElement != null) {

            String[] routeMapFieldValueArray = FormUtil.getElementPropertyValues(routeMapElement, formData);
            String routeMapIdValue = routeMapFieldValueArray[0];

            if (routeMapIdValue != null && !routeMapIdValue.trim().isEmpty()) {

                Connection con = null;
                try {
                    // Retrieve connection from the default datasource
                    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean(datasourceBeanName);
                    con = ds.getConnection();
                    // Execute SQL query
                    if (!con.isClosed()) {

                        // Getting geofences by RouteMap ID
//                        PreparedStatement stmt1 = con.prepareStatement(selectGeofencesByRouteMapIdSQLQuery);
//                        stmt1.setString(1, routeMapIdValue);
//                        ResultSet rs1 = stmt1.executeQuery();
//
//                        JsonArray geofencesJA = new JsonArray();
//                        while (rs1.next()) {
//                            JsonObject jo = new JsonObject();
//                            jo.add("lat", new JsonPrimitive(rs1.getDouble("c_lat")));
//                            jo.add("lng", new JsonPrimitive(rs1.getDouble("c_lng")));
//                            jo.add("number", new JsonPrimitive(rs1.getString("c_number")));
//                            geofencesJA.add(jo);
//                        }

                        // Getting RouteMap by ID
                        PreparedStatement stmt2 = con.prepareStatement(selectRouteMapByIdSQLQuery);
                        stmt2.setString(1, routeMapIdValue);
                        ResultSet rs2 = stmt2.executeQuery();


                        JsonObject routeMapJO = new JsonObject();
                        String waypoints = "";

                        if (rs2.next()) {

                            String empty = "";

                            routeMapJO.add("id", new JsonPrimitive(rs2.getString("id") == null ? empty : rs2.getString("id")));
                            routeMapJO.add("creatorDepartmentId", new JsonPrimitive(rs2.getString("c_creatorDepartmentId") == null ? empty : rs2.getString("c_creatorDepartmentId")));
//                            routeMapJO.add("creatorDepartmentName", new JsonPrimitive(rs2.getString("c_creatorDepartmentName") == null ? empty : rs2.getString("c_creatorDepartmentName")));
                            routeMapJO.add("creatorGroupsId", new JsonPrimitive(rs2.getString("c_creatorGroupsId") == null ? empty : rs2.getString("c_creatorGroupsId")));
//                            routeMapJO.add("creatorGroupsName", new JsonPrimitive(rs2.getString("c_creatorGroupsName") == null ? empty : rs2.getString("c_creatorGroupsName")));
                            routeMapJO.add("creatorId", new JsonPrimitive(rs2.getString("c_creatorId") == null ? empty : rs2.getString("c_creatorId")));
//                            routeMapJO.add("creatorName", new JsonPrimitive(rs2.getString("c_creatorName") == null ? empty : rs2.getString("c_creatorName")));
                            routeMapJO.add("creatorOrganizationId", new JsonPrimitive(rs2.getString("c_creatorOrganizationId") == null ? empty : rs2.getString("c_creatorOrganizationId")));
//                            routeMapJO.add("creatorOrganizationName", new JsonPrimitive(rs2.getString("c_creatorOrganizationName") == null ? empty : rs2.getString("c_creatorOrganizationName")));
//                            routeMapJO.add("dateCreated", new JsonPrimitive(rs2.getString("dateCreated") == null ? empty : rs2.getString("dateCreated")));
//                            routeMapJO.add("dateModified", new JsonPrimitive(rs2.getString("dateModified") == null ? empty : rs2.getString("dateModified")));
                            routeMapJO.add("name", new JsonPrimitive(rs2.getString("c_name") == null ? empty : rs2.getString("c_name")));
                            routeMapJO.add("polylineHash", new JsonPrimitive(rs2.getString("c_polylineHash") == null ? empty : rs2.getString("c_polylineHash")));
//                            routeMapJO.add("routeSnapshot", new JsonPrimitive(rs2.getString("c_routeSnapshot") == null ? empty : rs2.getString("c_routeSnapshot")));
//                            routeMapJO.add("wayPointsQuantity", new JsonPrimitive(rs2.getString("c_wayPointsQuantity") == null ? empty : rs2.getString("c_wayPointsQuantity")));
//                            routeMapJO.add("waypoints", new JsonPrimitive(rs2.getString("c_waypoints") == null ? empty : rs2.getString("c_waypoints")));

                            // Exclude waypoints, because we need to modify this field before serialization
                            waypoints = rs2.getString("c_waypoints");

                            formData.addRequestParameterValues("nameRouteMap", new String[]{
                                    rs2.getString("c_name") == null ? empty : rs2.getString("c_name")
                            });
                        }

                        JsonArray waypointsJA = new JsonArray();
                        JsonParser jp = new JsonParser();
                        if (jp.parse(waypoints).isJsonArray()) {
                            waypointsJA = jp.parse(waypoints).getAsJsonArray();
                        }

                        // Merge geofences and waypoints JsonArrays
//                        for (int i = 0; i < waypointsJA.size(); i++) {
//                            JsonObject jobj = waypointsJA.getByRouteMapId(i).getAsJsonObject();
//                            geofencesJA.add(jobj);
//                        }


                        routeMapJO.add("wayPoints", waypointsJA);

                        formData.addRequestParameterValues("imageRouteMap", new String[]{
                                routeMapJO.toString()
                        });

//                        LogUtil.info("RMID_BSV_RESULT_ARRAY", geofencesJA.toString());

                    }

                } catch (Exception e) {
                    LogUtil.error("RouteMap bean shell validator", e, "Error");

                } finally {
                    try {
                        if (con != null) {
                            con.close();
                        }
                    } catch (SQLException e) {
                    /* ignored */
                    }
                }
            } else {
                String validationParameterName = FormUtil.getElementParameterName(element);
                formData.addFormError(validationParameterName, isBlankValidationMessage);
                result = false;
            }


        }

        return result;
    }

    String func() {
        String processDefId = "GglApp#latest#NotifyProcess";

        if (processDefId.contains(":")) {
            processDefId = processDefId.replaceAll(":", "#");
        }

        if (processDefId != null && processDefId.contains("latest")) {
            ApplicationContext appContext = WorkflowUtil.getApplicationContext();
            WorkflowHelper workflowMapper = (WorkflowHelper) appContext.getBean("workflowHelper");
            String currentVersion = workflowMapper.getPublishedPackageVersion(processDefId.split("#")[0]);
            if (currentVersion != null && currentVersion.trim().length() > 0) {
                processDefId = processDefId.replace("latest", currentVersion);
            }
        }

        return processDefId;
    }
}