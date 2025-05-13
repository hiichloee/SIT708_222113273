package com.example.personallearningapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personallearningapp.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }
    private final List<Task> taskList;
    private final OnTaskClickListener listener;

    private final int userId;
    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener, int userId) {
        this.taskList = taskList;
        this.listener = listener;
        this.userId = userId;  // 保存进字段
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.title);
        holder.desc.setText(task.description);

        // 完成状态显示（只有 status 不为空时）
        if (!task.status.isEmpty()) {
            holder.status.setVisibility(View.VISIBLE);
            holder.btnHistory.setVisibility(View.VISIBLE);
            holder.status.setText(task.status);
        } else {
            holder.status.setVisibility(View.GONE);
            holder.btnHistory.setVisibility(View.GONE);
        }

        // 打开任务 or 跳转历史
        // Click anywhere on the card to respond
        holder.itemView.setOnClickListener(v -> listener.onTaskClick(task));
        // Arrow button function in the lower right corner
        holder.arrow.setOnClickListener(v -> listener.onTaskClick(task));
        holder.btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), HistoryActivity.class);
            intent.putExtra("user_id", userId);  // ✅ 使用传入字段，而不是访问 Dashboard 的 private 变量
            intent.putExtra("taskTitle", task.title);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, status, btnHistory;
        ImageView arrow;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTask_Title);
            desc = itemView.findViewById(R.id.tvTask_Desc);
            arrow = itemView.findViewById(R.id.ivArrow);
            status = itemView.findViewById(R.id.tvTask_Status);
            btnHistory = itemView.findViewById(R.id.btn_History);
        }
    }
}
