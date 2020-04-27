package org.joget.geowatch.api.dto.out.resp;

import org.joget.directory.model.Department;

public class DepOutResp {

    protected String id;
    protected String name;

    public static DepOutResp update(DepOutResp item, Department dep){
        if(item == null) return null;

        item.id = dep.getId();
        item.name = dep.getName();

        return item;
    }
}
