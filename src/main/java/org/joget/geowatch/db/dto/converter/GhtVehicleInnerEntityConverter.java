package org.joget.geowatch.db.dto.converter;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;

import javax.persistence.AttributeConverter;

import static org.joget.geowatch.util.StrUtil.getObj;
import static org.joget.geowatch.util.StrUtil.getStr;

public class GhtVehicleInnerEntityConverter implements AttributeConverter<GhtVehicleInnerEntity, String> {
    private static final String TAG = GhtVehicleInnerEntityConverter.class.getSimpleName();

    @Override
    public String convertToDatabaseColumn(GhtVehicleInnerEntity analyzeResult) {
        return getStr(analyzeResult);
    }

    @Override
    public GhtVehicleInnerEntity convertToEntityAttribute(String data) {
        try {
            return getObj(data, GhtVehicleInnerEntity.class);
        } catch (Exception e) {
            LogUtil.error(TAG, e, "ERROR");
            throw new RuntimeException(e);
        }
    }
}