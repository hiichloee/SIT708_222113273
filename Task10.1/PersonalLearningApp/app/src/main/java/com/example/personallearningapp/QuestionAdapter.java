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

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private final List<Question> questionList;
    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card, parent, false);
        return new ViewHolder(view);
    }

    // onBindViewHolder()
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = questionList.get(position);
        String qNum = (position + 1) + ". Question " + (position + 1);
        holder.number.setText(qNum);
        holder.title.setText(question.questionTitle);

        Log.d("DEBUG_QUESTION", "Setting title: " + question.questionTitle);

        // --- 展开控制逻辑 （学校要求） ---
        // 第一个题目默认展开
        if (position == 0) {
            question.isExpanded = true;
        }
        holder.title.setVisibility(question.isExpanded ? View.VISIBLE : View.GONE);
        holder.options.setVisibility(question.isExpanded ? View.VISIBLE : View.GONE);
        // 控制按钮显示逻辑
        if (position == 0 || question.isExpanded) {
            holder.expandBtn.setVisibility(View.GONE); // 第一个或已经展开的不显示按钮
        } else {
            holder.expandBtn.setVisibility(View.VISIBLE); // 其他未展开的显示按钮
        }

        // （更人性化）
        //  holder.title.setVisibility(question.isExpanded ? View.VISIBLE : View.GONE);
        //  holder.options.setVisibility(question.isExpanded ? View.VISIBLE : View.GONE);

        // 每次都先清空选项再重新添加
        holder.options.removeAllViews();
        for (int i = 0; i < question.options.length; i++) {
            RadioButton rb = new RadioButton(holder.itemView.getContext());
            rb.setText(question.options[i]);
            rb.setTextColor(Color.WHITE);
            int index = i;

            // 如果已经选择过，恢复选中状态
            if (question.selectedOption == i) {
                rb.setChecked(true);
            }

            rb.setOnClickListener(v -> question.selectedOption = index);
            holder.options.addView(rb);
        }

        // --- 点击按钮展开/收起 ---
        holder.expandBtn.setOnClickListener(v -> {
            question.isExpanded = !question.isExpanded;
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    // ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView number, title;
        RadioGroup options;
        ImageView expandBtn;

        // 构造器
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.tvQuestion_Number);
            title = itemView.findViewById(R.id.tvQuestion_Title);
            options = itemView.findViewById(R.id.radioGroupOptions);
            expandBtn = itemView.findViewById(R.id.btnExpand);
        }
    }
}
