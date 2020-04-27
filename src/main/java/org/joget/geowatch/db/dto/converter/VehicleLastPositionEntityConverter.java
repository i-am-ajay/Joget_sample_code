package org.joget.geowatch.db.dto.converter;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.inner.VehicleLastPositionInnerEntity;

import javax.persistence.AttributeConverter;

import static org.joget.geowatch.util.StrUtil.getObj;
import static org.joget.geowatch.util.StrUtil.getStr;

/**
 * Created by k.lebedyantsev
 * Date: 6/26/2018
 * Time: 12:49 PM
 */
public class VehicleLastPositionEntityConverter implements AttributeConverter<VehicleLastPositionInnerEntity, String> {
    private static final String TAG = VehicleLastPositionEntityConverter.class.getSimpleName();
    @Override
    public String convertToDatabaseColumn(VehicleLastPositionInnerEntity vehicleLastPositionInnerEntity) {
        return getStr(vehicleLastPositionInnerEntity);
    }

    @Override
    public VehicleLastPositionInnerEntity convertToEntityAttribute(String dbData) {
        try {
            return getObj(dbData, VehicleLastPositionInnerEntity.class);
        } catch (Exception e) {
            LogUtil.error(TAG, e, "ERROR");
            throw new RuntimeException(e);
        }
    }
}
