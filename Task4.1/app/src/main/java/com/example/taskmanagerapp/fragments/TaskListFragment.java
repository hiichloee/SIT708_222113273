package com.example.taskmanagerapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.List;

import com.example.taskmanagerapp.R;
import com.example.taskmanagerapp.TaskDatabaseHelper;
import com.example.taskmanagerapp.TaskAdapter;
import com.example.taskmanagerapp.Task;



public class TaskListFragment extends Fragment {
    private TaskDatabaseHelper dbHelper;
    private TaskAdapter adapter;
    private ListView taskListView;

    public TaskListFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskListView = view.findViewById(R.id.task_list_view);
        dbHelper = new TaskDatabaseHelper(getContext());

        List<Task> taskList = dbHelper.getAllTasks();
        adapter = new TaskAdapter(getContext(), taskList);
        taskListView.setAdapter(adapter);

        // Add a click event to go to the task details page
        taskListView.setOnItemClickListener((AdapterView<?> parent, View itemView, int position, long id) -> {
            Task selectedTask = taskList.get(position);

            Bundle bundle = new Bundle();
            bundle.putInt("task_id", selectedTask.getId());
            bundle.putString("task_title", selectedTask.getTitle());
            bundle.putString("task_description", selectedTask.getDescription());
            bundle.putString("task_due_date", selectedTask.getDueDate());

            Navigation.findNavController(view).navigate(R.id.action_navigation_task_list_to_taskDetailFragment, bundle);
        });
    }


}