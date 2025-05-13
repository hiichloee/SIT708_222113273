package com.example.personallearningapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestViewHolder> {

    private final List<String> interests;
    private final List<String> selectedInterests;

    public InterestAdapter(List<String> interests, List<String> selectedInterests) {
        this.interests = interests;
        this.selectedInterests = selectedInterests;
    }

    @NonNull
    @Override
    public InterestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_interest, parent, false);
        return new InterestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestViewHolder  holder, int position) {

        String topic = interests.get(position);
        holder.btn.setText(topic);
        // 设置按钮选中状态样式
        holder.btn.setSelected(selectedInterests.contains(topic));

        // 点击按钮逻辑
        holder.btn.setOnClickListener(v -> {
            if (selectedInterests.contains(topic)) {
                selectedInterests.remove(topic);
                holder.btn.setAlpha(1f);
//                holder.btn.setSelected(false);
                holder.btn.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

            } else {
                if (selectedInterests.size() < 10) {
                    selectedInterests.add(topic);
                    holder.btn.setAlpha(0.5f);
//                    holder.btn.setSelected(true);
                    holder.btn.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();

                } else {
                    Toast.makeText(holder.itemView.getContext(),
                            "You can select up to 10 topics only.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    static class InterestViewHolder extends RecyclerView.ViewHolder {
        Button btn;

        public InterestViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.btnInterest);
        }
    }
}
