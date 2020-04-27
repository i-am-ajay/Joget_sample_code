package org.joget.geowatch.ght.net.dto.in.resp.error;

import com.google.gson.annotations.SerializedName;

/**
 * Created by k.lebedyantsev
 * Date: 1/8/2018
 * Time: 5:45 PM
 */
public enum TourErrorCode {
    @SerializedName("1")
    NO_ERROR,
    @SerializedName("2")
    INPUT_ERROR,
    @SerializedName("3")
    MISSING_TOUR_ID,
    @SerializedName("4")
    UNKNOWN_TOUR_ID,
    @SerializedName("5")
    MISSING_HAULIER_ID,
    @SerializedName("6")
    MISSING_CUSTOMER_ID,
    @SerializedName("7")
    MISSING_VEHICLE_ID,
    @SerializedName("8")
    UNKNOWN_VEHICLE,
    @SerializedName("9")
    MISSING_TRANSPORT_COMPANY_ID
}
