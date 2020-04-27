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
			log.getAnalyzeResult().put(eventType, analyze(eventType,log));
			break;
		default:
			break;
 
		}
	}

	public AnalyzeResult analyze(EventType eventType, Log log) throws Exception {
		
		
		System.out.println("In no data analyzer");
		System.out.println(log.getLat());
		System.out.println(log.getDateModified());

		if (log != null && (log.getLat()) == null || log.getLat().isEmpty() ) {

			Date d = log.getDateModified();

			if (isWhitinOneHOur(d)) {
				
				System.out.println(isWhitinOneHOur(d));
				System.out.println("In no data analyzer if block");
				
				return new AnalyzeResult(eventType, NONSENSE);
			}
			else {
				
				System.out.println("In no data analyzer else block");
				
				return new AnalyzeResult(eventType, SOMETHING);
			}
		}

		return new AnalyzeResult(eventType, NONSENSE);

	}
		 
	
	
	
	public static boolean isWhitinOneHOur(Date date) throws Exception {
		Date now = new Date();
		long oneHour = 1000 * 60 * 60;
		
		System.out.println("In no data analyzer isWhitinOneHOur oneHour :"+oneHour);
		
		System.out.println("In no data analyzer isWhitinOneHOur date.getTime() :"+ date.getTime());
		
		System.out.println("In no data analyzer isWhitinOneHOur date.getTime() :"+ now.getTime());
		
	 
		
		 
		System.out.println((now.getTime() + oneHour) >= date.getTime());
		System.out.println((now.getTime() - oneHour) <= date.getTime());
		
		if ((now.getTime() + oneHour) >= date.getTime() && (now.getTime() - oneHour) <= date.getTime()) {
			return true;
		} else {
			return false;
		}
	}
	

}
