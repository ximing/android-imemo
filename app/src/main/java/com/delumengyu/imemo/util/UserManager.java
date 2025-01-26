package com.delumengyu.imemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.delumengyu.imemo.model.LoginResponse;
import com.google.gson.Gson;

public class UserManager {
    private static final String PREF_NAME = "IMemo";
    private static final String KEY_USER_INFO = "user_info";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static UserManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private LoginResponse currentUser;
    private String accessToken;

    private UserManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadUserInfo();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUserInfo(LoginResponse user, String accessToken) {
        currentUser = user;
        this.accessToken = accessToken;
        
        String userJson = gson.toJson(user);
        preferences.edit()
                .putString(KEY_USER_INFO, userJson)
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .apply();
    }

    private void loadUserInfo() {
        String userJson = preferences.getString(KEY_USER_INFO, null);
        accessToken = preferences.getString(KEY_ACCESS_TOKEN, null);
        if (userJson != null) {
            currentUser = gson.fromJson(userJson, LoginResponse.class);
        }
    }

    public LoginResponse getCurrentUser() {
        return currentUser;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void clearUserInfo() {
        currentUser = null;
        accessToken = null;
        preferences.edit()
                .remove(KEY_USER_INFO)
                .remove(KEY_ACCESS_TOKEN)
                .apply();
    }

    public boolean isLoggedIn() {
        return currentUser != null && accessToken != null;
    }
} 