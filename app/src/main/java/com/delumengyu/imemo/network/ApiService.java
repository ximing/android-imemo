package com.delumengyu.imemo.network;

import com.delumengyu.imemo.model.ListResourceResponse;
import com.delumengyu.imemo.model.LoginResponse;
import com.delumengyu.imemo.model.Memo;
import com.delumengyu.imemo.model.Resource;
import com.delumengyu.imemo.model.UserSetting;
import com.delumengyu.imemo.model.CreateMemoRequest;
import com.delumengyu.imemo.model.UpdateMemoRequest;
import com.delumengyu.imemo.model.SetMemoResourcesRequest;
import com.delumengyu.imemo.model.ListMemosResponse;
import com.delumengyu.imemo.model.CreateResourceRequest;
import com.delumengyu.imemo.model.MemosProfile;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/v1/auth/signin")
    Call<LoginResponse> login(
        @Query("username") String username,
        @Query("password") String password,
        @Query("neverExpire") Boolean neverExpire
    );

    @POST("api/v1/auth/signout")
    Call<Void> signOut();

    @POST("api/v1/auth/status")
    Call<LoginResponse> authStatus();

    @GET("api/v1/users/{id}/setting")
    Call<UserSetting> getUserSetting(@Path("id") String userId);

    @GET("api/v1/memos")
    Call<ListMemosResponse> listMemos(
        @Query("pageSize") int pageSize,
        @Query("pageToken") String pageToken,
        @Query("filter") String filter,
        @Query("view") String view
    );

    @POST("api/v1/memos")
    Call<Memo> createMemo(@Body CreateMemoRequest request);

    @PATCH("api/v1/memos/{id}/resources")
    Call<Void> setMemoResources(
        @Path("id") String memoId, 
        @Body SetMemoResourcesRequest request
    );

    @PATCH("api/v1/memos/{id}")
    Call<Memo> updateMemo(
        @Path("id") String memoId,
        @Body UpdateMemoRequest request
    );

    @DELETE("api/v1/memos/{id}")
    Call<Void> deleteMemo(@Path("id") String memoId);

    @DELETE("api/v1/memos/{id}/tags/{tag}")
    Call<Void> deleteMemoTag(
        @Path("id") String memoId,
        @Path("tag") String tag,
        @Query("deleteRelatedMemos") boolean deleteRelatedMemos
    );

    @GET("api/v1/resources")
    Call<ListResourceResponse> listResources();

    @POST("api/v1/resources")
    Call<Resource> createResource(@Body CreateResourceRequest request);

    @DELETE("api/v1/resources/{id}")
    Call<Void> deleteResource(@Path("id") String resourceId);

    @GET("api/v1/workspace/profile")
    Call<MemosProfile> getProfile();

    @GET("api/v1/users/{id}")
    Call<LoginResponse> getUser(@Path("id") String userId);
} 