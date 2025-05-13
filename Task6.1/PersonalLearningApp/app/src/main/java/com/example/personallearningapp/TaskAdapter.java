package com.example.personallearningapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
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
        if (!task.status.isEmpty()) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(task.status);
        } else {
            holder.status.setVisibility(View.GONE);
        }

        // Click anywhere on the card to respond
        holder.itemView.setOnClickListener(v -> listener.onTaskClick(task));
        // Arrow button function in the lower right corner
        holder.arrow.setOnClickListener(v -> listener.onTaskClick(task));

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, status;
        ImageView arrow;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTask_Title);
            desc = itemView.findViewById(R.id.tvTask_Desc);
            arrow = itemView.findViewById(R.id.ivArrow);
            status = itemView.findViewById(R.id.tvTask_Status);
        }
    }
}
