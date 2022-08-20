
package com.jeff87218.MasterDuelDeckExport.YgoObject;

import com.google.gson.annotations.SerializedName;

public class YgoObject {

    @SerializedName("ch_name")
    private String chName;
    @SerializedName("en_name")
    private String enName;
    private Long id;
    private Integer mdid;

    public YgoObject() {
    }

    public YgoObject(String chName, String enName, Long id, Integer mdid) {
        this.chName = chName;
        this.enName = enName;
        this.id = id;
        this.mdid = mdid;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMdid() {
        return mdid;
    }

    public void setMdid(Integer mdid) {
        this.mdid = mdid;
    }
}
