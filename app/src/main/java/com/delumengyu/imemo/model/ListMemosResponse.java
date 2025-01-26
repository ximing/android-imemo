package com.delumengyu.imemo.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListMemosResponse {
    private List<Memo> memos;
    @SerializedName("nextPageToken")
    private String nextPageToken;

    public List<Memo> getMemos() {
        return memos;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
} 