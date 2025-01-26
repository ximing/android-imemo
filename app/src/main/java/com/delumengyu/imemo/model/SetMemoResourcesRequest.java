package com.delumengyu.imemo.model;

import java.util.List;

public class SetMemoResourcesRequest {
    private List<SetMemoResourcesRequestItem> resources;

    public SetMemoResourcesRequest(List<SetMemoResourcesRequestItem> resources) {
        this.resources = resources;
    }

    public List<SetMemoResourcesRequestItem> getResources() {
        return resources;
    }
} 