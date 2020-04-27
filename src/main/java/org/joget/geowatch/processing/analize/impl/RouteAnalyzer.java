package org.joget.geowatch.processing.analize.impl;

import com.google.maps.model.LatLng;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.processing.analize.Analyzer;
import org.joget.geowatch.processing.analize.dto.AnalyzeTripContext;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventType;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joget.geowatch.app.AppProperties.ANALYZE_GEOFENCE_TOLERANCE;
import static org.joget.geowatch.app.AppProperties.ANALYZE_ROUTE_TOLERANCE;
import static org.joget.geowatch.type.EventSubType.NONSENSE;
import static org.joget.geowatch.type.EventSubType.SOMETHING;
import static org.joget.geowatch.type.EventSubType.UNKNOWN;

/**
 * Created by k.lebedyantsev
 * Date: 2/21/2018
 * Time: 4:03 PM
 */
public class RouteAnalyzer extends Analyzer {
    private static final String TAG = RouteAnalyzer.class.getSimpleName();

    @Override
    public void analyze(EventType eventType, AnalyzeTripContext tripContext, Log log) throws Exception {
        if (isEmpty(log.getLat()) || isEmpty(log.getLng())) {
            log.getAnalyzeResult().put(eventType, new AnalyzeResult(eventType, UNKNOWN));
            return;
        }

        LatLng point = new LatLng(Double.parseDouble(log.getLat()), Double.parseDouble(log.getLng()));
        log.getAnalyzeResult().put(eventType, analyze(eventType, tripContext, point));
    }

    private AnalyzeResult analyze(EventType eventType, AnalyzeTripContext tripContext, LatLng point) {

        for (ExGeo geo : tripContext.getGeoList()) {
            if (geo.isWithin(point, ANALYZE_GEOFENCE_TOLERANCE))
                return new AnalyzeResult(eventType, NONSENSE);
        }

        if (tripContext.getRoute().isWithin(point, ANALYZE_ROUTE_TOLERANCE))
            return new AnalyzeResult(eventType, NONSENSE);

        return new AnalyzeResult(eventType, SOMETHING);
    }
}
