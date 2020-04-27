package org.joget.geowatch.api.dto.out.resp;

import org.joget.geowatch.type.ILabel;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TypeOutResp {

    private transient ILabel odj;
    private String type;
    private String label;

    public static TypeOutResp getInstance(ILabel odj){
        if(odj == null) return null;

        TypeOutResp item = new TypeOutResp();
        item.odj = odj;
        item.type = odj.name();
        item.label = !isEmpty(odj.label()) ? odj.label() : odj.name();
        return item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ILabel getOdj() {
        return odj;
    }

    public void setOdj(ILabel odj) {
        this.odj = odj;
    }
}
