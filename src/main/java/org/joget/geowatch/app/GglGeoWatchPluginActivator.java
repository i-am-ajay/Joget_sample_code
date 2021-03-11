package org.joget.geowatch.app;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.api.configuration.MapActivityApi;
import org.joget.geowatch.api.configuration.MapApi;
import org.joget.geowatch.db.service.impl.DateServiceImpl;
import org.joget.geowatch.processing.service.ArchiveProceess;
import org.joget.geowatch.processing.service.LogRunnable;
import org.joget.geowatch.processing.service.TripRunnable;
import org.joget.geowatch.util.JogetUtil;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.joget.geowatch.util.DateUtil.getFeApiStrDate;

public class GglGeoWatchPluginActivator implements BundleActivator {
    private static final String TAG = GglGeoWatchPluginActivator.class.getSimpleName();

    protected Collection<ServiceRegistration> registrationList;

    protected static volatile JogetUtil.EmailSender emailSender;
    protected ScheduledExecutorService ghtHandlerExecutor = Executors.newSingleThreadScheduledExecutor();
    protected ScheduledExecutorService tripHandlerExecutor = Executors.newSingleThreadScheduledExecutor();
    protected ScheduledExecutorService archiveHandlerExecutor = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean cancelInitFlag = false;

    public void start(BundleContext context) {
        LogUtil.info(TAG, "################################################################################");
        LogUtil.info(TAG, "GGL PLUGIN. REGISTRATION START");
        try {
            //Register plugin here
            registrationList = new ArrayList<>();
            registrationList.add(context.registerService(MapApi.class.getName(), new MapApi(), null));
            registrationList.add(context.registerService(MapActivityApi.class.getName(), new MapActivityApi(), null));

            cancelInitFlag = false;
            new Thread(new InnitRunnable()).start();

            LogUtil.info(TAG, "GGL PLUGIN. REGISTRATION COMPLETED");
        } catch (Throwable t) {
            LogUtil.error(TAG, t, "GGL PLUGIN. REGISTRATION ERROR");
        }
    }

    class InnitRunnable implements Runnable {

        @Override
        public void run() {
            LogUtil.info(TAG, "GGL PLUGIN. WAITING INIT");
            try {
                synchronized (GglGeoWatchPluginActivator.this) {
                    GglGeoWatchPluginActivator.this.wait(TimeUnit.MINUTES.toMillis(1L));
                }

                if (cancelInitFlag){
                    LogUtil.info(TAG, "GGL PLUGIN. INITIALIZATION CANCELED");
                    return;
                }

                init();

            } catch (Throwable t) {
                LogUtil.error(TAG, t, "GGL PLUGIN. WAITING INIT ERROR");
            }
        }
    }

    protected void init() {
        try {
            LogUtil.info(TAG, "GGL PLUGIN. INITIALIZATION START");

            LogUtil.info(TAG, "SERVER TIME: " + getFeApiStrDate(new Date()));
            LogUtil.info(TAG, "DB TIME: " + getFeApiStrDate(AppContext.getBean("dateService", DateServiceImpl.class).getDbDate()));

            emailSender = JogetUtil.EmailSender.getInstance(100);

//            ghtHandlerExecutor.scheduleWithFixedDelay(new LogRunnable(), 1, 7, TimeUnit.MINUTES);
//            tripHandlerExecutor.scheduleWithFixedDelay(new TripRunnable(), 4, 7, TimeUnit.MINUTES);

            ghtHandlerExecutor.scheduleWithFixedDelay(new LogRunnable(), 1, 1, TimeUnit.MINUTES);
            tripHandlerExecutor.scheduleWithFixedDelay(new TripRunnable(), 1, 1, TimeUnit.MINUTES);
            archiveHandlerExecutor.scheduleWithFixedDelay(new ArchiveProceess(), 1, 15, TimeUnit.MINUTES);
            LogUtil.info(TAG, archiveHandlerExecutor.toString());
            LogUtil.info(TAG, "GGL PLUGIN. INITIALIZATION COMPLETED");

        } catch (Throwable t) {
            LogUtil.error(TAG, t, "GGL PLUGIN. INITIALIZATION ERROR");
        }
    }

    public void stop(BundleContext context) {
        try {
            cancelInitFlag = true;
            synchronized (GglGeoWatchPluginActivator.this) {
                GglGeoWatchPluginActivator.this.notifyAll();
            }
            destroy();

            LogUtil.info(TAG, "GGL PLUGIN. UNREGISTER START");

            for (ServiceRegistration registration : registrationList)
                registration.unregister();

            LogUtil.info(TAG, "GGL PLUGIN. UNREGISTER COMPLETED");
        } catch (Throwable t) {
            LogUtil.error(TAG, t, "GGL PLUGIN. UNREGISTER ERROR");
        }finally {
            LogUtil.info(TAG, "################################################################################");
        }
    }

    public void destroy() {
        LogUtil.info(TAG, "GGL PLUGIN. DESTROY START");
        try {

            ghtHandlerExecutor.shutdown();
            ghtHandlerExecutor = null;

            tripHandlerExecutor.shutdown();
            tripHandlerExecutor = null;
            
            archiveHandlerExecutor.shutdown();
            archiveHandlerExecutor=null;

            LogUtil.info(TAG, "SERVER TIME: " + getFeApiStrDate(new Date()));
            LogUtil.info(TAG, "DB TIME: " + getFeApiStrDate(AppContext.getBean("dateService", DateServiceImpl.class).getDbDate()));

            LogUtil.info(TAG, "GGL PLUGIN. DESTROY COMPLETED");
        } catch (Throwable t) {
            LogUtil.error(TAG, t, "GGL PLUGIN. DESTROY ERROR");
        }
    }

    public static void sendEmail(JogetUtil.Email email) throws InterruptedException {
        emailSender.put(email);
    }
}