package com.delumengyu.imemo.model;

public class SetMemoResourcesRequestItem {
    private String name;
    private String uid;

    public SetMemoResourcesRequestItem(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }
} 