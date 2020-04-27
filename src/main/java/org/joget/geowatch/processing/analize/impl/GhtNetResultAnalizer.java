package org.joget.geowatch.processing.analize.impl;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.processing.analize.Analyzer;
import org.joget.geowatch.processing.analize.dto.AnalyzeTripContext;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventType;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.joget.geowatch.type.EventSubType.NONSENSE;
import static org.joget.geowatch.type.EventSubType.SOMETHING;

public class GhtNetResultAnalizer extends Analyzer {
    private static final String TAG = GhtNetResultAnalizer.class.getSimpleName();

    @Override
    public void analyze(EventType eventType, AnalyzeTripContext tripContext, List<Log> logList) throws Exception {
        for (Log log : logList) {
            try {
                analyze(eventType, tripContext, log);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR.");
            }
        }
    }

    @Override
    public void analyze(EventType eventType, AnalyzeTripContext tripContext, Log log) throws Exception {
        log.getAnalyzeResult().put(eventType,
                SC_OK == log.getGhtNetResult()
                        ? new AnalyzeResult(eventType, NONSENSE)
                        : new AnalyzeResult(eventType, SOMETHING));
    }
}
