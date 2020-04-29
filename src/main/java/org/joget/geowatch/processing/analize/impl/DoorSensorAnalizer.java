package org.joget.geowatch.processing.analize.impl;

import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.processing.analize.Analyzer;
import org.joget.geowatch.processing.analize.dto.AnalyzeTripContext;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.processing.dto.LogData;
import org.joget.geowatch.type.EventSubType;
import org.joget.geowatch.type.EventType;

public class DoorSensorAnalizer extends Analyzer {
    private static final String TAG = DoorSensorAnalizer.class.getSimpleName();



    @Override
    public void analyze(EventType eventType, AnalyzeTripContext tripContext, Log log) throws Exception {
        switch (eventType) {
            case DOOR1:
                log.getAnalyzeResult().put(eventType, analyze(eventType, log.getDoor1State()));
                break;
            case DOOR2:
                log.getAnalyzeResult().put(eventType, analyze(eventType, log.getDoor2State()));
                break;
            case DOOR3:
                log.getAnalyzeResult().put(eventType, analyze(eventType, log.getDoor3State()));
                break;
            case DOOR4:
                log.getAnalyzeResult().put(eventType, analyze(eventType, log.getDoor4State()));
                break;
            default:
                throw new IllegalArgumentException("The eventType: " + eventType + "doesn't support.");
        }
    }

    protected AnalyzeResult analyze(EventType eventType, Boolean doorStatus) {
    	
    	//System.out.println("EVENT "+eventType+" state = "+doorStatus);
        if (doorStatus == null)
            return new AnalyzeResult(eventType, EventSubType.UNKNOWN);
        else if (!doorStatus)
            return new AnalyzeResult(eventType, EventSubType.NONSENSE);
        else
            return new AnalyzeResult(eventType, EventSubType.SOMETHING);
    }

}
