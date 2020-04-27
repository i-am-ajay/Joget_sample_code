package org.joget.geowatch.db.dto.converter;

import org.joget.geowatch.db.dto.type.JobType;

import javax.persistence.AttributeConverter;

public class JobTypeConverter implements AttributeConverter<JobType, String> {
    private static final String TAG = JobTypeConverter.class.getSimpleName();

    @Override
    public String convertToDatabaseColumn(JobType analyzeResult) {
        return analyzeResult.value();
    }

    @Override
    public JobType convertToEntityAttribute(String data) {
        for (JobType e : JobType.values()) {
            if (e.value().equals(data))
                return e;
        }
        throw new IllegalArgumentException("I can't resolve jobType name: " + data);
    }
}
