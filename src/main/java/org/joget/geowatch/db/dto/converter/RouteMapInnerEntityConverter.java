package org.joget.geowatch.db.dto.converter;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.RouteMap;
import org.joget.geowatch.db.dto.inner.RouteMapInnerEntity;

import javax.persistence.AttributeConverter;

import static org.joget.geowatch.util.StrUtil.getObj;
import static org.joget.geowatch.util.StrUtil.getStr;

public class RouteMapInnerEntityConverter implements AttributeConverter<RouteMapInnerEntity, String> {
    private static final String TAG = RouteMapInnerEntityConverter.class.getSimpleName();

    @Override
    public String convertToDatabaseColumn(RouteMapInnerEntity data) {
        return getStr(data);
    }

    @Override
    public RouteMapInnerEntity convertToEntityAttribute(String data) {
        try {
            return getObj(data, RouteMapInnerEntity.class);
        } catch (Exception e) {
            LogUtil.error(TAG, e, "ERROR");
            throw new RuntimeException(e);
        }
    }
}