package com.delumengyu.imemo.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private int id;
    private String name;
    private String role;
    private String username;
    private String email;
    private String nickname;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    private String description;
    private String rowStatus;
    
    @SerializedName("createTime")
    private String createTime;
    
    @SerializedName("updateTime")
    private String updateTime;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getRowStatus() {
        return rowStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }
} 