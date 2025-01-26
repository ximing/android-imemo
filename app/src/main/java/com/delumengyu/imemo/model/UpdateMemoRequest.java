package com.delumengyu.imemo.model;

public class UpdateMemoRequest {
    private String content;
    private String visibility;
    private String rowStatus;
    private Boolean pinned;

    public UpdateMemoRequest() {}

    public void setContent(String content) {
        this.content = content;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setRowStatus(String rowStatus) {
        this.rowStatus = rowStatus;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }
} 