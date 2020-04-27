package org.joget.geowatch.ght.net.dto.in.resp.error;

/**
 * Created by klebedyantsev on 2017-12-27
 */
public enum StatusMessages {
    ERR_20000_SHORT("Cancelled by user"),
    ERR_20100_SHORT("Cancelled at import"),
    ERR_30000_SHORT("Planned too far in the past"),
    ERR_30100_SHORT("Planned too far in the past"),
    ERR_30200_SHORT("Planned far in the future"),
    ERR_30300_SHORT("Planned in the past"),
    ERR_40000_SHORT("Waiting for location"),
    ERR_40100_SHORT("Waiting for address lookup..."),
    ERR_40200_SHORT("Unknown vehicle"),
    ERR_40300_SHORT("Unknown address"),
    ERR_50100_SHORT("Telematics account failure"),
    ERR_50200_SHORT("Receiving no data"),
    ERR_60000_SHORT("Planned begin in the past"),
    ERR_60100_SHORT("Erroneous stop window"),
    ERR_60200_SHORT("Erroneous RTA window"),
    ERR_60300_SHORT("Stop window error"),
    ERR_90000_SHORT("No data received"),
    ERR_90100_SHORT("No real-time data received"),
    ERR_90200_SHORT("Inadequate data reception"),
    ERR_90300_SHORT("Erroneous ETA calculation"),
    ERR_90400_SHORT("Erroneous telematics account"),
    ERR_90500_SHORT("Erroneous data reception"),
    STAT_20200_SHORT("Merged with other tour"),
    STAT_20300_SHORT("Interrupted by other tour"),
    STAT_20400_SHORT("Stop not reached"),
    STAT_20500_SHORT("Stops out of sequence"),
    STAT_20600_SHORT("Tour timeout"),
    STAT_20700_SHORT("Tour deactivated"),
    STAT_50000_SHORT("Tour not activated"),
    STAT_50300_SHORT("Calculating ETA..."),
    STAT_50400_SHORT("ETA not calculated"),
    STAT_50500_SHORT("No ETA - data too old"),
    STAT_70000_SHORT("Vehicle off route"),
    STAT_70100_SHORT("Location not permitted"),
    STAT_70200_SHORT("Temperature alarm"),
    STAT_70300_SHORT("Reefer alarm"),
    STAT_70400_SHORT("Door alarm"),
    STAT_70500_SHORT("Late for next stop"),
    STAT_70600_SHORT("Tour currently late"),
    STAT_80000_SHORT("Previously off route"),
    STAT_80100_SHORT("Previous unpermitted stop"),
    STAT_80200_SHORT("Previous temperature alarm"),
    STAT_80300_SHORT("Previous reefer alarm"),
    STAT_80400_SHORT("Previous door alarm"),
    STAT_80500_SHORT("Tour was late"),
    STAT_80600_SHORT("Late for last stop"),
    STAT_80700_SHORT("Late for first stop"),
    STAT_80800_SHORT("Late for intermediate stop"),
    STAT_20200_LONG("The tour was cancelled as the stops have been merged with another tour. The remaining of the tour is not executed."),
    STAT_20300_LONG("The tour was cancelled as it did not complete before another adjacent tour started using the same asset. " +
            "The remaining of the tour is not executed."),
    STAT_20400_LONG("One or more of the stops were not reached due to imprecise addresses or erroneous tracking data."),
    STAT_20500_LONG("The tour was executed out of the allowed sequence or a stop was missed due to inadequate tracking data."),
    STAT_20600_LONG("The tour timed out. Not all stop locations were visited within the overall tour time frame."),
    STAT_20700_LONG("The tour was deactivated (typically through the API). The remaining of the tour is not executed."),
    STAT_50000_LONG("Planned beginning of the tour is surpassed without the tour being initiated at the pickup location."),
    STAT_50300_LONG("Please wait while ETA is being calculated..."),
    STAT_50400_LONG("ETA cannot be calculated due to error-prone GPS data."),
    STAT_50500_LONG("ETA cannot be calculated as the most recent GPS position is too old to provide a valid and trustworthy result."),
    STAT_70000_LONG("The vehicle does not follow the authorized route."),
    STAT_70100_LONG("The current stop location is not planned and thereby permitted."),
    STAT_70200_LONG("The current temperature is outside the permitted min/max thresholds."),
    STAT_70300_LONG("The vehicle has one or more reefer alarms."),
    STAT_70400_LONG("One or more doors have been opened at unauthorized locations."),
    STAT_70500_LONG("The next appointment is estimated late."),
    STAT_70600_LONG("One or more of the next appointments are estimated to be late."),
    STAT_80000_LONG("The vehicle has been deviating from the authorized route."),
    STAT_80100_LONG("The vehicle has had a number of unauthorized stops."),
    STAT_80200_LONG("There has been a number of temperature alarms."),
    STAT_80300_LONG("There has been a number of reefer alarms."),
    STAT_80400_LONG("There has been a number of door alarms."),
    STAT_80500_LONG("One or more appointments were late."),
    STAT_80600_LONG("The last stop had late arrival."),
    STAT_80700_LONG("The first stop had late arrival."),
    STAT_80800_LONG("One or more of the intermediate stops were late arrivals."),
    ERR_20000_LONG("The tour was cancelled by a user before its completion."),
    ERR_20100_LONG("The tour was cancelled at import. The tour could not be executed."),
    ERR_30000_LONG("At the time of tour creation, the planned end - and thereby the entire tour time frame - " +
            "was already too far in the past. It was not possible to initiate and processResponse the tour using historical data. " +
            "The tour could therefore not be executed."),
    ERR_30100_LONG("At the time of tour creation, the planned begin was too far in the past. " +
            "Tracking of the tour may therefore not start at the pickup location and the first part of the tour may be missing."),
    ERR_30200_LONG("At the time of tour creation, the planned begin is far in the future." +
            " This may indicate erroneous tour data. The tour is however" +
            " accepted and will execute if possible when time comes."),
    ERR_30300_LONG("At the time of tour creation, the planned end - and thereby the entire tour time frame - was already in the past. " +
            "The initiation of the tour at pickup may be recognized from historical data but in that " +
            "case the initial track will not be available. Any or all of the stops may be recognized from historical data."),
    ERR_40000_LONG("Waiting for more locations. At least two stops are required for the tour to execute. Currently, the tour is on hold."),
    ERR_40100_LONG("Waiting for address lookup… The tour cannot execute before all stops are geo-coded."),
    ERR_40200_LONG("The license plate or asset id is not known by the service and therefore the vehicle cannot be tracked."),
    ERR_40300_LONG("One or more addresses cannot be understood/geo-referenced. " +
            "This results in unknown geographical stop locations and the tourcan therefore not be executed."),
    ERR_50100_LONG("The telematics account for the vehicle has failed and the data connection is closed. No data is received."),
    ERR_50200_LONG("No telematics data is currently being received from the vehicle."),
    ERR_60000_LONG("At the time of tour creation, the expected tour start time was already in the past. " +
            "This may result in not receiving tracking data from the beginning of the tour. " +
            "Tour start time may be tagged from historical data."),
    ERR_60100_LONG("One or more of the stops have time windows outside the overall tour time frame. These stop windows are ignored."),
    ERR_60200_LONG("One or more of the stops have an RTA (Requested Time of Arrival) value outside the overall tour time frame. " +
            "These RTA values are ignored."),
    ERR_60300_LONG("One or more of the stops have assigned a time window where thebegin and end time has been interchanged. " +
            "These windows are ignored."),
    ERR_90000_LONG("No tracking data was received within the entire tour time frame."),
    ERR_90100_LONG("No real-time data has been received within the entire tour time frame. Historical data has been used to activate the tour."),
    ERR_90200_LONG("There have been periods with inadequate data reception for ETA calculation."),
    ERR_90300_LONG("There have been periods with ETA calculation problems."),
    ERR_90400_LONG("There have been periods with one or more tracking account failure."),
    ERR_90500_LONG("There have been periods with inadequate data reception."),
    ERR_0_SHORT("No such error id"),
    ERR_0_LONG("No such error id. Please contact with ghTrack");

    private String value;

    StatusMessages(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
