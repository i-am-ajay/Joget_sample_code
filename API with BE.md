# API

## Development contracts with BE: Trip, Logs and Alerts API.

1. GET -  Get list of LifeTrip using user permissions by userId from session
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=LifeTrip&logDate={logDate}&alertDate={alertDate}
	logDate and alertDate are not required. Date format: UTC, format ISO-8601. Example: 2016-03-03T00:00:00
	
	```
    [
      {
        "id": "123",
        "startDate": "2016-03-03T00:00:00",
        "endDate": "2016-03-03T00:00:00",
        "name":"Route",
        "vehicleList": [
          {
            "vehicleId": "43242",
            "licensePlate": "GGL_TEST-001",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.123,
              "lng": 22.123
            },
            "allLocations": [
            ]
          },
          {
            "vehicleId": "43242",
            "licensePlate": "GGL_TEST-001",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.123,
              "lng": 22.123
            },
            "allLocations": [
            ]
          }
        ],
        "logList": [
          {
            "vehicleId": "43242",
            "id": "1403309",
            "idGhtLog": "20000",
            "type": "ERR",
            "idTrip":"123",
            "shortDesc": "Cancelled by user",
            "longDesc": "The tour was cancelled by a user before its completion.",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.237049,
              "lng": 21.017532
            }
          },
          {
            "vehicleId": "43242",
            "id": "1403309",
            "idGhtLog": "20000",
            "idTrip":"123",
            "type": "ERR",
            "shortDesc": "Cancelled by user",
            "longDesc": "The tour was cancelled by a user before its completion.",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.237049,
              "lng": 21.017532
            }
          }
        ],
        "alerts": [
          {
            "vehicleId": "43242",
            "id": "20000",
            "idTrip":"123",
            "idGhtAlert": "20000",
            "type": "ERR",
            "shortDesc": "Cancelled by user",
            "longDesc": "The tour was cancelled by a user before its completion.",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.237049,
              "lng": 21.017532
            }
          },
          {
            "vehicleId": "43242",
            "id": "20000",
            "idTrip":"123",
            "idGhtAlert": "20000",
            "type": "ERR",
            "shortDesc": "Cancelled by user",
            "longDesc": "The tour was cancelled by a user before its completion.",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.237049,
              "lng": 21.017532
            }
          }
        ]
      }
    ]

	```

