package com.example.lostfoundapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.*;
import android.content.Intent;
import android.database.Cursor;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ShowItemsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<LostFoundItem> itemList;
    LostFoundAdapter adapter;
    DBHelper db;
    TextView tvEmpty;
    private static final int REQUEST_CODE_ITEM_DETAILS = 1;
    private ActivityResultLauncher<Intent> itemDetailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        listView = findViewById(R.id.listView);
        db = new DBHelper(this);
        itemList = new ArrayList<>();

        adapter = new LostFoundAdapter(this, itemList);
        listView.setAdapter(adapter);

        tvEmpty = findViewById(R.id.tvEmpty);

        itemDetailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    reloadList();  // Refresh the list after successful delete
                }
        });

        // Open the details page
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ItemDetailsActivity.class);
            intent.putExtra("item", itemList.get(position));
            itemDetailLauncher.launch(intent);
        });

        reloadList(); // Initial Loading
    }

    //  Refreshing the list
    private void reloadList() {
        // make sure itemList is not null
        if (itemList == null) {
            itemList = new ArrayList<>();
        } else {
            itemList.clear();
        }
        Cursor cursor = db.getAllItems();
        while (cursor.moveToNext()) {
            LostFoundItem item = new LostFoundItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
            itemList.add(item);
        }

        adapter.notifyDataSetChanged();

        if (itemList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    // Refresh page after deleting
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ITEM_DETAILS && resultCode == RESULT_OK) {
            reloadList();
        }
    }

}