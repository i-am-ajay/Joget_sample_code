package org.joget.geowatch.util;

import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.directory.model.Group;
import org.joget.directory.model.User;
import org.joget.workflow.model.service.WorkflowManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNoneEmpty;
import static org.joget.geowatch.app.AppContext.getBean;
import static org.joget.geowatch.app.AppProperties.JOGET_PROCESS_NOTIFY_NAME;

/**
 * Created by k.lebedyantsev
 * Date: 3/12/2018
 * Time: 11:44 AM
 */
public class JogetUtil {
    private static final String TAG = JogetUtil.class.getSimpleName();

    public static String getEnvVar(String key, String localStr, String defStr, String defLocalStr) {
        String var = defStr;
        if (isEmpty(key)) return var;

        if (isNoneEmpty(defLocalStr)) var = ResourceBundleUtil.getMessage(key, new Locale(defLocalStr));
        if (isNoneEmpty(localStr) && !localStr.equalsIgnoreCase(defLocalStr))
            var = ResourceBundleUtil.getMessage(key, var, new Locale(localStr));
        return var;
    }

    protected static void sendEmail(Email email) throws Exception {
        try {
            WorkflowManager wfm = getBean("workflowManager", WorkflowManager.class);
            wfm.processStart(JOGET_PROCESS_NOTIFY_NAME, email.variables());
        } catch (Exception e) {
            LogUtil.error(TAG, e, "Error send email: " + email);
            throw new Exception(e);
        }
    }

    public static class Email {
        String sendTo;
        String tripId;
        String requesterDepartment;
        String vehicleId;
        String eventDate;
        String description;
        String body;
        String geofenceName;

        public Email(String sendTo, String tripId,String requesterDepartment , String vehicleId, String eventDate, String description, String body, String geofenceName) {
            this.sendTo = sendTo;
            this.tripId = tripId;
            this.requesterDepartment = requesterDepartment;
            this.vehicleId = vehicleId;
            this.eventDate = eventDate;
            this.description = description;
            this.body = body;
            this.geofenceName = geofenceName;
        }

        Map<String, String> variables() {
            Map<String, String> map = new HashMap();
            map.put("sendTo", sendTo);
            map.put("tripId", tripId);
            map.put("requesterDepartment", requesterDepartment);          
            map.put("vehicleId", vehicleId);
            map.put("eventDate", eventDate);
            map.put("description", description);
            map.put("body", body);
            map.put("geofenceName", geofenceName);
            return map;
        }
    }

    public static boolean isEditable(User user) throws Exception {

        for (Object o : user.getGroups()) {
            Group g = (Group) o;
            if ("MonitorAdmin".equals(g.getId())
                    || "MonitorUser".equals(g.getId()))
                return true;
        }
        return false;
    }

    /*
     * This method create specific Joget processResponse. Work only with
     * user - PU. This user can be at db.
     */
    public static void updateJogetProccess(String processInstanceId, String statusKey, String statusValue) throws Exception {
        try {
            WorkflowManager wfm = getBean("workflowManager", WorkflowManager.class);
            wfm.activityVariable(processInstanceId, statusKey, statusValue);
        } catch (Exception e) {
            LogUtil.error(TAG, e, "Error at create processResponse state with instance ID: " + processInstanceId);
            throw new Exception(e);
        }
    }

    /*
     * This method jump to specific processResponse element. Work only with
     * user - PU. This user can be at db.
     */
    public static void completeActivity(String processInstanceId, String nextActivity) throws Exception {
        try {
            WorkflowManager wfm = getBean("workflowManager", WorkflowManager.class);
            if (!wfm.activityStart(processInstanceId, nextActivity, true))
                throw new Exception();
        } catch (Exception e) {
            LogUtil.error(TAG, e, "Error at complete activity with instance ID: " + processInstanceId);
            throw new Exception(e);
        }
    }

    /*
     * This method start processResponse by id. Work only with
     * user - PU. This user can be at db.
     */
    public static void processStart(String processId, Map<String, String> variables) throws Exception {
        try {
            WorkflowManager wfm = getBean("workflowManager", WorkflowManager.class);

          /*  List<WorkflowProcess> processes = (List<WorkflowProcess>) wfm.getProcessList(PACKAGE_ID);
            WorkflowProcess searchedProcess = null;
            for (WorkflowProcess processResponse: processes){
                if (processResponse.getGhtVehicleInResp().equals(processId)){
                    searchedProcess = processResponse;
                    break;
                }
            }

            if (searchedProcess != null){*/
//                WorkflowAssignment wfAssignment = wfm.getAssignmentByProcess(searchedProcess.getGhtVehicleInResp());
            wfm.processStart(processId, variables);
//            }
        } catch (Exception e) {
            LogUtil.error(TAG, e, "Error at start processResponse with id: " + processId);
            throw new Exception(e);
        }
    }

    public static class EmailSender {

        private Thread consumer;
        private BlockingQueue<Email> queue;

        private EmailSender(int capacity) {
            queue = new ArrayBlockingQueue<>(capacity, true);
            consumer = new Thread(new Consume(queue), "Email Consume");
            consumer.start();
        }

        public static EmailSender getInstance(int capacity) {
            EmailSender instance = new EmailSender(capacity);
            return instance;
        }

        public void put(Email email) throws InterruptedException {
            if (email != null) {
                queue.put(email);
            }
        }

        class Consume implements Runnable {
            private BlockingQueue<Email> queue;

            public Consume(BlockingQueue<Email> queue) {
                this.queue = queue;
            }

            @Override
            public void run() {
                try {

                    while (true)
                        sendEmail(this.queue.take());

                } catch (Exception e) {
                    LogUtil.error(TAG, e, "ERROR. Cannot send email");
                }
            }
        }
    }
}
