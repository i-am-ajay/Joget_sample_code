package org.joget.geowatch.db.dto;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.Type;
import org.joget.geowatch.db.dto.converter.*;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.RouteMapInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleLastPositionInnerEntity;
import org.joget.geowatch.db.dto.type.JobType;
import org.joget.geowatch.db.dto.type.TripLifeStateType;
import org.joget.geowatch.db.dto.type.TripLifeSubStateType;
import org.joget.geowatch.db.dto.type.VehicleType;
import org.joget.geowatch.db.dto.type.YesNoType;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 3:21 PM
 */
@Entity
@Table(name = "app_fd_Trip")
public class Trip implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Type(type = "text")
    @Column(name = "c__id")
    private String c__id;

    @Expose
    @Column(name = "dateCreated", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Expose
    @Column(name = "dateModified", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

//    @Type(type = "text")
//    @Column(name = "c_requesterDepartmentName" )
//    private String requesterDepartmentName;

    @Convert(converter = JobTypeConverter.class)
    @Column(name = "c_jobType", columnDefinition = "longtext", updatable = false)
    private JobType jobType;

    @Type(type = "text")
    @Column(name = "c_rmoId", updatable = false)
    private String rmoId;

    @Type(type = "text")
    @Column(name = "c_invoice", updatable = false)
    private String invoice;

    @Type(type = "text")
    @Column(name = "c_requesterId", updatable = false)
    private String requesterId;

    @Type(type = "text")
    @Column(name = "c_routeId", updatable = false)
    private String routeId;

    @Type(type = "text")
    @Column(name = "c_rmoDriverId", updatable = false)
    private String rmoDriverId;

    @Type(type = "text")
    @Column(name = "c_rmoCoDriverId", updatable = false)
    private String rmoCoDriverId;

    @Type(type = "text")
    @Column(name = "c_haulierCoDriverId", updatable = false)
    private String haulierCoDriverId;

    @Type(type = "text")
    @Column(name = "c_requesterDepartmentId", updatable = false)
    private String requesterDepartmentId;

    @Type(type = "text")
    @Column(name = "c_rmoRequired", updatable = false)
    private String rmoRequired;

    @Type(type = "text")
    @Column(name = "c_requesterOrganizationId", updatable = false)
    private String requesterOrganizationId;

    @Type(type = "text")
    @Column(name = "c_haulierDriverId", updatable = false)
    private String haulierDriverId;

    @Type(type = "text")
    @Column(name = "c_requesterGroupsId", updatable = false)
    private String requesterGroupsId;

    @Type(type = "text")
    @Column(name = "c_haulierId", updatable = false)
    private String haulierId;

    @Type(type = "text")
    @Column(name = "c_haulierDepartmentId", updatable = false)
    private String haulierDepartmentId;

    @Type(type = "text")
    @Column(name = "c_rmoDepartmentId", updatable = false)
    private String rmoDepartmentId;

    @Type(type = "text")
    @Column(name = "c_resolverId", updatable = false)
    private String resolverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_liveState", columnDefinition = "longtext")
    private TripLifeStateType liveState;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_liveSubState", columnDefinition = "longtext")
    private TripLifeSubStateType liveSubState;
 
    //new
    
 @Type(type="text")
 @Column(name="c_Driver1Name", updatable =false)
 private String c_Driver1Name;
    
    
@Type(type = "text")
@Column(name = "c_Driver1Phone", updatable = false)
private String c_Driver1Phone;
    
    
@Type(type = "text")
@Column(name = "c_TruckVanReg", updatable = false)
private String c_TruckVanReg;
    
@Type(type = "text")
@Column(name = "c_TrailerReg", updatable = false)
private String c_TrailerReg;
  
  
@Type(type = "text")
@Column(name = "c_TrackType", updatable = false)
private String c_TrackType;
  
  
@Type(type = "text")
@Column(name = "c_DoorSensor", updatable = false)
private String c_DoorSensor;
  
@Type(type = "text")
@Column(name = "c_TemperatureMonitoring", updatable = false)
private String c_TemperatureMonitoring;
  
  
@Type(type = "text")
@Column(name = "c_PortableDeviceID", updatable = false)
private String c_PortableDeviceID;
  
  
 

//    @Type(type = "text")
//    @Column(name = "c_startDate", updatable = false)
//    private String startDate;
//
//    @Type(type = "text")
//    @Column(name = "c_finishDate", updatable = false)
//    private String finishDate;
//
//    @Type(type = "text")
//    @Column(name = "c_startTime", updatable = false)
//    private String startTime;
//
//    @Type(type = "text")
//    @Column(name = "c_finishTime", updatable = false)
//    private String finishTime;

    @Type(type = "text")
    @Column(name = "c_startDateTime", updatable = false)
    private String startDateTime;

    @Type(type = "text")
    @Column(name = "c_finishDateTime", updatable = false)
    private String finishDateTime;

    @Type(type = "text")
    @Column(name = "c_name", updatable = false)
    private String name;

    @Type(type = "text")
    @Column(name = "c_emailRequesterGroup", updatable = false)
    private String emailRequesterGroup;

    @Type(type = "text")
    @Column(name = "c_emailHaulierGroup", updatable = false)
    private String emailHaulierGroup;

    @Type(type = "text")
    @Column(name = "c_emailRmoGroup", updatable = false)
    private String emailRmoGroup;

    @Type(type = "text")
    @Column(name = "c_emailMonitorGroup", updatable = false)
    private String emailMonitorGroup;

    @Type(type="true_false")
    @Column(name = "c_startReached")
    private Boolean startReached;

    @Type(type="true_false")
    @Column(name = "c_finishReached")
    private Boolean finishReached;

    @Type(type="long")
    @Column(name = "c_startDelay")
    private Long startDelay;

    @Type(type="long")
    @Column(name = "c_finishDelay")
    private Long finishDelay;

    @Convert(converter = RouteMapInnerEntityConverter.class)
    @Column(name = "c_imageRouteMap", columnDefinition = "longtext")
    private RouteMapInnerEntity imageRouteMap;

    @Type(type = "text")
    @Column(name = "c_haulierVehicleId", updatable = false)
    private String haulierVehicleId;

    @Type(type = "text")
    @Column(name = "c_haulierTrailerVehicleId", updatable = false)
    private String haulierTrailerVehicleId;

    @Type(type = "text")
    @Column(name = "c_rmoVehicleId", updatable = false)
    private String rmoVehicleId;

    @Convert(converter = VehicleInnerEntityConverter.class)
    @Column(name = "c_haulierVehicle", columnDefinition = "longtext", updatable = false)
    private VehicleInnerEntity haulierVehicle;

    @Convert(converter = VehicleInnerEntityConverter.class)
    @Column(name = "c_haulierTrailerVehicle", columnDefinition = "longtext", updatable = false)
    private VehicleInnerEntity haulierTrailerVehicle;

    @Convert(converter = VehicleInnerEntityConverter.class)
    @Column(name = "c_rmoVehicle", columnDefinition = "longtext", updatable = false)
    private VehicleInnerEntity rmoVehicle;

    @Convert(converter = GhtVehicleInnerEntityConverter.class)
    @Column(name = "c_haulierGhtVehicle", columnDefinition = "longtext", updatable = false)
    private GhtVehicleInnerEntity haulierGhtVehicle;

    @Convert(converter = GhtVehicleInnerEntityConverter.class)
    @Column(name = "c_haulierTrailerGhtVehicle", columnDefinition = "longtext", updatable = false)
    private GhtVehicleInnerEntity haulierTrailerGhtVehicle;

    @Convert(converter = GhtVehicleInnerEntityConverter.class)
    @Column(name = "c_rmoGhtVehicle", columnDefinition = "longtext", updatable = false)
    private GhtVehicleInnerEntity rmoGhtVehicle;
    
    //new 
    
    
   
   // private GhtVehicleInnerEntity customhaulierGhtVehicle;

    
   // private GhtVehicleInnerEntity customhaulierTrailerGhtVehicle;

   
    
    
    
    

    @Convert(converter = VehicleLastPositionEntityConverter.class)
    @Column(name = "c_haulierLastPosition", columnDefinition = "longtext")
    private VehicleLastPositionInnerEntity haulierLastPosition;

    @Convert(converter = VehicleLastPositionEntityConverter.class)
    @Column(name = "c_haulierTrailerLastPosition", columnDefinition = "longtext")
    private VehicleLastPositionInnerEntity haulierTrailerLastPosition;

    @Convert(converter = VehicleLastPositionEntityConverter.class)
    @Column(name = "c_rmoLastPosition", columnDefinition = "longtext")
    private VehicleLastPositionInnerEntity rmoLastPosition;

    @Type(type = "text")
    @Column(name = "c_haulierTripHistorySnapshot")
    private String haulierTripHistorySnapshot;

    @Type(type = "text")
    @Column(name = "c_trailerTripHistorySnapshot")
    private String trailerTripHistorySnapshot;

    @Type(type = "text")
    @Column(name = "c_rmoTripHistorySnapshot")
    private String rmoTripHistorySnapshot;

    public Trip() {
    }

    public Trip(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC__id() {
        return c__id;
    }

    public void setC__id(String c__id) {
        this.c__id = c__id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getRmoId() {
        return rmoId;
    }

    public void setRmoId(String rmoId) {
        this.rmoId = rmoId;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRmoDriverId() {
        return rmoDriverId;
    }

    public void setRmoDriverId(String rmoDriverId) {
        this.rmoDriverId = rmoDriverId;
    }

    public String getRmoCoDriverId() {
        return rmoCoDriverId;
    }

    public void setRmoCoDriverId(String rmoCoDriverId) {
        this.rmoCoDriverId = rmoCoDriverId;
    }

    public String getHaulierCoDriverId() {
        return haulierCoDriverId;
    }

    public void setHaulierCoDriverId(String haulierCoDriverId) {
        this.haulierCoDriverId = haulierCoDriverId;
    }

    public String getRequesterDepartmentId() {
        return requesterDepartmentId;
    }

    public void setRequesterDepartmentId(String requesterDepartmentId) {
        this.requesterDepartmentId = requesterDepartmentId;
    }

    public String getRmoRequired() {
        return rmoRequired;
    }

    public void setRmoRequired(String rmoRequired) {
        this.rmoRequired = rmoRequired;
    }

    public String getRequesterOrganizationId() {
        return requesterOrganizationId;
    }

    public void setRequesterOrganizationId(String requesterOrganizationId) {
        this.requesterOrganizationId = requesterOrganizationId;
    }

    public String getHaulierDriverId() {
        return haulierDriverId;
    }

    public void setHaulierDriverId(String haulierDriverId) {
        this.haulierDriverId = haulierDriverId;
    }

    public String getRequesterGroupsId() {
        return requesterGroupsId;
    }

    public void setRequesterGroupsId(String requesterGroupsId) {
        this.requesterGroupsId = requesterGroupsId;
    }

    public String getHaulierId() {
        return haulierId;
    }

    public void setHaulierId(String haulierId) {
        this.haulierId = haulierId;
    }

    public String getHaulierDepartmentId() {
        return haulierDepartmentId;
    }

    public void setHaulierDepartmentId(String haulierDepartmentId) {
        this.haulierDepartmentId = haulierDepartmentId;
    }

    public String getRmoDepartmentId() {
        return rmoDepartmentId;
    }

    public void setRmoDepartmentId(String rmoDepartmentId) {
        this.rmoDepartmentId = rmoDepartmentId;
    }

    public String getResolverId() {
        return resolverId;
    }

    public void setResolverId(String resolverId) {
        this.resolverId = resolverId;
    }

    public TripLifeStateType getLiveState() {
        return liveState;
    }

    public void setLiveState(TripLifeStateType liveState) {
        this.liveState = liveState;
    }

    public TripLifeSubStateType getLiveSubState() {
        return liveSubState;
    }

    public void setLiveSubState(TripLifeSubStateType liveSubState) {
        this.liveSubState = liveSubState;
    }
    
    
    //new
    
    public String getC_Driver1Name() {
  		return c_Driver1Name;
  	}

  	public void setC_Driver1Name(String c_Driver1Name) {
  		this.c_Driver1Name = c_Driver1Name;
  	}

  	public String getC_Driver1Phone() {
  		return c_Driver1Phone;
  	}

  	public void setC_Driver1Phone(String c_Driver1Phone) {
  		this.c_Driver1Phone = c_Driver1Phone;
  	}

  	public String getC_TruckVanReg() {
  		return c_TruckVanReg;
  	}

  	public void setC_TruckVanReg(String c_TruckVanReg) {
  		this.c_TruckVanReg = c_TruckVanReg;
  	}

  	public String getC_TrailerReg() {
  		return c_TrailerReg;
  	}

  	public void setC_TrailerReg(String c_TrailerReg) {
  		this.c_TrailerReg = c_TrailerReg;
  	}

  	public String getC_TrackType() {
  		return c_TrackType;
  	}

  	public void setC_TrackType(String c_TrackType) {
  		this.c_TrackType = c_TrackType;
  	}

  	public String getC_DoorSensor() {
  		return c_DoorSensor;
  	}

  	public void setC_DoorSensor(String c_DoorSensor) {
  		this.c_DoorSensor = c_DoorSensor;
  	}

  	public String getC_TemperatureMonitoring() {
  		return c_TemperatureMonitoring;
  	}

  	public void setC_TemperatureMonitoring(String c_TemperatureMonitoring) {
  		this.c_TemperatureMonitoring = c_TemperatureMonitoring;
  	}

  	public String getC_PortableDeviceID() {
  		return c_PortableDeviceID;
  	}

  	public void setC_PortableDeviceID(String c_PortableDeviceID) {
  		this.c_PortableDeviceID = c_PortableDeviceID;
  	}

 
//    public String getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(String startDate) {
//        this.startDate = startDate;
//    }
//
//    public String getFinishDate() {
//        return finishDate;
//    }
//
//    public void setFinishDate(String finishDate) {
//        this.finishDate = finishDate;
//    }
//
//    public String getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(String startTime) {
//        this.startTime = startTime;
//    }
//
//    public String getFinishTime() {
//        return finishTime;
//    }
//
//    public void setFinishTime(String finishTime) {
//        this.finishTime = finishTime;
//    }


    public String getStartDateTime() {
        return startDateTime;
    }

  
	public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getFinishDateTime() {
        return finishDateTime;
    }

    public void setFinishDateTime(String finishDateTime) {
        this.finishDateTime = finishDateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailRequesterGroup() {
        return emailRequesterGroup;
    }

    public void setEmailRequesterGroup(String emailRequesterGroup) {
        this.emailRequesterGroup = emailRequesterGroup;
    }

    public String getEmailHaulierGroup() {
        return emailHaulierGroup;
    }

    public void setEmailHaulierGroup(String emailHaulierGroup) {
        this.emailHaulierGroup = emailHaulierGroup;
    }

    public String getEmailRmoGroup() {
        return emailRmoGroup;
    }

    public void setEmailRmoGroup(String emailRmoGroup) {
        this.emailRmoGroup = emailRmoGroup;
    }

    public String getEmailMonitorGroup() {
        return emailMonitorGroup;
    }

    public void setEmailMonitorGroup(String emailMonitorGroup) {
        this.emailMonitorGroup = emailMonitorGroup;
    }

    public Boolean getStartReached() {
        return startReached;
    }

    public void setStartReached(Boolean startReached) {
        this.startReached = startReached;
    }

    public Boolean getFinishReached() {
        return finishReached;
    }

    public void setFinishReached(Boolean finishReached) {
        this.finishReached = finishReached;
    }

    public Long getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(Long startDelay) {
        this.startDelay = startDelay;
    }

    public Long getFinishDelay() {
        return finishDelay;
    }

    public void setFinishDelay(Long finishDelay) {
        this.finishDelay = finishDelay;
    }

    public RouteMapInnerEntity getImageRouteMap() {
        return imageRouteMap;
    }

    public void setImageRouteMap(RouteMapInnerEntity imageRouteMap) {
        this.imageRouteMap = imageRouteMap;
    }

    public String getHaulierVehicleId() {
        return haulierVehicleId;
    }

    public void setHaulierVehicleId(String haulierVehicleId) {
        this.haulierVehicleId = haulierVehicleId;
    }

    public String getHaulierTrailerVehicleId() {
        return haulierTrailerVehicleId;
    }

    public void setHaulierTrailerVehicleId(String haulierTrailerVehicleId) {
        this.haulierTrailerVehicleId = haulierTrailerVehicleId;
    }

    public String getRmoVehicleId() {
        return rmoVehicleId;
    }

    public void setRmoVehicleId(String rmoVehicleId) {
        this.rmoVehicleId = rmoVehicleId;
    }

    public VehicleInnerEntity getHaulierVehicle() {
    	
    	 if(this.jobType.equals(JobType.CUSTOM_JOB)) {
 	    	
    		 VehicleInnerEntity customhaulierTrailer =new VehicleInnerEntity();
  		   
  		   
    		 customhaulierTrailer.setId(c_TruckVanReg);
    		 customhaulierTrailer.setName(c_TruckVanReg);
  		   	 customhaulierTrailer.setType(VehicleType.HAULIER);
  		   	 customhaulierTrailer.setDoorAlarm(this.c_DoorSensor.equals("YES")?YesNoType.YES:YesNoType.NO);
  			 customhaulierTrailer.setPanicButton(this.c_DoorSensor.equals("YES")?YesNoType.YES:YesNoType.NO);
  			 
  		   	 return customhaulierTrailer; 
  	
  	   }
  	 else 
       {
       	 return haulierVehicle;
       	 
       }
       
     
        
    }

    public void setHaulierVehicle(VehicleInnerEntity haulierVehicle) {
        this.haulierVehicle = haulierVehicle;
    }

    public VehicleInnerEntity getHaulierTrailerVehicle() {
    	
    	 if(this.jobType.equals(JobType.CUSTOM_JOB)) {
    		 
    		 if(c_TrailerReg==null||c_TrailerReg.isEmpty())
  			   return null;
    		 VehicleInnerEntity customhaulierTrailer =new VehicleInnerEntity();
  		   
  		   
    		 customhaulierTrailer.setId(c_TrailerReg);
    		 customhaulierTrailer.setName(c_TrailerReg);
    	 	 customhaulierTrailer.setType(VehicleType.TRAILER);
  		   	 customhaulierTrailer.setDoorAlarm(this.c_DoorSensor.equals("YES")?YesNoType.YES:YesNoType.NO);
  			 customhaulierTrailer.setPanicButton(this.c_DoorSensor.equals("YES")?YesNoType.YES:YesNoType.NO);
  			
  		   
  		   return customhaulierTrailer; 
  	
  	   }
  	 else 
       {
       	 return haulierTrailerVehicle;
       	 
       }
       
    }

    public void setHaulierTrailerVehicle(VehicleInnerEntity haulierTrailerVehicle) {
        this.haulierTrailerVehicle = haulierTrailerVehicle;
    }

    public VehicleInnerEntity getRmoVehicle() {
        return rmoVehicle;
    }

    public void setRmoVehicle(VehicleInnerEntity rmoVehicle) {
        this.rmoVehicle = rmoVehicle;
    }

    public GhtVehicleInnerEntity getHaulierGhtVehicle() {
    	
    	
    	// return haulierGhtVehicle;
    	
    	 
      
       if(this.jobType.equals(JobType.CUSTOM_JOB)) {
        	
	      
    	  GhtVehicleInnerEntity  customhaulierGhtVehicle = new GhtVehicleInnerEntity();
	        	
	      customhaulierGhtVehicle.setId(c_TruckVanReg);
	      customhaulierGhtVehicle.setTransportCompany("self");
	         
        
    	   return customhaulierGhtVehicle;
        }
        else 
        {
        	 return haulierGhtVehicle;
        	 
        }
  
  
    }

    public void setHaulierGhtVehicle(GhtVehicleInnerEntity haulierGhtVehicle) {
        this.haulierGhtVehicle = haulierGhtVehicle;
    }

    public GhtVehicleInnerEntity getHaulierTrailerGhtVehicle() {
    	
    	
    	 //return haulierTrailerGhtVehicle;
    	
    	
    	   if(this.jobType.equals(JobType.CUSTOM_JOB)) {
    	
    		   if(c_TrailerReg==null||c_TrailerReg.isEmpty())
    			   return null;
    		   GhtVehicleInnerEntity customhaulierTrailerGhtVehicle =new GhtVehicleInnerEntity();
    		   
    		   
    		   customhaulierTrailerGhtVehicle.setId(c_TrailerReg);
    		   customhaulierTrailerGhtVehicle.setTransportCompany("self");
    		   
    		   return customhaulierTrailerGhtVehicle; 
    	
    	   }
    	 else 
         {
         	 return haulierTrailerGhtVehicle;
         	 
         }
    	
       
    }

    public void setHaulierTrailerGhtVehicle(GhtVehicleInnerEntity haulierTrailerGhtVehicle) {
        this.haulierTrailerGhtVehicle = haulierTrailerGhtVehicle;
    }

    public GhtVehicleInnerEntity getRmoGhtVehicle() {
        return rmoGhtVehicle;
    }

    public void setRmoGhtVehicle(GhtVehicleInnerEntity rmoGhtVehicle) {
        this.rmoGhtVehicle = rmoGhtVehicle;
    }

    public VehicleLastPositionInnerEntity getHaulierLastPosition() {
        return haulierLastPosition;
    }

    public void setHaulierLastPosition(VehicleLastPositionInnerEntity haulierLastPosition) {
        this.haulierLastPosition = haulierLastPosition;
    }

    public VehicleLastPositionInnerEntity getHaulierTrailerLastPosition() {
        return haulierTrailerLastPosition;
    }

    public void setHaulierTrailerLastPosition(VehicleLastPositionInnerEntity haulierTrailerLastPosition) {
        this.haulierTrailerLastPosition = haulierTrailerLastPosition;
    }

    public VehicleLastPositionInnerEntity getRmoLastPosition() {
        return rmoLastPosition;
    }

    public void setRmoLastPosition(VehicleLastPositionInnerEntity rmoLastPosition) {
        this.rmoLastPosition = rmoLastPosition;
    }

    public String getHaulierTripHistorySnapshot() {
        return haulierTripHistorySnapshot;
    }

    public void setHaulierTripHistorySnapshot(String haulierTripHistorySnapshot) {
        this.haulierTripHistorySnapshot = haulierTripHistorySnapshot;
    }

    public String getTrailerTripHistorySnapshot() {
        return trailerTripHistorySnapshot;
    }

    public void setTrailerTripHistorySnapshot(String trailerTripHistorySnapshot) {
        this.trailerTripHistorySnapshot = trailerTripHistorySnapshot;
    }

    public String getRmoTripHistorySnapshot() {
        return rmoTripHistorySnapshot;
    }

    public void setRmoTripHistorySnapshot(String rmoTripHistorySnapshot) {
        this.rmoTripHistorySnapshot = rmoTripHistorySnapshot;
    }
}
