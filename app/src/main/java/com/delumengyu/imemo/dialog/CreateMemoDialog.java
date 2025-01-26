package com.delumengyu.imemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.delumengyu.imemo.R;
import com.delumengyu.imemo.databinding.DialogCreateMemoBinding;
import com.delumengyu.imemo.model.Memo;
import com.delumengyu.imemo.model.CreateMemoRequest;
import com.delumengyu.imemo.model.MemoVisibility;
import com.delumengyu.imemo.network.ApiClient;
import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMemoDialog extends Dialog {
    private DialogCreateMemoBinding binding;
    private final Markwon markwon;
    private OnMemoCreatedListener listener;
    private boolean isPreviewMode = false;

    public interface OnMemoCreatedListener {
        void onMemoCreated(Memo memo);
    }

    public CreateMemoDialog(@NonNull Context context) {
        super(context);
        markwon = Markwon.create(context);
    }

    public void show(OnMemoCreatedListener listener) {
        this.listener = listener;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogCreateMemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 设置对话框宽度为屏幕宽度
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        setupViews();
    }

    private void setupViews() {
        binding.previewButton.setOnClickListener(v -> togglePreview());
        binding.saveButton.setOnClickListener(v -> saveMemo());
    }

    private void togglePreview() {
        isPreviewMode = !isPreviewMode;
        if (isPreviewMode) {
            String content = binding.memoInput.getText().toString();
            binding.preview.setVisibility(View.VISIBLE);
            binding.memoInput.setVisibility(View.GONE);
            markwon.setMarkdown(binding.preview, content);
        } else {
            binding.preview.setVisibility(View.GONE);
            binding.memoInput.setVisibility(View.VISIBLE);
        }
    }

    private void saveMemo() {
        String content = binding.memoInput.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateMemoRequest request = new CreateMemoRequest(content, MemoVisibility.PRIVATE.name());
        ApiClient.getInstance()
                .getApiService()
                .createMemo(request)
                .enqueue(new Callback<Memo>() {
                    @Override
                    public void onResponse(Call<Memo> call, Response<Memo> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            dismiss();
                            if (listener != null) {
                                listener.onMemoCreated(response.body());
                            }
                        } else {
                            Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Memo> call, Throwable t) {
                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }
} 