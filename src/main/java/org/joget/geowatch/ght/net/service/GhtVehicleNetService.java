package org.joget.geowatch.ght.net.service;

import org.joget.geowatch.ght.net.dto.in.resp.log.LogGhtVehicleInResp;
import org.joget.geowatch.ght.net.dto.in.resp.error.GhtVehicleError;
import org.joget.geowatch.util.UrlUtil;

import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 1/3/2018
 * Time: 4:54 PM
 */
public interface GhtVehicleNetService {
    UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError>
    pullGhtLogByGhtVehicleId(String ghtVehicleId, Date lastLogDate) throws Exception;
}
