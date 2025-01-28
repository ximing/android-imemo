package com.delumengyu.imemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import io.noties.markwon.Markwon;
import com.delumengyu.imemo.R;
import com.delumengyu.imemo.model.Memo;
import android.content.ClipData;
import android.content.ClipboardManager;
import com.delumengyu.imemo.MainActivity;

public class MemoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;
    
    private List<Memo> memos = new ArrayList<>();
    private final Markwon markwon;
    private boolean isLoadingMore = false;

    public MemoAdapter(Context context) {
        this.markwon = Markwon.create(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_memo, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MemoViewHolder && position < memos.size()) {
            Memo memo = memos.get(position);
            MemoViewHolder memoHolder = (MemoViewHolder) holder;
            markwon.setMarkdown(memoHolder.memoContent, memo.getContent());
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String timeStr = sdf.format(memo.getDate());
            memoHolder.memoTime.setText(timeStr);
            
            // 设置更多按钮点击事件
            memoHolder.memoOptionsButton.setOnClickListener(v -> 
                showMemoOptions(v.getContext(), memo, v));
        }
    }

    @Override
    public int getItemCount() {
        if (memos.isEmpty() && isLoadingMore) {
            return 1;
        }
        return memos.size() + (isLoadingMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (memos.isEmpty() && isLoadingMore) {
            return TYPE_LOADING;
        }
        return position == memos.size() ? TYPE_LOADING : TYPE_ITEM;
    }

    public void setLoadingMore(boolean loading) {
        if (this.isLoadingMore != loading) {
            this.isLoadingMore = loading;
            if (loading) {
                notifyItemInserted(memos.size());
            } else {
                notifyItemRemoved(memos.size());
            }
        }
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
        notifyDataSetChanged();
    }

    public void addMemos(List<Memo> newMemos) {
        int startPosition = memos.size();
        this.memos.addAll(newMemos);
        notifyItemRangeInserted(startPosition, newMemos.size());
    }

    private void showMemoOptions(Context context, Memo memo, View anchor) {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenuInflater().inflate(R.menu.memo_options_menu, popup.getMenu());
        
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_pin) {
                // TODO: 处理置顶，暂时移除这个功能
                return true;
            } else if (id == R.id.action_copy) {
                // 复制内容到剪贴板
                ClipboardManager clipboard = (ClipboardManager) 
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("memo_content", memo.getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_edit) {
                // 处理编辑
                if (context instanceof MainActivity) {
                    ((MainActivity) context).editMemo(memo);
                }
                return true;
            } else if (id == R.id.action_delete) {
                // 处理删除
                if (context instanceof MainActivity) {
                    ((MainActivity) context).deleteMemo(memo);
                }
                return true;
            }
            return false;
        });
        
        popup.show();
    }

    static class MemoViewHolder extends RecyclerView.ViewHolder {
        TextView memoContent;
        TextView memoTime;
        ImageButton memoOptionsButton;

        MemoViewHolder(@NonNull View itemView) {
            super(itemView);
            memoContent = itemView.findViewById(R.id.memoContent);
            memoTime = itemView.findViewById(R.id.memoTime);
            memoOptionsButton = itemView.findViewById(R.id.memoOptionsButton);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
} 