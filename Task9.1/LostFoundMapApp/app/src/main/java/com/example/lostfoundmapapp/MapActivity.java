package com.example.lostfoundmapapp;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.database.Cursor;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        db = new DBHelper(this);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Cursor cursor = db.getAllItems();

        boolean errorShown = false;
        while (cursor.moveToNext()) {
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

                LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(item.getType() + ": " + item.getDescription())
                        .snippet("Location: " + item.getLocation()));

            } catch (Exception e) {

                e.printStackTrace();

                if (!errorShown) {
                    Toast.makeText(MapActivity.this, "Failed info: Empty map data.", Toast.LENGTH_SHORT).show();
                    errorShown = true;
                }
                Toast.makeText(MapActivity.this, "Failed info: Empty map data.", Toast.LENGTH_SHORT).show();
            }
        }
        if (cursor.moveToFirst()) {
            double lat = cursor.getDouble(7);
            double lng = cursor.getDouble(8);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10f));
        }
    }
}