2. GET -  Get Life Trip by LifeTripId and using user permissions by userId from session
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=LifeTrip&id={LifeTripId}&logDate={logDate}&alertDate={alertDate}
	logDate and alertDate are not required. Date format: UTC, format ISO-8601. Example: 2016-03-03T00:00:00
	
	```
      {
        "id": "123",
        "startDate": "2016-03-03T00:00:00",
        "endDate": "2016-03-03T00:00:00",
        "name":"Route",
        "vehicleList": [
          {
            "vehicleId": "43242",
            "licensePlate": "GGL_TEST-001",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.123,
              "lng": 22.123
            },
            "allLocations": [
              {
                "date": "2016-03-03T00:00:00",
                "currentPosition": {
                  "lat": 52.123,
                  "lng": 22.123
                }
              },
              {
                "date": "2016-03-03T00:00:00",
                "currentPosition": {
                  "lat": 52.123,
                  "lng": 22.123
                }
              }
            ]
          },
          {
            "vehicleId": "43242",
            "licensePlate": "GGL_TEST-001",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.123,
              "lng": 22.123
            },
            "allLocations": [
              {
                "date": "2016-03-03T00:00:00",
                "currentPosition": {
                  "lat": 52.123,
                  "lng": 22.123
                }
              },
              {
                "date": "2016-03-03T00:00:00",
                "currentPosition": {
                  "lat": 52.123,
                  "lng": 22.123
                }
              }
            ]
          }
        ],
        "route": {
          "id": "234",
          "name":"TEST",
          "wayPoints": [
            {
              "name": "pickup",
              "type": "DEFAULT",
              "description":"test",
              "endpointNotificationType":"ALARM",
              "geofenceNotificationType": "ALARM",
              "lat": 55.751244,
              "lng": 37.618423,
              "radius":123,
              "estimatedPauseDuration": 600,
              "estimatedWorkDuration": 900,
              "rta": "2016-03-03T17:00:01",
              "rtaMax": "2016-03-03T17:00:02",
              "timeWindowBegin": "2016-03-03T00:00:00",
              "timeWindowEnd": "2016-03-03T17:00:00"        
            },
            {
              "name": "via_zone",
              "type": "DEFAULT",
              "description":"test",
              "geofenceNotificationType": "ALARM",
              "lat": 53.9,
              "radius": 120,
              "lng": 27.56667,
              "estimatedPauseDuration": 600,
              "estimatedWorkDuration": 900,
              "rta": "2016-03-03T17:00:01",
              "rtaMax": "2016-03-03T17:00:02",
              "timeWindowBegin": "2016-03-03T00:00:00",
              "timeWindowEnd": "2016-03-03T17:00:00"
            },
            {
              "lat": 52.237049,
              "lng": 21.017532
            },
            {
              "type": "DEFAULT",
              "description":"test",
              "endpointNotificationType":"NOTHING",
              "geofenceNotificationType": "ALARM",
              "city": "Prague",
              "country": "EZ",
              "lat": 48.812737,
              "lng": 14.317466,
              "estimatedPauseDuration": 600,
              "estimatedWorkDuration": 900,
              "rta": "2016-03-03T17:00:01",
              "rtaMax": "2016-03-03T17:00:02",
              "timeWindowBegin": "2016-03-03T00:00:00",
              "timeWindowEnd": "2016-03-03T17:00:00",
              "name": "delivery"
            }
          ]
        },
        "logList": [
          {
            "vehicleId": "43242",
            "id": "1403309",
            "idGhtLog": "20000",
            "idTrip":"123",
            "type": "ERR",
            "shortDesc": "Cancelled by user",
            "longDesc": "The tour was cancelled by a user before its completion.",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.237049,
              "lng": 21.017532
            }
          },
          {
            "vehicleId": "43242",
            "id": "1403309",
            "idGhtLog": "20000",
            "idTrip":"123",
            "type": "ERR",
            "shortDesc": "Cancelled by user",
            "longDesc": "The tour was cancelled by a user before its completion.",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.237049,
              "lng": 21.017532
            }
          }
        ],
        "alerts": [
          {
            "vehicleId": "43242",
            "id": "20000",
            "idGhtAlert": "20000",
            "type": "ERR",
            "idTrip":"123",
            "shortDesc": "Cancelled by user",
            "longDesc": "The tour was cancelled by a user before its completion.",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.237049,
              "lng": 21.017532
            }
          },
          {
            "vehicleId": "43242",
            "id": "20000",
            "idTrip":"123",
            "idGhtAlert": "20000",
            "type": "ERR",
            "shortDesc": "Cancelled by user",
            "longDesc": "The tour was cancelled by a user before its completion.",
            "date": "2016-03-03T00:00:00",
            "currentPosition": {
              "lat": 52.237049,
              "lng": 21.017532
            }
          }
        ]
      }
	```
	
3. POST -  Create Life Trip at GhTrack by LifeTripId and using user permissions by userId from session
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=LifeTrip&id={LifeTripId}
	
	Response(success):
	
	```
	{"status": 200}
	```
	
	Response(error):
	
    ```
    {"status": 500}
    ```
   
4. GET -  Get Geo Data by LifeTripId and using user permissions by userId from session
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=LifeTripGeoData&id={LifeTripId}
    logDate and alertDate are not required.
    
    ```
	    [
		    {
                vehicleId: "5416347135",
                deviceId: "76536251",
                time: "unixtime"
                "position": {
                    lat: '1234.432',
                    lng: '1123.32',
		        }
		    },
	    ]
    ```
    
## Development contracts with BE: Property API.

1. GET - Get default property for specific enum type.
  	Permission: All authorized users.
  	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Property&classKey={classKey}
  	"classKey" can be (the list is not complete and will be supplemented): GeofenceType
  	
  	Response:
  	
  	```
  	[
        {
            "type":"GeofenceType.LOAD_ZONE",
            "value":"NOTICE"
        },...
  	]
  	```
    
## Development contracts with BE: Route API.

