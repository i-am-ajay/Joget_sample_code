package org.joget.geowatch.ght.net.dto.in.resp.error;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k.lebedyantsev
 * Date: 2/14/2018
 * Time: 2:05 PM
 */
public enum VehicleErrorCode {
    @SerializedName("0")
    MISSING_API_KEY,
    @SerializedName("1")
    MISSING_VEHICLE_ID,
    @SerializedName("2")
    UNKNOWN_VEHICLE_ID
}
