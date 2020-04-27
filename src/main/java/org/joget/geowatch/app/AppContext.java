package org.joget.geowatch.app;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 1:52 PM
 */
public class AppContext {
    private static final String TAG = AppContext.class.getSimpleName();

    private static volatile AbstractApplicationContext abstractApplicationContext = null;

    private static AbstractApplicationContext getInstance() throws Exception {
        if (abstractApplicationContext != null) return abstractApplicationContext;
        else {
            synchronized (AppContext.class) {
                if (abstractApplicationContext == null) {
                    Thread currentThread = Thread.currentThread();
                    ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
                    try {
                        currentThread.setContextClassLoader(AppContext.class.getClassLoader());
                        abstractApplicationContext = new ClassPathXmlApplicationContext(
                                new String[]{"/geowatchApplicationContext.xml"},
                                AppContext.class, AppUtil.getApplicationContext());
                    } catch (Throwable t) {
                        LogUtil.error(TAG, t, "ERROR");
                        throw t;
                    } finally {
                        currentThread.setContextClassLoader(threadContextClassLoader);
                    }
                }
                return abstractApplicationContext;
            }
        }
    }

    public static <T> T getBean(String name, Class<T> clas) throws Exception {
        return (T) getInstance().getBean(name);
    }
}
