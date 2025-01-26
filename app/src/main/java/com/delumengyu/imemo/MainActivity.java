package com.delumengyu.imemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import com.delumengyu.imemo.adapter.MemoAdapter;
import com.delumengyu.imemo.databinding.ActivityMainBinding;
import com.delumengyu.imemo.dialog.CreateMemoDialog;
import com.delumengyu.imemo.model.LoginResponse;
import com.delumengyu.imemo.model.Memo;
import com.delumengyu.imemo.util.UserManager;
import com.delumengyu.imemo.network.ApiClient;
import com.delumengyu.imemo.model.ListMemosResponse;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MemoAdapter memoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 检查登录状态
        if (!UserManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
        setupUserInfo();
        
        loadMemos();
    }

    private void setupRecyclerView() {
        memoAdapter = new MemoAdapter(this);
        binding.memoList.setAdapter(memoAdapter);
        binding.memoList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadMemos);
    }

    private void setupFab() {
        binding.addMemoButton.setOnClickListener(v -> showCreateMemoDialog());
    }

    private void setupUserInfo() {
        LoginResponse user = UserManager.getInstance(this).getCurrentUser();
        if (user != null) {
            // 设置用户信息，比如头像和昵称
            binding.toolbar.setTitle(user.getUsername());  // 使用 toolbar 标题来显示用户名
            // 如果有头像URL，可以使用Glide等图片加载库加载头像
        }
    }

    private void loadMemos() {
        ApiClient.getInstance()
                .getApiService()
                .listMemos(20, null, "", null)  // 使用 listMemos 方法，设置默认参数
                .enqueue(new Callback<ListMemosResponse>() {
                    @Override
                    public void onResponse(Call<ListMemosResponse> call, Response<ListMemosResponse> response) {
                        binding.swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null) {
                            memoAdapter.setMemos(response.body().getMemos());
                        } else {
                            Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ListMemosResponse> call, Throwable t) {
                        binding.swipeRefresh.setRefreshing(false);
                        Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCreateMemoDialog() {
        // TODO: 实现创建备忘录的对话框
        CreateMemoDialog dialog = new CreateMemoDialog(this);
        dialog.show(memo -> {
            // 刷新列表
            loadMemos();
        });
    }
} 