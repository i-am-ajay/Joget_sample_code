package org.joget.geowatch.api.dto.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.joget.geowatch.type.ILabel;

import java.lang.reflect.Type;

import static com.google.gson.JsonNull.INSTANCE;

public class EnumAdapter {

    public static class Serializer implements JsonSerializer<ILabel> {

        @Override
        public JsonElement serialize(ILabel src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) return INSTANCE;

            JsonObject jo = new JsonObject();
            jo.addProperty("type", src.name());
            jo.addProperty("label", src.label() != null ? src.label() : src.name());

            return jo;
        }
    }
}
