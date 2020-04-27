package org.joget.geowatch.api.controller;

import org.apache.commons.lang.StringUtils;
import org.joget.commons.util.LogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.joget.geowatch.app.AppProperties.PLUGIN_DEBUG_MODE;
import static org.joget.geowatch.util.StrUtil.getObj;
import static org.joget.geowatch.util.StrUtil.getStr;

public class HttpWrap {
    private static final String TAG = HttpWrap.class.getSimpleName();
    private HttpServletRequest request;
    private HttpServletResponse response;

    private String method;
    private String uri;
    private String params;
    private String session;

    private Object body;
    private String strBody;

    private Object object;

    private Integer status = null;
    private boolean isError = true;

    public HttpWrap(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        if (PLUGIN_DEBUG_MODE) init();
    }

    protected void init() {
        method = request.getMethod();
        uri = request.getRequestURI();
        params = request.getQueryString();
        session = request.getRequestedSessionId();
    }

    public HttpWrap result(int status, Object result) throws Exception {
        this.status = status;
        this.object = result;
        isError = false;
        return this;
    }

    public HttpWrap error(int status, Object error) throws Exception {
        this.status = status;
        this.object = error;
        isError = true;
        return this;
    }

    public String getParameter(String name) {
        return request.getParameter(name);
    }

    public <T> T getBody(Class<T> clas) throws Exception {
        strBody = getStr(request.getReader());
        body = getObj(strBody, clas);
        return (T) body;
    }

    public String getMethod() throws Exception {
        return request.getMethod();
    }

    public void send() {
        try {
            if (isError || status == null)
                response.sendError(status != null ? status : SC_INTERNAL_SERVER_ERROR, getStr(object));
            else {
                response.setStatus(status);
                String body = getStr(object);
                response.getWriter().write(body.length() > 0 ? body : "{}");
            }

            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "INCOMING METHOD: " + method);
                LogUtil.info(TAG, "INCOMING URI: " + uri);
                LogUtil.info(TAG, "INCOMING PARAMS: " + params);
                LogUtil.info(TAG, "INCOMING SESSION: " + session);
                LogUtil.info(TAG, "INCOMING BODY: " + strBody);
                LogUtil.info(TAG, "INCOMING STATUS: " + status);
                LogUtil.info(TAG, "INCOMING RESULT: " + getStr(object));
            }

        } catch (Exception e) {
            LogUtil.error(TAG, e, "ERROR");
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }
}
