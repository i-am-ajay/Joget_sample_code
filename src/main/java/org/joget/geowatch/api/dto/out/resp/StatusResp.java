package org.joget.geowatch.api.dto.out.resp;

/**
 * Created by k.lebedyantsev
 * Date: 6/20/2018
 * Time: 5:29 PM
 */
public class StatusResp {
    private String id;
    private Integer code;
    private String key;

    public StatusResp(Integer code, String key) {
        this.code = code;
        this.key = key;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
