package org.joget.geowatch.processing.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

public class ArchiveProceess implements Runnable{
	private static final String TAG = ArchiveProceess.class.getSimpleName();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		LogUtil.info(TAG,"Archive is running");
		 Date d1 = new Date();

		    Calendar c1 = Calendar.getInstance();
		    c1.setTime(d1);
		    System.out.println(c1.get(Calendar.DAY_OF_WEEK));

		    if ((c1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) 
		            || (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) 
		    {
		    	LogUtil.debug(TAG,"Archive is running-- it is weekend ");
		    
		    	
		 Connection con = null;
		 try {
	            // retrieve connection from the default datasource
	            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
	            con = ds.getConnection();
	          
	            // execute SQL query
	            if(!con.isClosed()) {
	                PreparedStatement stmt = con.prepareStatement("CREATE table if not EXISTS app_fd_Log_backup like app_fd_Log");
	                	LogUtil.info(TAG, stmt.toString());
	                 stmt.executeUpdate();
	                 
	                 PreparedStatement stmt2 = con.prepareStatement("Insert IGNORE into app_fd_Log_backup  (select * from app_fd_Log where dateCreated<date_add(now(), INTERVAL -180 day) order by dateCreated limit 1000)");
	                 LogUtil.info(TAG, stmt2.toString());
	                 stmt2.executeUpdate();
	                 
	                 PreparedStatement stmt3 = con.prepareStatement("delete from app_fd_Log where id in (select id from app_fd_Log_backup where app_fd_Log_backup.dateCreated<date_add(now(), INTERVAL -180 day) order by dateCreated) limit 1000");
	                 LogUtil.info(TAG, stmt3.toString());
	                 stmt3.executeUpdate();
	                
	            }
	        } catch(Exception e) {
	            LogUtil.error(TAG, e, "Error in Archiving");
	        } finally {
	            //always close the connection after used
	            try {
	                if(con != null) {
	                    con.close();
	                }
	            } catch(SQLException e) {/* ignored */}
	        }
		 }
		  else
		    {
			  LogUtil.info(TAG,"Archive not executed its weekday");
		    }
		
	}
	
}
