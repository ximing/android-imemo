package com.delumengyu.imemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.delumengyu.imemo.model.LoginResponse;
import com.delumengyu.imemo.network.ApiClient;
import com.delumengyu.imemo.util.UserManager;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // 检查是否已经登录
        if (UserManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiClient.getInstance()
                .getApiService()
                .login(username, password, true)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // 获取 access token
                            Headers headers = response.headers();
                            String accessToken = null;
                            
                            // 尝试从 grpc-metadata-set-cookie 获取 token (Memos 0.22.0)
                            if (!headers.values("grpc-metadata-set-cookie").isEmpty()) {
                                String cookie = headers.values("grpc-metadata-set-cookie").get(0);
                                accessToken = extractAccessToken(cookie);
                            }
                            
                            // 如果找到 token，保存用户信息
                            if (accessToken != null) {
                                LoginResponse user = response.body();
                                UserManager.getInstance(LoginActivity.this)
                                    .saveUserInfo(user, accessToken);
                                
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败：无法获取访问令牌", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String extractAccessToken(String cookie) {
        if (cookie == null) return null;
        String[] parts = cookie.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("memos.access-token=")) {
                return part.trim().substring("memos.access-token=".length());
            }
        }
        return null;
    }
} 