package org.joget.geowatch.ghtrack;

import org.joget.geowatch.db.dto.GhtVehicle;
import org.joget.geowatch.ght.net.dto.in.resp.log.Id;
import org.joget.geowatch.ght.net.dto.in.resp.error.StatusMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 1:28 PM
 */
public class GhTrackUtil {
    private static final String GH_TRACK_DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static Map<String, ArrayList<StatusMessages>> statusMessagesMap;


    static {
        statusMessagesMap = new HashMap<>();

        for (StatusMessages message :
                StatusMessages.values()) {
            String code = message.toString().replace("ERR_", "").replace("STAT_", "")
                    .replace("_LONG", "").replace("_SHORT", "");
            if (statusMessagesMap.get(code) == null) {
                statusMessagesMap.put(code, new ArrayList<StatusMessages>());
            }
            statusMessagesMap.get(code).add(message);
        }
    }

    public static List<StatusMessages> getMessage(String idAlert) {
        List<StatusMessages> message = statusMessagesMap.get(idAlert);
        if (message != null) {
            return message;
        }
        return statusMessagesMap.get("0");
    }

    private static GhtVehicle remapGhVehicleToVehicleDto(Id ghGhtVehicle) {
        GhtVehicle ghtVehicle = new GhtVehicle();
        ghtVehicle.setId(ghGhtVehicle.getLicense_plate());
        ghtVehicle.setChassisNumber(ghGhtVehicle.getChassis_number());
        ghtVehicle.setLicensePlate(ghGhtVehicle.getLicense_plate());
        ghtVehicle.setName(ghGhtVehicle.getName());
        ghtVehicle.setOperational(ghGhtVehicle.getOperational());
        return ghtVehicle;
    }

    public static List<GhtVehicle> remapListGhVehicleToListVehicleDto(Id[] list) {
        List<GhtVehicle> listGhtVehicleDto = new ArrayList<>();
        if (list != null) {
            for (Id ghtVehicle : list) {
                listGhtVehicleDto.add(remapGhVehicleToVehicleDto(ghtVehicle));
            }
        }
        return listGhtVehicleDto;
    }
}