1. POST - Create Route
	Permission: MonitorAdmin,MonitorUser
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Route
	
	```
	{
      "name": "route-123", // required
      "wayPoints": [
        {
          "name": "Way point 1", // required for point with established field "type"
          "type": "DEFAULT", // not required for point neaded for bulding route at google maps. 
          // Can be:DEFAULT, ALERT_ZONE, PARKING_ZONE, FERRY_PORT_ZONE, BLACKLIST_ZONE. Default value: DEFAULT
          "description":"test",// not required
          "endpointNotificationType":"ALARM",//not required. Notification type for origin and destination points. Can be: NOTHING, NOTICE, WARNING, ALARM 
          "geofenceNotificationType": "ALARM", // not required. Notification type for all types of geozone. Set this if "type" was established. Can be: NOTHING, NOTICE, WARNING, ALARM
          "rta": "2016-03-03T17:00:02", // not required. Requered with established field "type" .Required time of arrival UTC, format ISO-8601
          "rtaMax": "2016-03-03T17:00:02",// not required. Requered with established field "type" .Required time of arrival max UTC, format ISO-8601
          "estimatedPauseDuration": 123, // not required. Estimated work duration in seconds at currentPosition 
          "estimatedWorkDuration": 123, // not required. Estimated pause duration in seconds at currentPosition 
          "timeWindowBegin": "2016-03-03T17:00:02",// not required.UTC, format ISO-8601
          "timeWindowEnd": "2016-03-03T17:00:02",// not required. UTC, format ISO-8601
          "lat": 12.11,// required
          "lng": 12.33,// required
          "radius": 120// Requered with established field "type" 
        },
        {
          "id":123,//if we set to route created geozone
          "name": "Way point 1",
          "type": "ALERT_ZONE",
          "geofenceNotificationType": "ALARM",
          "rta": "2016-03-03T17:00:02",
          "rtaMax": "2016-03-03T17:00:02",
          "estimatedPauseDuration": 123,
          "estimatedWorkDuration": 123,
          "timeWindowBegin": "2016-03-03T17:00:02",
          "timeWindowEnd": "2016-03-03T17:00:02",
          "lat": 12.11,
          "lng": 12.33,
          "radius": 120
        },
        {
          "lat": 12.11,//google points always send as describe here
          "lng": 12.33//google points always send as describe here
        },
        {
          "name": "Way point 1",
          "type": "DEFAULT",
          "geofenceNotificationType": "ALARM",
          "endpointNotificationType":"ALARM",
          "rta": "2016-03-03T17:00:02",
          "rtaMax": "2016-03-03T17:00:02",
          "estimatedPauseDuration": 100,
          "estimatedWorkDuration": 123,
          "timeWindowBegin": "2016-03-03T17:00:02",
          "timeWindowEnd": "2016-03-03T17:00:02",
          "lat": 34.11,
          "lng": 1.33,
          "radius": 120
        }
      ]
    }
	```
	
	Response:
	
	```
	{
		"id": "6723465"
	}
	```
	
	Response(not valide user): HTTP_ERROR_401
	
