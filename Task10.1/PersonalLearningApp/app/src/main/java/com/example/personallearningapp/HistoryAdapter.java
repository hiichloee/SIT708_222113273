package com.example.personallearningapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personallearningapp.model.Question;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<Question> questionList;
    public HistoryAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = questionList.get(position);
        String qNum = (position + 1) + ". Question " + (position + 1);
        holder.tvNumber.setText(qNum);
        holder.tvTitle.setText(question.questionTitle);

        // 第一个题目默认展开
        if (position == 0) {
            question.isExpanded = true;
        }

        // 控制内容显示
        holder.tvTitle.setVisibility(question.isExpanded ? View.VISIBLE : View.GONE);
        holder.radioGroup.setVisibility(question.isExpanded ? View.VISIBLE : View.GONE);

//        Log.d("DEBUG_QUESTION", "position: " + position);
//        Log.d("DEBUG_QUESTION", "if isExpanded: " + question.isExpanded);

        // 控制按钮显示逻辑
        if (position == 0 || question.isExpanded) {
            holder.expandBtn.setVisibility(View.GONE); // 第一个或已经展开的不显示按钮
        } else {
            holder.expandBtn.setVisibility(View.VISIBLE); // 其他未展开的显示按钮
        }

        // 每次重新渲染前清空
        holder.radioGroup.removeAllViews();
        for (int i = 0; i < question.options.length; i++) {
            RadioButton rb = new RadioButton(holder.itemView.getContext());
            rb.setText(question.options[i]);
            rb.setEnabled(false);

            // 设置颜色逻辑
            if (question.selectedOption == i && !question.options[i].equals(question.correctAnswer)) {
                rb.setTextColor(Color.RED); // 选了但不是正确答案
            } else if (question.options[i].equals(question.correctAnswer)) {
                rb.setTextColor(Color.GREEN); // 正确答案
            } else {
                rb.setTextColor(Color.WHITE); // 其他默认白色
            }

            if (question.selectedOption == i) rb.setChecked(true);
            holder.radioGroup.addView(rb);
        }

        // 顶部时间 + topic 设置
        holder.tvTimestamp.setText(question.timestamp != null ? question.timestamp : "Unknown");
        holder.tvTopic.setText(question.taskTitle != null ? question.taskTitle : "Unknown");

        // 展开按钮逻辑
        holder.expandBtn.setOnClickListener(v -> {
            question.isExpanded = !question.isExpanded;
            notifyItemChanged(position);
        });



    }

    @Override
    public int getItemCount() {
        return questionList == null ? 0 : questionList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvTitle, tvTimestamp, tvTopic;
        RadioGroup radioGroup;
        ImageView expandBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvHistory_Question_Number);
            tvTitle = itemView.findViewById(R.id.tvHistory_Question_Title);
            radioGroup = itemView.findViewById(R.id.History_radioGroupOptions);
            expandBtn = itemView.findViewById(R.id.btnExpand);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvTopic = itemView.findViewById(R.id.tv_topic);
        }
    }
}
