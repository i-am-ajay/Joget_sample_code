package org.joget.geowatch.ght.net.service.impl;

import org.joget.geowatch.ght.net.dto.in.resp.error.GhtVehicleError;
import org.joget.geowatch.ght.net.dto.in.resp.log.LogGhtVehicleInResp;
import org.joget.geowatch.ght.net.service.GhtVehicleNetService;
import org.joget.geowatch.ghtrack.service.impl.AbstractGhTrackService;
import org.joget.geowatch.util.UrlUtil;

import java.util.Date;

import static org.joget.geowatch.app.AppProperties.GHT_BASE_URL;
import static org.joget.geowatch.app.AppProperties.GHT_VEHICLE_API_KEY;
import static org.joget.geowatch.util.DateUtil.getGhtStrDate;
import static org.joget.geowatch.util.UrlUtil.Method.GET;
import static org.joget.geowatch.util.UrlUtil.getObj;

;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 4:36 PM
 */
public class GhtVehicleNetServiceImpl extends AbstractGhTrackService implements GhtVehicleNetService {
    private static final String TAG = GhtVehicleNetServiceImpl.class.getSimpleName();

    private static final String GET_VEHICLE_URL = GHT_BASE_URL + "/api/v2/vehicle/%s?from_ts=%s";
    private static final String GET_VEHICLE_URL1 = GHT_BASE_URL + "/api/v2/vehicle/%s";

    @Override
    public UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError>
    pullGhtLogByGhtVehicleId(String ghtVehicleId, Date lastLogDate) throws Exception {
        UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError> respRes;

        System.out.println("----"+lastLogDate);
        if (lastLogDate != null) {
            respRes =
                    getObj(LogGhtVehicleInResp.class, GhtVehicleError.class,
                            GET, GET_VEHICLE_URL, new String[]{ghtVehicleId, getGhtStrDate(lastLogDate)}, null,
                            new UrlUtil.Header[]{
                                    new UrlUtil.Header("Content-Type", "application/json"),
                                    new UrlUtil.Header("X-Api-Key", GHT_VEHICLE_API_KEY)}
                    );
        } else {
            respRes =
                    getObj(LogGhtVehicleInResp.class, GhtVehicleError.class,
                            GET, GET_VEHICLE_URL1, new String[]{ghtVehicleId}, null,
                            new UrlUtil.Header[]{
                                    new UrlUtil.Header("Content-Type", "application/json"),
                                    new UrlUtil.Header("X-Api-Key", GHT_VEHICLE_API_KEY)}
                    );
        }

        return respRes;
    }
}