2. PUT - Edit Route	
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Route&id={idRoute}
	Permission: MonitorAdmin,MonitorUser
	```
	{
      "name": "route-123",
      "wayPoints": [
        {
          "id":"12312", // requered for points with esteblished field "type". If created new point  - not set this. If remove old - don't send object
          "name": "Way point 1", // can be edit(for old point)
          "description":"Test desc",// can be edit(for old point)
          "type": "DEFAULT",
          "endpointNotificationType":"ALARM",//not required. Notification type for origin and destination points
          "geofenceNotificationType": "ALARM",
          "rta": "2016-03-03T17:00:02",
          "rtaMax": "2016-03-03T17:00:02",
          "estimatedPauseDuration": "2016-03-03T17:00:02",
          "estimatedWorkDuration": "2016-03-03T17:00:02",
          "timeWindowBegin": "2016-03-03T17:00:02",
          "timeWindowEnd": "2016-03-03T17:00:02"
        },
        {
          "name": "Way point 1",
          "description":"Test desc",
          "type": "ALERT_ZONE",
          "endpointNotificationType":"ALARM",
          "rta": "2016-03-03T17:00:02",
          "rtaMax": "2016-03-03T17:00:02",
          "estimatedPauseDuration": "2016-03-03T17:00:02",
          "estimatedWorkDuration": "2016-03-03T17:00:02",
          "timeWindowBegin": "2016-03-03T17:00:02",
          "timeWindowEnd": "2016-03-03T17:00:02",
          "lat": 12.11,// it's new point. need coordinates. at old point coordinates don't set
          "lng": 12.33,// it's new point. need coordinates. at old point coordinates don't set
          "radius": 120 // it's new point. need coordinates. at old point coordinates don't set
        },
        {
          "lat": 12.11,//google points always send as describe here
          "lng": 12.33
        },
        {
          "id":"12312",
          "name": "Way point 1",
          "description":"Test desc",
          "type": "DEFAULT",
          "endpointNotificationType":"ALARM",//not required. Notification type for origin and destination points
          "geofenceNotificationType": "ALARM",
          "rta": "2016-03-03T17:00:02",
          "rtaMax": "2016-03-03T17:00:02",
          "estimatedPauseDuration": "2016-03-03T17:00:02",
          "estimatedWorkDuration": "2016-03-03T17:00:02",
          "timeWindowBegin": "2016-03-03T17:00:02",
          "timeWindowEnd": "2016-03-03T17:00:02"
        }
      ]
    }
	```
	Response:
	
	```
	{
		"status": "200"
	}
	
	```
	Response(not valide user): HTTP_ERROR_401
	
3. GET - Get Route List
    Permission: MonitorAdmin,MonitorUser
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Route
	Response:
	
	```
	[
      {
        "id": "123123123",
        "name": "route-123",
        "wayPoints": [
          {
            "id":"12312", // requered for points with esteblished field "type"
            "name": "Way point 1",
            "description":"Test desc",
            "type": "DEFAULT",
            "endpointNotificationType":"ALARM",//not required. Notification type for origin and destination points
            "geofenceNotificationType": "ALARM",
            "rta": "2016-03-03T17:00:02",
            "rtaMax": "2016-03-03T17:00:02",
            "estimatedPauseDuration": "2016-03-03T17:00:02",
            "estimatedWorkDuration": "2016-03-03T17:00:02",
            "timeWindowBegin": "2016-03-03T17:00:02",
            "timeWindowEnd": "2016-03-03T17:00:02",
            "lat": 12.11,
            "lng": 12.33,
            "radius": 120 
          },
          {
            "id":"12312",
            "name": "Way point 1",
            "description":"Test desc",
            "type": "ALERT_ZONE",
            "geofenceNotificationType": "ALARM",
            "rta": "2016-03-03T17:00:02",
            "rtaMax": "2016-03-03T17:00:02",
            "estimatedPauseDuration": "2016-03-03T17:00:02",
            "estimatedWorkDuration": "2016-03-03T17:00:02",
            "timeWindowBegin": "2016-03-03T17:00:02",
            "timeWindowEnd": "2016-03-03T17:00:02",
            "lat": 12.11,
            "lng": 12.33,
            "radius": 120
          },
          {
            "lat": 12.11,
            "lng": 12.33
          },
          {
            "id":"12312",
            "name": "Way point 1",
            "description":"Test desc",
            "type": "DEFAULT",
            "geofenceNotificationType": "ALARM",
            "rta": "2016-03-03T17:00:02",
            "rtaMax": "2016-03-03T17:00:02",
            "estimatedPauseDuration": "2016-03-03T17:00:02",
            "estimatedWorkDuration": "2016-03-03T17:00:02",
            "timeWindowBegin": "2016-03-03T17:00:02",
            "timeWindowEnd": "2016-03-03T17:00:02",
            "lat": 34.11,
            "lng": 1.33,
            "radius": 120
          }
        ]
      }, ....
    ]
	```
	
	Response(not valide user): HTTP_ERROR_401	
