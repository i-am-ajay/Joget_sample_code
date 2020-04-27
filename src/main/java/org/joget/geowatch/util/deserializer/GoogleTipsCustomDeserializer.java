package org.joget.geowatch.util.deserializer;

import com.google.gson.*;
import org.joget.geowatch.api.dto.WayPoint;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/29/2018
 * Time: 12:58 PM
 */
public class GoogleTipsCustomDeserializer implements JsonDeserializer<List<WayPoint>> {
    @Override
    public List<WayPoint> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<WayPoint> wayPoints = new ArrayList<>();
        JsonArray jsonArray = element.getAsJsonObject().getAsJsonArray("predictions");
        for (JsonElement jsonObject : jsonArray) {
            WayPoint wayPoint = new WayPoint();
            wayPoint.setName(jsonObject.getAsJsonObject().get("description").getAsString());
            wayPoint.setPlaceId(jsonObject.getAsJsonObject().get("place_id").getAsString());
            wayPoints.add(wayPoint);
        }
        return wayPoints;
    }
}
