package com.example.lostfoundapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemDetailsActivity extends AppCompatActivity {

    TextView tvTypeTitle, tvDescription, tvDate, tvLocation;
    Button removeBtn;
    LostFoundItem item;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        item = (LostFoundItem) getIntent().getSerializableExtra("item");
        db = new DBHelper(this);

        tvTypeTitle = findViewById(R.id.tvTypeTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        removeBtn = findViewById(R.id.btnRemove);

        // Title (Lost/Found) + Colour
        tvTypeTitle.setText(item.getType());
        if (item.getType().equalsIgnoreCase("Lost")) {
            tvTypeTitle.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvTypeTitle.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        }

        // Description content
        tvDescription.setText(item.getDescription());

        // Date converted to ‘X days ago’
        String dateStr = item.getDate();  // dd/MM/yyyyy
        String daysAgoText = convertDateToDaysAgo(dateStr);
        tvDate.setText(daysAgoText);

        // Location
        tvLocation.setText("At " + item.getLocation());

        removeBtn.setOnClickListener(v -> {
            db.deleteItem(item.getId());
            Toast.makeText(this, "Item with ID: " + item.getId() + " removed", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Notify previous page refresh
            finish();
        });
    }

    private String convertDateToDaysAgo(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date itemDate = sdf.parse(dateStr);
            long diff = new Date().getTime() - itemDate.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            if (days == 0) return "Today";
            else if (days == 1) return "1 day ago";
            else if (days < 31) return days + " days ago";
            else return (days / 30) + " months ago";
        } catch (Exception e) {
            return dateStr; // If parsing fails, return as is
        }
    }
}