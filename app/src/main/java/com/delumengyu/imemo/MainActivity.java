package com.delumengyu.imemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.delumengyu.imemo.adapter.MemoAdapter;
import com.delumengyu.imemo.databinding.ActivityMainBinding;
import com.delumengyu.imemo.model.LoginResponse;
import com.delumengyu.imemo.model.Memo;
import com.delumengyu.imemo.util.UserManager;
import com.delumengyu.imemo.network.ApiClient;
import com.delumengyu.imemo.model.ListMemosResponse;

import com.delumengyu.imemo.model.CreateMemoRequest;
import com.delumengyu.imemo.model.MemoVisibility;
import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import android.widget.ImageButton;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.app.AlertDialog;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MemoAdapter memoAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View expandedPanel;  // 将变量移到类成员变量
    private String nextPageToken = null;
    private boolean isLoading = false;
    private static final int PAGE_SIZE = 20;
    private static final int SHOW_SCROLL_UP_THRESHOLD = 1000; // 使用固定像素值作为阈值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置软键盘模式
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | 
            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        );
        
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
        setupUserInfo();
        setupQuickInput();
        
        // 首次加载时显示加载状态
        memoAdapter.setLoadingMore(true);
        loadMemos();

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            // Handle search button click
        });

        // Setup navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Set username in nav header
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.usernameText);
        usernameText.setText("用户名"); // Set actual username

        // Handle settings click
        headerView.findViewById(R.id.settingsButton).setOnClickListener(v -> {
            // Handle settings click
        });
    }

    private void setupRecyclerView() {
        memoAdapter = new MemoAdapter(this);
        binding.memoList.setAdapter(memoAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.memoList.setLayoutManager(layoutManager);
        
        binding.memoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                // 检查是否需要加载更多
                if (dy > 0) { // 向下滚动
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && nextPageToken != null) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            loadMoreMemos();
                        }
                    }
                }
                
                // 控制回到顶部按钮的显示和隐藏
                int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                View firstVisibleItem = layoutManager.findViewByPosition(firstCompletelyVisibleItemPosition);
                if (firstVisibleItem != null) {
                    int itemTop = firstVisibleItem.getTop();
                    int scrollY = firstCompletelyVisibleItemPosition * firstVisibleItem.getHeight() - itemTop;
                    
                    if (scrollY > SHOW_SCROLL_UP_THRESHOLD) {
                        showScrollToTopButton();
                    } else {
                        hideScrollToTopButton();
                    }
                }
            }
        });

        // 设置回到顶部按钮点击事件
        binding.scrollToTopButton.setOnClickListener(v -> {
            binding.memoList.smoothScrollToPosition(0);
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::refreshMemos);
    }

    private void setupUserInfo() {
        LoginResponse user = UserManager.getInstance(this).getCurrentUser();
        if (user != null) {
            // 设置用户信息，比如头像和昵称
            binding.toolbar.setTitle(user.getUsername());  // 使用 toolbar 标题来显示用户名
            // 如果有头像URL，可以使用Glide等图片加载库加载头像
        }
    }

    private void setupQuickInput() {
        EditText quickInput = binding.quickMemoInput;
        
        quickInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showExpandedPanel();
            }
        });
    }

    private void showExpandedPanel() {
        // 隐藏默认输入框
        binding.inputContainer.setVisibility(View.GONE);
        // 显示扩展输入框
        binding.expandedInputPanel.setVisibility(View.VISIBLE);
        
        EditText expandedInput = binding.expandedInput;
        expandedInput.setText(binding.quickMemoInput.getText());
        expandedInput.requestFocus();
        
        // 显示键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(expandedInput, InputMethodManager.SHOW_IMPLICIT);
        
        // 设置返回键处理
        expandedInput.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                hideExpandedPanel();
                return true;
            }
            return false;
        });

        // 设置发送按钮点击事件
        binding.expandedInputPanel.findViewById(R.id.sendButton).setOnClickListener(v -> {
            String content = binding.expandedInput.getText().toString();
            if (!content.isEmpty()) {
                CreateMemoRequest request = new CreateMemoRequest(content, MemoVisibility.PRIVATE.name());
                createMemoAndRefresh(request);
            }
        });
    }

    private void hideExpandedPanel() {
        // 隐藏扩展输入框
        binding.expandedInputPanel.setVisibility(View.GONE);
        // 显示默认输入框
        binding.inputContainer.setVisibility(View.VISIBLE);
        
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.quickMemoInput.getWindowToken(), 0);
        
        // 清空输入框并取消焦点
        binding.quickMemoInput.setText("");
        binding.quickMemoInput.clearFocus();
    }

    @Override
    public void onBackPressed() {
        if (expandedPanel != null) {
            hideExpandedPanel();
        } else {
            super.onBackPressed();
        }
    }

    private void createMemoAndRefresh(CreateMemoRequest request) {
        ApiClient.getInstance()
                .getApiService()
                .createMemo(request)
                .enqueue(new Callback<Memo>() {
                    @Override
                    public void onResponse(Call<Memo> call, Response<Memo> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // 隐藏输入面板
                            hideExpandedPanel();
                            // 刷新列表并滚动到顶部
                            refreshAndScrollToTop();
                            Toast.makeText(MainActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Memo> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String buildFilter() {
        LoginResponse currentUser = UserManager.getInstance(this).getCurrentUser();
        // 使用 && 连接多个条件，数组使用 [] 语法
        return String.format("creator == 'users/%s' && visibilities == ['PUBLIC', 'PROTECTED', 'PRIVATE']", 
                           currentUser.getId());
    }

    private void refreshAndScrollToTop() {
        nextPageToken = null;
        
        String filter = buildFilter();
        
        ApiClient.getInstance()
                .getApiService()
                .listMemos(PAGE_SIZE, null, filter, null)
                .enqueue(new Callback<ListMemosResponse>() {
                    @Override
                    public void onResponse(Call<ListMemosResponse> call, Response<ListMemosResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ListMemosResponse data = response.body();
                            nextPageToken = data.getNextPageToken();
                            memoAdapter.setMemos(data.getMemos());
                            
                            // 平滑滚动到顶部
                            binding.memoList.smoothScrollToPosition(0);
                        } else {
                            Toast.makeText(MainActivity.this, "刷新失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ListMemosResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refreshMemos() {
        nextPageToken = null; // 重置分页标记
        loadMemos(true);
    }

    private void loadMoreMemos() {
        if (isLoading || nextPageToken == null) return;
        loadMemos(false);
    }

    private void loadMemos() {
        loadMemos(false);
    }

    private void loadMemos(boolean isRefresh) {
        isLoading = true;
        if (!isRefresh && memoAdapter.getItemCount() == 0) {
            memoAdapter.setLoadingMore(true);
        }
        
        String filter = buildFilter();
        
        ApiClient.getInstance()
                .getApiService()
                .listMemos(PAGE_SIZE, nextPageToken, filter, null)
                .enqueue(new Callback<ListMemosResponse>() {
                    @Override
                    public void onResponse(Call<ListMemosResponse> call, Response<ListMemosResponse> response) {
                        isLoading = false;
                        binding.swipeRefresh.setRefreshing(false);
                        memoAdapter.setLoadingMore(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ListMemosResponse data = response.body();
                            nextPageToken = data.getNextPageToken();
                            
                            if (isRefresh) {
                                memoAdapter.setMemos(data.getMemos());
                                Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                memoAdapter.addMemos(data.getMemos());
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ListMemosResponse> call, Throwable t) {
                        isLoading = false;
                        binding.swipeRefresh.setRefreshing(false);
                        memoAdapter.setLoadingMore(false);
                        Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 添加一个方法来判断是否有数据
    private boolean hasMemos() {
        return memoAdapter != null && memoAdapter.getItemCount() > 0;
    }

    private void showScrollToTopButton() {
        if (binding.scrollToTopButton.getVisibility() != View.VISIBLE) {
            binding.scrollToTopButton.animate()
                .alpha(1f)
                .setDuration(200)
                .withStartAction(() -> binding.scrollToTopButton.setVisibility(View.VISIBLE))
                .start();
        }
    }

    private void hideScrollToTopButton() {
        if (binding.scrollToTopButton.getVisibility() == View.VISIBLE) {
            binding.scrollToTopButton.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> binding.scrollToTopButton.setVisibility(View.GONE))
                .start();
        }
    }

    public void editMemo(Memo memo) {
        // TODO: 实现编辑功能
        Toast.makeText(this, "编辑功能开发中", Toast.LENGTH_SHORT).show();
    }

    public void deleteMemo(Memo memo) {
        // 添加更多日志来检查 memo 对象
        Log.d("MainActivity", "Memo object: " + memo);
        Log.d("MainActivity", "Raw memo fields: name=" + memo.getName() + 
                            ", content=" + memo.getContent() + 
                            ", date=" + memo.getDate() +
                            ", creatorId=" + memo.getCreatorId());

        String memoName = memo.getName();
        if (memoName == null) {
            Toast.makeText(this, "无效的备忘录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AlertDialog.Builder(this)
            .setTitle("删除确认")
            .setMessage("确定要删除这条备忘录吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                Log.d("MainActivity", "User confirmed deletion for memo name: " + memoName);
                
                ApiClient.getInstance()
                    .getApiService()
                    .deleteMemo(memoName)  // 使用完整的 name
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            // 添加日志：检查响应
                            Log.d("MainActivity", "Delete response code: " + response.code());
                            if (!response.isSuccessful()) {
                                try {
                                    Log.e("MainActivity", "Error body: " + response.errorBody().string());
                                } catch (Exception e) {
                                    Log.e("MainActivity", "Error reading error body", e);
                                }
                            }
                            
                            if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                refreshMemos();
                            } else {
                                Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // 添加日志：网络错误详情
                            Log.e("MainActivity", "Network error when deleting memo", t);
                            Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
            })
            .setNegativeButton("取消", null)
            .show();
    }
} 