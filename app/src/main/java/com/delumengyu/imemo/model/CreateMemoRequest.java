package com.delumengyu.imemo.model;

public class CreateMemoRequest {
    private String content;
    private String visibility;

    public CreateMemoRequest(String content, String visibility) {
        this.content = content;
        this.visibility = visibility;
    }
} 