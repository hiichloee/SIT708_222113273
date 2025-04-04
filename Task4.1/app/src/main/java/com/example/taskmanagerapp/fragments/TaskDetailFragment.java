package com.example.taskmanagerapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.taskmanagerapp.R;
import com.example.taskmanagerapp.Task;
import com.example.taskmanagerapp.TaskDatabaseHelper;

public class TaskDetailFragment extends Fragment {
    private TextView textTitle, textDescription, textDueDate;
    private Button buttonDelete, buttonEdit;
    private TaskDatabaseHelper dbHelper;
    private int taskId;

    public TaskDetailFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textTitle = view.findViewById(R.id.text_task_title);
        textDescription = view.findViewById(R.id.text_task_description);
        textDueDate = view.findViewById(R.id.text_task_due_date);
        buttonDelete = view.findViewById(R.id.button_delete_task);
        buttonEdit = view.findViewById(R.id.button_edit_task);
        dbHelper = new TaskDatabaseHelper(getContext());


        if (getArguments() != null) {
            taskId = getArguments().getInt("task_id");
            String title = getArguments().getString("task_title");
            String description = getArguments().getString("task_description");
            String dueDate = getArguments().getString("task_due_date");

            textTitle.setText(title);
            textDescription.setText(description);
            textDueDate.setText(dueDate);
        }

        // Edit the task, jump to AddEditTaskFragment and pass the data
        buttonEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("task_id", taskId);
            bundle.putString("task_title", textTitle.getText().toString());
            bundle.putString("task_description", textDescription.getText().toString());
            bundle.putString("task_due_date", textDueDate.getText().toString());

            Navigation.findNavController(view).navigate(R.id.action_taskDetailFragment_to_addEditTaskFragment, bundle);
        });

        // Delete Task
        buttonDelete.setOnClickListener(v -> {
            dbHelper.deleteTask(taskId);
            Toast.makeText(getContext(), "Task Deleted", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTaskDetails();  // Reload task data
    }
    private void refreshTaskDetails() {
        if (taskId == -1) return; // If the taskId is invalid, it is not updated

        Task updatedTask = dbHelper.getTaskById(taskId); // Re-query the database
        if (updatedTask != null) {
            textTitle.setText(updatedTask.getTitle());
            textDescription.setText(updatedTask.getDescription());
            textDueDate.setText(updatedTask.getDueDate());
        }
    }
}