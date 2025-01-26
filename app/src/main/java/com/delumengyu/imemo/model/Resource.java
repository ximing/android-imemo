package com.delumengyu.imemo.model;

import android.net.Uri;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Resource {
    @SerializedName("uid")
    private String identifier;
    @SerializedName("createTime")
    private Date date;
    private String filename;
    private String type;
    private String externalLink;
    private String name;

    public String getIdentifier() {
        return identifier;
    }

    public Date getDate() {
        return date;
    }

    public String getFilename() {
        return filename;
    }

    public String getMimeType() {
        return type;
    }

    public Uri getUri(String host) {
        if (externalLink != null && !externalLink.isEmpty()) {
            return Uri.parse(externalLink);
        }
        return Uri.parse(host)
            .buildUpon()
            .appendPath("file")
            .appendPath(name)
            .appendPath(filename)
            .build();
    }
} 