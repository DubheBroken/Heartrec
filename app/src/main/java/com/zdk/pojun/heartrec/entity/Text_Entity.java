package com.zdk.pojun.heartrec.entity;

/**
 * Created by Zero on 2017/2/16.
 */

public class Text_Entity {
    private String id;//主键ID
    private String substance;//条目内容
    private String time;//最后修改时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
