package com.dream.dto.excel;

public class BasePropMap {

    private String name;
    private String prop;

    public BasePropMap(String name, String prop) {
        this.name = name;
        this.prop = prop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }
}
