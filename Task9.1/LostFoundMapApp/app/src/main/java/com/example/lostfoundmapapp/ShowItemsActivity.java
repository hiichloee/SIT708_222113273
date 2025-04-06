package com.example.lostfoundmapapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.*;
import android.content.Intent;
import android.database.Cursor;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.util.ArrayList;

public class ShowItemsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<LostFoundItem> itemList;
    LostFoundAdapter adapter;
    DBHelper db;
    TextView tvEmpty;
    private static final int REQUEST_CODE_ITEM_DETAILS = 1;
    private ActivityResultLauncher<Intent> itemDetailLauncher;
    private Cursor cursor;

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

        boolean errorShown = false;
        while (cursor.moveToNext()) {

            // prevent for crash
            try {
                if (cursor.isNull(7) || cursor.isNull(8)) continue;

                LostFoundItem item = new LostFoundItem(
                        cursor.getInt(0),     // id
                        cursor.getString(1),  // type
                        cursor.getString(2),  // name
                        cursor.getString(3),  // phone
                        cursor.getString(4),  // description
                        cursor.getString(5),  // date
                        cursor.getString(6),  // location
                        cursor.getDouble(7),  // latitude
                        cursor.getDouble(8)   // longitude
                );

                itemList.add(item);

            } catch (Exception e) {

                e.printStackTrace();

                if (!errorShown) {
                    Toast.makeText(ShowItemsActivity.this, "Failed info: Empty map data.", Toast.LENGTH_SHORT).show();
                    errorShown = true;
                }
                Toast.makeText(ShowItemsActivity.this, "Failed info: Empty map data.", Toast.LENGTH_SHORT).show();
            }


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