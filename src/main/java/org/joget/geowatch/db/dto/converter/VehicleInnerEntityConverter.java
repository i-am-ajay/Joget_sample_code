package org.joget.geowatch.db.dto.converter;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;

import javax.persistence.AttributeConverter;

import static org.joget.geowatch.util.StrUtil.getObj;
import static org.joget.geowatch.util.StrUtil.getStr;

public class VehicleInnerEntityConverter implements AttributeConverter<VehicleInnerEntity, String> {
    private static final String TAG = VehicleInnerEntityConverter.class.getSimpleName();

    @Override
    public String convertToDatabaseColumn(VehicleInnerEntity analyzeResult) {
        return getStr(analyzeResult);
    }

    @Override
    public VehicleInnerEntity convertToEntityAttribute(String data) {
        try {
            return getObj(data, VehicleInnerEntity.class);
        } catch (Exception e) {
            LogUtil.error(TAG, e, "ERROR");
            throw new RuntimeException(e);
        }
    }
}