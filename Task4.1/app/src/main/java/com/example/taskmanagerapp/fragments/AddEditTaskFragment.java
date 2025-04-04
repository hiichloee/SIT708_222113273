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
import android.widget.EditText;
import android.widget.Toast;
import com.example.taskmanagerapp.R;
import com.example.taskmanagerapp.TaskDatabaseHelper;
import com.example.taskmanagerapp.Task;

public class AddEditTaskFragment extends Fragment {
    private EditText editTitle, editDescription, editDueDate;
    private Button buttonSave;
    private TaskDatabaseHelper dbHelper;
    private int taskId = -1;

    public AddEditTaskFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTitle = view.findViewById(R.id.edit_task_title);
        editDescription = view.findViewById(R.id.edit_task_description);
        editDueDate = view.findViewById(R.id.edit_task_due_date);
        buttonSave = view.findViewById(R.id.button_save_task);
        dbHelper = new TaskDatabaseHelper(getContext());

        if (getArguments() != null) {
            taskId = getArguments().getInt("task_id", -1);
            editTitle.setText(getArguments().getString("task_title", ""));
            editDescription.setText(getArguments().getString("task_description", ""));
            editDueDate.setText(getArguments().getString("task_due_date", ""));
        }

        buttonSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String description = editDescription.getText().toString();
            String dueDate = editDueDate.getText().toString();

            //  Validation and Error Handling
            if (title.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(getContext(), "Title and Due Date are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (taskId == -1) {
                dbHelper.addTask(title, description, dueDate);
                Toast.makeText(getContext(), "Task Added", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateTask(new Task(taskId, title, description, dueDate));
                Toast.makeText(getContext(), "Task Updated", Toast.LENGTH_SHORT).show();
            }

              requireActivity().getSupportFragmentManager().popBackStack();
//            Navigation.findNavController(view).popBackStack();
        });
    }
}