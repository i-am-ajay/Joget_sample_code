package org.joget.geowatch.processing.service;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.app.AppContext;
import org.joget.geowatch.db.service.TripService;
import org.joget.workflow.model.service.WorkflowUserManager;

import java.util.concurrent.TimeUnit;

import static org.joget.geowatch.app.AppProperties.JOGET_PLUGIN_USER;
import static org.joget.geowatch.app.AppProperties.PLUGIN_MINUTE_BEFORE_LIVE;

/**
 * Created by k.lebedyantsev
 * Date: 2/27/2018
 * Time: 5:35 PM
 */
public class TripRunnable implements Runnable {
    private static final String TAG = TripRunnable.class.getSimpleName();

    @Override
    public void run() {
        try {
            LogUtil.info(TAG, "START.");

            WorkflowUserManager wfm = AppContext.getBean("workflowUserManager", WorkflowUserManager.class);
            wfm.setCurrentThreadUser(JOGET_PLUGIN_USER);

            TripService tripService = AppContext.getBean("tripService", TripService.class);
            tripService.transferTripToLife(PLUGIN_MINUTE_BEFORE_LIVE, TimeUnit.MINUTES);

            LogUtil.info(TAG, "FINISH.");
        } catch (Throwable t) {
            LogUtil.error(TAG, t, "ERROR.");
        }
    }
}
