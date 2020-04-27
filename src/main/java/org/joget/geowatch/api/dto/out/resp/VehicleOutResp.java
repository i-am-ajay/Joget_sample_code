package org.joget.geowatch.api.dto.out.resp;

import org.joget.directory.model.Department;
import org.joget.geowatch.api.dto.Location;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleLastPositionInnerEntity;
import org.joget.geowatch.db.dto.type.VehicleType;

import java.util.List;

import static org.joget.geowatch.util.JogetDepUtil.getDepById;

public class VehicleOutResp {

    protected String vehicleId;
    protected String ghtVehicleId;
    protected String licensePlate;
    protected DepOutResp owner;
    protected VehicleType type;
    protected Location currentPosition;
    protected List<Location> allLocations;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public DepOutResp getOwner() {
        return owner;
    }

    public void setOwner(DepOutResp owner) {
        this.owner = owner;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public Location getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Location currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<Location> getAllLocations() {
        return allLocations;
    }

    public void setAllLocations(List<Location> allLocations) {
        this.allLocations = allLocations;
    }

    static public VehicleOutResp update (
            VehicleOutResp item, VehicleType vehicleType,
            VehicleInnerEntity vehicle, GhtVehicleInnerEntity ghtVehicle,
            VehicleLastPositionInnerEntity vehicleLastPositionEntity, Log log) throws Exception {
        if (item == null) return null;
        if (vehicle == null) return item;
        if (ghtVehicle == null) return item;

        item.type = vehicleType;
        item.vehicleId = vehicle.getId();
        Department dep = getDepById(vehicle.getRequesterDepartmentId());
        if (dep != null) item.owner = DepOutResp.update(new DepOutResp(), dep);

        item.ghtVehicleId = ghtVehicle.getId();
        item.licensePlate = ghtVehicle.getId();

        item = VehicleOutResp.update(item, log, vehicleLastPositionEntity);

        return item;
    }

    static public VehicleOutResp update(VehicleOutResp item, Log log, VehicleLastPositionInnerEntity vehicleLastPositionEntity) {
        if(item == null) return null;

        if (log != null)
            item.setCurrentPosition(Location.update(new Location(), log));

        if (vehicleLastPositionEntity != null)
            item.setCurrentPosition(Location.update(new Location(), vehicleLastPositionEntity));

        return item;
    }
}
