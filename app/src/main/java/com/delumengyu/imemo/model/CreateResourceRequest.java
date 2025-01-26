package com.delumengyu.imemo.model;

public class CreateResourceRequest {
    private String filename;
    private String type;
    private String content;
    private String memo;

    public CreateResourceRequest(String filename, String type, String content, String memo) {
        this.filename = filename;
        this.type = type;
        this.content = content;
        this.memo = memo;
    }

    public CreateResourceRequest(String filename, String type, String content) {
        this(filename, type, content, null);
    }

    public String getFilename() {
        return filename;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getMemo() {
        return memo;
    }
} 