package com.delumengyu.imemo.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class User {
    @SerializedName("id")
    private String identifier;
    private String name;
    @SerializedName("createTime")
    private Date startDate;
    @SerializedName("setting")
    private UserSetting setting;

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public MemoVisibility getDefaultVisibility() {
        return setting != null ? setting.getMemoVisibility() : MemoVisibility.PRIVATE;
    }
} 