4. GET - Get Route by Id
    Permission: MonitorAdmin,MonitorUser
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Route&id={idRoute}
	Response:
	
	```
	{
      "id": "123123123",
      "name": "route-123",
      "wayPoints": [
        {
          "id":"12312", // requered for points with esteblished field "type"
          "name": "Way point 1",
          "description":"Test desc",
          "type": "DEFAULT",
          "geofenceNotificationType": "ALARM",
          "endpointNotificationType": "ALARM",
          "rta": "2016-03-03T17:00:02",
          "rtaMax": "2016-03-03T17:00:02",
          "estimatedPauseDuration": "2016-03-03T17:00:02",
          "estimatedWorkDuration": "2016-03-03T17:00:02",
          "timeWindowBegin": "2016-03-03T17:00:02",
          "timeWindowEnd": "2016-03-03T17:00:02",
          "lat": 12.11,
          "lng": 12.33,
          "radius": 120 
        },
        {
          "id":"12312",
          "name": "Way point 2",
          "description":"Test desc",
          "type": "ALERT_ZONE",
          "geofenceNotificationType": "ALARM",
          "rta": "2016-03-03T17:00:02",
          "rtaMax": "2016-03-03T17:00:02",
          "estimatedPauseDuration": "2016-03-03T17:00:02",
          "estimatedWorkDuration": "2016-03-03T17:00:02",
          "timeWindowBegin": "2016-03-03T17:00:02",
          "timeWindowEnd": "2016-03-03T17:00:02",
          "lat": 12.11,
          "lng": 12.33,
          "radius": 120
        },
        {
          "lat": 12.11,
          "lng": 12.33
        },
        {
          "id":"12312",
          "name": "Way point 3",
          "description":"Test desc",
          "type": "DEFAULT",
          "geofenceNotificationType": "ALARM",
          "endpointNotificationType": "ALARM",
          "rta": "2016-03-03T17:00:02",
          "rtaMax": "2016-03-03T17:00:02",
          "estimatedPauseDuration": "2016-03-03T17:00:02",
          "estimatedWorkDuration": "2016-03-03T17:00:02",
          "timeWindowBegin": "2016-03-03T17:00:02",
          "timeWindowEnd": "2016-03-03T17:00:02",
          "lat": 34.11,
          "lng": 1.33,
          "radius": 120
        }
      ]
    }
	```
    Response(not valide user): HTTP_ERROR_401	
5. DELETE - Delete Route by Id
    Permission: MonitorAdmin,MonitorUser
	/jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Route&id={idRoute}
	Response:
	
	```
	{status: "200"}
	
	or
	
	{status: "500"}
	```
	Response(not valide user): HTTP_ERROR_401
## Development contracts with BE: Vehicle API.

1. GET - Get list of vehicleList:
    Permission: HaulierAdmin,HaulierUser,RMOAdmin,RMOUser,SuperAdminGroup,MonitorAdmin,MonitorUser
    /jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Vehicle
    Response:
    
    ```
    [
      {
        "vehicleId": "1297370072",
        "licensePlate": "1297370072",
        "name": "1297370072"
      },
      ...
    ]
    ```
    Response(not valide user): HTTP_ERROR_401
2. GET - Get vehicle by vehicleId:
    Permission: HaulierAdmin,HaulierUser,RMOAdmin,RMOUser,SuperAdminGroup,MonitorAdmin,MonitorUser
   /jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Vehicle&id={vehicleId}
   Response:
        
    ```
      {
        "vehicleId": "1297370072",
        "licensePlate": "1297370072",
        "name": "1297370072"
      }
    ```
    Response(not valide user): HTTP_ERROR_401
    
3. PUT - Update vehicle by vehicleId:
    Permission: HaulierAdmin,HaulierUser,RMOAdmin,RMOUser
   /jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Vehicle&id={vehicleId}
   Request:
        
    ```
      {
        "vehicleId": "1297370072",
        "licensePlate": "1297370072",
        "name": "1297370072"
      }
    ```
    
    Response:
    	
    ```
    {status: "200"}
    	
    or
    	
    {status: "500"}
    ```
    Response(not valide user): HTTP_ERROR_401
4. DELETE - Delete vehicle by vehicleId:
    Permission: HaulierAdmin,HaulierUser,RMOAdmin,RMOUser
   /jw/web/json/plugin/org.joget.geowatch.api.configuration.MapApi/service?action=Vehicle&id={vehicleId}

    Response:
    	
    ```
    {status: "200"}
    	
    or
    	
    {status: "500"}
    ```
    Response(not valide user): HTTP_ERROR_401