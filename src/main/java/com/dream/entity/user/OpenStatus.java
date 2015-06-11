package com.dream.entity.user;

/**
 * Created by Knight on 2015/6/11 16:33.
 */
public enum OpenStatus {

    OPEN("公开"),

    CLOSED("不公开"),

    AUTHORIZE("授权后公开");

    OpenStatus(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
