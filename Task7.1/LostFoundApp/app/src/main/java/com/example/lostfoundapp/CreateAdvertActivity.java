package com.example.lostfoundapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.widget.*;

import java.util.Calendar;

public class CreateAdvertActivity extends AppCompatActivity {

    RadioGroup postTypeGroup;
    EditText name, phone, desc, date, location;
    Button saveBtn;
    DBHelper db;
    Spinner spinnerLocation;  // State Spinner
    String[] states = {"VIC", "NSW", "QLD", "WA", "SA", "TAS", "ACT", "NT"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        db = new DBHelper(this);
        postTypeGroup = findViewById(R.id.radioGroupType);
        name = findViewById(R.id.etName);
        phone = findViewById(R.id.etPhone);
        desc = findViewById(R.id.etDescription);
        date = findViewById(R.id.etDate);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        saveBtn = findViewById(R.id.btnSave);

        // Setting the contents of the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
        spinnerLocation.setAdapter(adapter);

        // Date Pick
        date.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAdvertActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        date.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Save
        saveBtn.setOnClickListener(v -> {
            String type = ((RadioButton) findViewById(postTypeGroup.getCheckedRadioButtonId())).getText().toString();
            String selectedLocation = spinnerLocation.getSelectedItem().toString();
            db.insertItem(
                    type,
                    name.getText().toString(),
                    phone.getText().toString(),
                    desc.getText().toString(),
                    date.getText().toString(),
                    selectedLocation
            );

            Toast.makeText(this, "Advert Saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}