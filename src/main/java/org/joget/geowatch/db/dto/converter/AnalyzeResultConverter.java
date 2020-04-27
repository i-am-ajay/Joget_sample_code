package org.joget.geowatch.db.dto.converter;

import com.google.gson.reflect.TypeToken;
import org.joget.commons.util.LogUtil;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventType;

import javax.persistence.AttributeConverter;
import java.util.Map;

import static org.joget.geowatch.util.StrUtil.getObj;
import static org.joget.geowatch.util.StrUtil.getStr;

public class AnalyzeResultConverter implements AttributeConverter<Map<EventType, AnalyzeResult>, String> {
    private static final String TAG = AnalyzeResultConverter.class.getSimpleName();

    @Override
    public String convertToDatabaseColumn(Map<EventType, AnalyzeResult> analyzeResult) {
        return getStr(analyzeResult);
    }

    @Override
    public Map<EventType, AnalyzeResult> convertToEntityAttribute(String data) {
        try {
            return getObj(data, new TypeToken<Map<EventType, AnalyzeResult>>() {});
        } catch (Exception e) {
            LogUtil.error(TAG, e, "ERROR");
            throw new RuntimeException(e);
        }
    }
}