package com.delumengyu.imemo.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.delumengyu.imemo.App;
import com.delumengyu.imemo.util.UserManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://memo.ximing.ren/"; // 替换为你的服务器地址
    private static ApiClient instance;
    private final ApiService apiService;

    private ApiClient() {
        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 创建 OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    String token = UserManager.getInstance(App.getInstance()).getAccessToken();
                    
                    // 添加认证头
                    Request.Builder builder = original.newBuilder();
                    if (token != null) {
                        builder.header("Authorization", "Bearer " + token);
                    }
                    Request request = builder
                            .method(original.method(), original.body())
                            .build();
                    
                    return chain.proceed(request);
                })
                .addInterceptor(loggingInterceptor)  // 添加日志拦截器
                .build();

        // 创建 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
} 