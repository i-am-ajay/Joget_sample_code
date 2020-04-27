package org.joget.geowatch.api.controller;

import org.joget.directory.model.User;

/**
 * Created by k.lebedyantsev
 * Date: 1/11/2018
 * Time: 2:12 PM
 */
public interface Controller {
    HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception;

    HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception;

    HttpWrap processPut(User user, HttpWrap httpWrap) throws Exception;

    HttpWrap processDelete(User user, HttpWrap httpWrap) throws Exception;

}
