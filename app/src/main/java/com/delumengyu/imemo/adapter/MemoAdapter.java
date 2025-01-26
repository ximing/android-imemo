package com.delumengyu.imemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {
    private List<Memo> memos = new ArrayList<>();
    private final Markwon markwon;

    public MemoAdapter(Context context) {
        this.markwon = Markwon.create(context);
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_memo, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo memo = memos.get(position);
        markwon.setMarkdown(holder.memoContent, memo.getContent());
        
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String timeStr = sdf.format(memo.getDate());
        holder.memoTime.setText(timeStr);
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
        notifyDataSetChanged();
    }

    static class MemoViewHolder extends RecyclerView.ViewHolder {
        TextView memoContent;
        TextView memoTime;

        MemoViewHolder(@NonNull View itemView) {
            super(itemView);
            memoContent = itemView.findViewById(R.id.memoContent);
            memoTime = itemView.findViewById(R.id.memoTime);
        }
    }
} 