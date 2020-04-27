package org.joget.geowatch.api.dto.out.resp;

import org.joget.geowatch.db.dto.GhtVehicle;

import java.util.List;

public class TripTestOutResp {
    protected String tripId;
    protected List<Vehicle> vehicles;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public static class Vehicle {
        protected String vehicleId;
        protected String vehicleName;
        protected String ghtVehicleId;
        protected String ghtVehicleName;
        protected String vehicleType;
        protected Sensor sensor1;
        protected Sensor sensor2;
        protected Sensor sensor3;
        protected Sensor sensor4;
        protected Sensor sensor5;
        protected Sensor sensor6;
        protected Sensor sensor7;
        protected Sensor sensor8;

        public String getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(String vehicleId) {
            this.vehicleId = vehicleId;
        }

        public String getVehicleName() {
            return vehicleName;
        }

        public void setVehicleName(String vehicleName) {
            this.vehicleName = vehicleName;
        }

        public String getGhtVehicleId() {
            return ghtVehicleId;
        }

        public void setGhtVehicleId(String ghtVehicleId) {
            this.ghtVehicleId = ghtVehicleId;
        }

        public String getGhtVehicleName() {
            return ghtVehicleName;
        }

        public void setGhtVehicleName(String ghtVehicleName) {
            this.ghtVehicleName = ghtVehicleName;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public Sensor getSensor1() {
            return sensor1;
        }

        public void setSensor1(Sensor sensor1) {
            this.sensor1 = sensor1;
        }

        public Sensor getSensor2() {
            return sensor2;
        }

        public void setSensor2(Sensor sensor2) {
            this.sensor2 = sensor2;
        }

        public Sensor getSensor3() {
            return sensor3;
        }

        public void setSensor3(Sensor sensor3) {
            this.sensor3 = sensor3;
        }

        public Sensor getSensor4() {
            return sensor4;
        }

        public void setSensor4(Sensor sensor4) {
            this.sensor4 = sensor4;
        }

        public Sensor getSensor5() {
            return sensor5;
        }

        public void setSensor5(Sensor sensor5) {
            this.sensor5 = sensor5;
        }

        public Sensor getSensor6() {
            return sensor6;
        }

        public void setSensor6(Sensor sensor6) {
            this.sensor6 = sensor6;
        }

        public Sensor getSensor7() {
            return sensor7;
        }

        public void setSensor7(Sensor sensor7) {
            this.sensor7 = sensor7;
        }

        public Sensor getSensor8() {
            return sensor8;
        }

        public void setSensor8(Sensor sensor8) {
            this.sensor8 = sensor8;
        }

        public Vehicle update(GhtVehicle incoming) {
            if (incoming == null) return this;
            ghtVehicleId = incoming.getId();
            ghtVehicleName = incoming.getName();
            return this;
        }

        public Vehicle update(org.joget.geowatch.db.dto.Vehicle incoming) {
            if (incoming == null) return this;
            vehicleId = incoming.getId();
            vehicleName = incoming.getName();
            vehicleType = incoming.getType();
            return this;
        }
    }

    public static class Sensor {
        protected String name;
        protected String value;

        public Sensor(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
