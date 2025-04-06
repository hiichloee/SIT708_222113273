package com.example.lostfoundmapapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.widget.*;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import androidx.annotation.Nullable;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.*;

public class CreateAdvertActivity extends AppCompatActivity {

    EditText etName, etPhone, etDescription, etDate, etLocation;
    RadioGroup postTypeGroup;
    RadioButton rbLost, rbFound;
    Button saveBtn, btnGetLocation;
    DBHelper db;

    double latitude = 0.0, longitude = 0.0;
    FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<Intent> placeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        db = new DBHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        postTypeGroup = findViewById(R.id.radioGroupType);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        saveBtn = findViewById(R.id.btnSave);

        btnGetLocation = findViewById(R.id.btnGetLocation);
        rbLost = findViewById(R.id.rbLost);
        rbFound = findViewById(R.id.rbFound);

        // Initial Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyB5ecq62CCVSN6YIRtu5sDMkWnW5F1AxZM", Locale.getDefault());
        }

        // Registering Autocomplete Return Handling in a New Way
        placeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());
                        etLocation.setText(place.getAddress());
                        LatLng latLng = place.getLatLng();
                        latitude = latLng.latitude;
                        longitude = latLng.longitude;
                    } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                        Status status = Autocomplete.getStatusFromIntent(result.getData());
                        Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    } else if (result.getResultCode() == RESULT_CANCELED) {

                        // User cancellation also closes the pop-up window
                        Toast.makeText(this, "Search canceled", Toast.LENGTH_SHORT).show();
                    }
                });

        // Auto-Fill Address screen
        etLocation.setFocusable(false);
        etLocation.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
            placeLauncher.launch(intent);
        });

        // Get current location
        btnGetLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (!addresses.isEmpty()) {
                            etLocation.setText(addresses.get(0).getAddressLine(0));
                        }
                    } catch (Exception e) {
                        etLocation.setText("Lat: " + latitude + ", Lng: " + longitude);
                    }
                }
            });
        });

        // Date Pick
        etDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAdvertActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        etDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Save
        saveBtn.setOnClickListener(v -> {
            String type = rbLost.isChecked() ? "Lost" : "Found";
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String desc = etDescription.getText().toString();
            String date = etDate.getText().toString();
            String location = etLocation.getText().toString();

            db.insertItem(type, name, phone, desc, date, location, latitude, longitude);

            Toast.makeText(this, "Advert Saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

}