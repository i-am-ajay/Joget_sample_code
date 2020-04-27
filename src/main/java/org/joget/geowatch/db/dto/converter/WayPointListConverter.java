package org.joget.geowatch.db.dto.converter;

import com.google.gson.reflect.TypeToken;
import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;
import org.joget.geowatch.util.StrUtil;

import javax.persistence.AttributeConverter;

import java.util.List;

import static org.joget.geowatch.util.StrUtil.getStr;

public class WayPointListConverter implements AttributeConverter<List<WayPointInnerEntity>, String> {
    private static final String TAG = WayPointListConverter.class.getSimpleName();

    @Override
    public String convertToDatabaseColumn(List<WayPointInnerEntity> data) {
        return getStr(data);
    }

    @Override
    public List<WayPointInnerEntity> convertToEntityAttribute(String data) {
        try {
            return StrUtil.getList(data, new TypeToken<List<WayPointInnerEntity>>() {
            });
        } catch (Exception e) {
            LogUtil.error(TAG, e, "ERROR");
            throw new RuntimeException(e);
        }
    }
}
