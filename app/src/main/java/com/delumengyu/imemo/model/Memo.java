package com.delumengyu.imemo.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Memo {
    @SerializedName("uid")
    private String uid;

    @SerializedName("name")
    private String name;

    @SerializedName("content")
    private String content;

    @SerializedName("rowStatus")
    private String rowStatus;

    @SerializedName("createTime")
    private Date date;
    private boolean pinned;
    private MemoVisibility visibility;
    private List<Resource> resources;
    @SerializedName("property")
    private MemoProperty property;
    @SerializedName("creator")
    private String creatorId;

    public String getUId() {
        return uid;
    }

    public String getName(){
        return name;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public boolean isPinned() {
        return pinned;
    }

    public MemoVisibility getVisibility() {
        return visibility;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<String> getTags() {
        return property != null ? property.getTags() : null;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public static class MemoProperty {
        private List<String> tags;

        public List<String> getTags() {
            return tags;
        }
    }
}