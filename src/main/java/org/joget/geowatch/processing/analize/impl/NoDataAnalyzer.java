package org.joget.geowatch.processing.analize.impl;

 
import static org.joget.geowatch.type.EventSubType.NONSENSE;
import static org.joget.geowatch.type.EventSubType.SOMETHING;
 

import java.util.Date;
import java.util.List;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.processing.analize.Analyzer;
import org.joget.geowatch.processing.analize.dto.AnalyzeTripContext;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.processing.dto.LogData;
import org.joget.geowatch.type.EventType;

 

public class NoDataAnalyzer extends Analyzer {
	
	private static final String TAG = NoDataAnalyzer.class.getSimpleName();

	@Override
	public void analyze(EventType eventType, AnalyzeTripContext tripContext, LogData logData) throws Exception {
		
		  analyze(eventType, tripContext, logData.getLogList());

	}
	
	
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

		switch (eventType) {
		case NO_DATA:
		{
			AnalyzeResult result= new AnalyzeResult(eventType, NONSENSE);
			try {
			if(tripContext.getLogService().checkActiveRecords(log.getTripId(), log.getVehicleId()))
			{
				result= new AnalyzeResult(eventType, SOMETHING);
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			log.getAnalyzeResult().put(eventType, result);
			
		
			
		}
			
			break;
		default:
			break;
 
		}
	}

	
	

}
