package org.joget.geowatch.processing.service;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.app.AppContext;
import org.joget.geowatch.processing.LogDeviseProcess;
import org.joget.workflow.model.service.WorkflowUserManager;

import static org.joget.geowatch.app.AppProperties.JOGET_PLUGIN_USER;

/**
 * Created by k.lebedyantsev
 * Date: 1/30/2018
 * Time: 1:35 PM
 */
public class LogRunnable implements Runnable {
    private static final String TAG = LogRunnable.class.getSimpleName();

    @Override
    public void run() {
        try {
            LogUtil.info(TAG, "START.");

            WorkflowUserManager wfm = AppContext.getBean("workflowUserManager", WorkflowUserManager.class);
            wfm.setCurrentThreadUser(JOGET_PLUGIN_USER);

            LogDeviseProcess logDeviseProcess = AppContext.getBean("logDeviseProcess", LogDeviseProcess.class);
            logDeviseProcess.process();

            LogUtil.info(TAG, "FINISH.");
        } catch (Throwable t) {
            LogUtil.error(TAG, t, "ERROR.");
        }
    }
}
