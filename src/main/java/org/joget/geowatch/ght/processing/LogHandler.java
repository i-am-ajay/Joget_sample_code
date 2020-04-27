package org.joget.geowatch.ght.processing;

import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;
import org.joget.geowatch.ght.net.dto.in.resp.error.GhtVehicleError;
import org.joget.geowatch.ght.net.dto.in.resp.log.Trailer;
import org.joget.geowatch.ght.net.dto.in.resp.log.VehicleBase;
import org.joget.geowatch.ght.net.dto.in.resp.log.VehicleSample;
import org.joget.geowatch.ght.net.dto.in.resp.log.simple.Doors;
import org.joget.geowatch.ght.net.dto.in.resp.log.simple.Temperature;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.joget.geowatch.util.DateUtil.getGhtDate;

/**
 * Created by k.lebedyantsev
 * Date: 2/15/2018
 * Time: 12:52 PM
 */
public class LogHandler {

    public List<Log> handleSample(
            Trip trip, VehicleInnerEntity vehicle, GhtVehicleInnerEntity ghtVehicle,
            List<VehicleSample> samples, Integer netResult, Date reqDate) throws Exception {
        List<Log> logList = new ArrayList<>();
        if (samples != null) for (VehicleSample sample : samples) {
            if (sample.getStatus() != null && sample.getStatus().getVehicle() != null) {
                Log log = null;
                if (sample.getStatus().getVehicle().getTractor() != null) {
                    if (sample.getStatus().getVehicle().getTractor().getVehicle_base() != null) {
                        log = fillCoreInfo(trip, vehicle, ghtVehicle, netResult);
                        handleVehicleBaseData(log, sample.getStatus().getVehicle().getTractor().getVehicle_base(), reqDate);
                    }
                } else if (sample.getStatus().getVehicle().getTrailer() != null) {
                    log = fillCoreInfo(trip, vehicle, ghtVehicle, netResult);
                    handleTrailer(log, sample.getStatus().getVehicle().getTrailer(), reqDate);
                }
                if (log != null) logList.add(log);
            }
        }
        return logList;
    }

    private void handleTrailer(Log log, Trailer trailer, Date reqDate) throws Exception {
        if (trailer.getVehicle_base() != null) {
            handleVehicleBaseData(log, trailer.getVehicle_base(), reqDate);
        }
        if (trailer.getTrailer_tire_pressure() != null) {
            //now ignore, but handle it if need.add columns to base
        }
        if (trailer.getDoors() != null) {
            fillDoorsInfo(log, trailer.getDoors());
        }
        if (trailer.getTemperatures() != null) {
            fillTemperature(log, trailer.getTemperatures());
        }
    }

    private Log handleVehicleBaseData(Log log, VehicleBase vehicleBase, Date reqDate) throws Exception {
        if (vehicleBase == null) return log;

        log.setReqDate(reqDate);
      //  log.setGhtVehicleId(vehicleBase.getId().getLicense_plate());
        if (vehicleBase.getServer_timestamp() != null)
            log.setServerDate(getGhtDate(vehicleBase.getServer_timestamp()));

        if (vehicleBase.getTimestamp() != null) {
//            log.setIncomDateStr(vehicleBase.getTimestamp());
            log.setDate(getGhtDate(vehicleBase.getTimestamp()));
        } else {
            throw new Exception("Event timestamp is empty from GhTrack");
        }
        if (vehicleBase.getPower() != null) {
            log.setBatteryVoltage(String.valueOf(vehicleBase.getPower().getBattery_voltage()));
        }
        if (vehicleBase.getPosition() != null && vehicleBase.getPosition().getGps() != null &&
                vehicleBase.getPosition().getGps().getLatitude() != null &&
                vehicleBase.getPosition().getGps().getLongitude() != null) {
            log.setLat(String.valueOf(vehicleBase.getPosition().getGps().getLatitude()));
            log.setLng(String.valueOf(vehicleBase.getPosition().getGps().getLongitude()));
            if (vehicleBase.getPosition().getGps().getSpeed() != null) {
                log.setSpeed(String.valueOf(vehicleBase.getPosition().getGps().getSpeed()));
            }
        }

        return log;
    }

    public Log handleError(
            Trip trip, VehicleInnerEntity vehicle, GhtVehicleInnerEntity ghtVehicle,
            GhtVehicleError error, Integer netResult, Date reqDate) throws Exception{
        Log log = fillCoreInfo(trip, vehicle, ghtVehicle, netResult);
        log.setDate(new Date());
        log.setReqDate(reqDate);
        log.setServerDate(new Date());
        log.setShortDesc(error.getMessage());
        log.setLongDesc("Error number: " + error.getCode() + ", message: " + error.getMessage());
        return log;
    }

    private void fillDoorsInfo(Log log, Doors doors) {
        log.setDoor1State(doors.getDoor1_open());
        log.setDoor2State(doors.getDoor2_open());
        log.setDoor3State(doors.getDoor3_open());
        log.setDoor4State(doors.getDoor4_open());
    }

    private void fillTemperature(Log log, Temperature temperatures) {

        log.setTempSensor1(temperatures.getTemp_1() == null ? null
                : temperatures.getTemp_1() + "");

        log.setTempSensor2(temperatures.getTemp_2() == null ? null
                : temperatures.getTemp_2() + "");

        log.setTempSensor3(temperatures.getTemp_3() == null ? null
                : temperatures.getTemp_3() + "");

        log.setTempSensor4(temperatures.getTemp_4() == null ? null
                : temperatures.getTemp_4() + "");

        log.setTempSensor5(temperatures.getTemp_5() == null ? null
                : temperatures.getTemp_5() + "");

        log.setTempSensor6(temperatures.getTemp_6() == null ? null
                : temperatures.getTemp_6() + "");

        log.setTempSensor7(temperatures.getTemp_7() == null ? null
                : temperatures.getTemp_7() + "");

        log.setTempSensor8(temperatures.getTemp_8() == null ? null
                : temperatures.getTemp_8() + "");

        log.setTempSensor9(temperatures.getTemp_9() == null ? null
                : temperatures.getTemp_9() + "");
    }

    private Log fillCoreInfo(Trip trip, VehicleInnerEntity vehicle, GhtVehicleInnerEntity ghtVehicle, Integer netResult) {
        Log log = new Log(new HashMap<EventType, AnalyzeResult>());

        log.setDateCreated(new Date());
        log.setDateModified(new Date());

        log.setTrip(trip);
        log.setTripId(trip.getId());
        
  //      System.out.println("fillCoreInfo Trip--- "+trip.getId());

        log.setVehicle(vehicle);
        log.setVehicleId(vehicle.getId());
      //  System.out.println("fillCoreInfo VehicleId--- "+vehicle.getId());
     //   System.out.println("fillCoreInfo Vehicle Name--- "+vehicle.getName());
        log.setGhtVehicle(ghtVehicle);
        log.setGhtVehicleId(ghtVehicle.getId());
       // System.out.println("fillCoreInfo GHTVehicleId--- "+ghtVehicle.getId());
        //System.out.println("fillCoreInfo GHTVehicle getTransportCompany--- "+ghtVehicle.getTransportCompany());

        log.setGhtNetResult(netResult);

        return log;
    }
}
