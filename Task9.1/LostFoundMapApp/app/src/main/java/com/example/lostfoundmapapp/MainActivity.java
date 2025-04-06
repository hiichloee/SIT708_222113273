package com.example.lostfoundmapapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {

    Button btnCreate, btnShow, btnMap, btnClearAll;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);  // Initial Database

        btnCreate = findViewById(R.id.btnCreateAdvert);
        btnShow = findViewById(R.id.btnShowItems);
        btnMap = findViewById(R.id.btnShowOnMap);
        btnClearAll = findViewById(R.id.btnClearAll);

        // Navigate to Create page
        btnCreate.setOnClickListener(v -> startActivity(new Intent(this, CreateAdvertActivity.class)));
        // Navigate to Show page
        btnShow.setOnClickListener(v -> startActivity(new Intent(this, ShowItemsActivity.class)));
        // Navigate to Map page
        btnMap.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));

        // Clear all items in DB
        btnClearAll.setOnClickListener(v -> {
            SQLiteDatabase dbHelper = db.getWritableDatabase();
            int deletedRows = dbHelper.delete("items", null, null);
            Toast.makeText(this,
                    "All records cleared: " + deletedRows + " items removed.",
                    Toast.LENGTH_SHORT).show();
        });
    }
}