package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.accounts.AccountHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapHomeActivity extends AppCompatActivity implements SensorEventListener{

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private SensorManager sensorManager;
    private float[] gravity;
    private float[] geomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        map.setZoomLevel(20);
        // Initialize MyLocationNewOverlay
        myLocationOverlay = new MyLocationNewOverlay(map);
        myLocationOverlay.enableMyLocation(); // Enable location tracking
        map.getOverlays().add(myLocationOverlay);

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return; // Exit if permissions are not granted
        }

        // Center the map on the user's location if available
        myLocationOverlay.enableFollowLocation();

        // Add a marker
        /*
                Marker marker = new Marker(map);
        marker.setPosition(myLocationOverlay.getMyLocation());
        marker.setTitle("I found you");
        map.getOverlays().add(marker);*/

        // Initialize Sensor Manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLocationOverlay.disableMyLocation(); // Disable location tracking when done
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuthInDegrees = (float) Math.toDegrees(orientation[0]); // Azimuth in degrees
                rotateMap(azimuthInDegrees);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void rotateMap(float azimuth) {
        map.setMapOrientation(-azimuth); // Rotate map according to azimuth
    }

    public void recommendedRestroomsButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SavedBuildingsActivity.class);

        startActivity(intent);
    }

    public void searchRestroomButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SearchBuildingActivity.class);

        startActivity(intent);
    }

    public void accountHomeButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, AccountHomeActivity.class);

        startActivity(intent);
    }

    public void rateRestroomsButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, ReviewReportRestroomActivity.class);

        startActivity(intent);
    }

    public void suggestRestroomButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SuggestRestroomLocationActivity.class);

        intent.putExtra("CALLER", "USER");
        startActivity(intent);
    }

    public void viewBuildingButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, ViewBuildingActivity.class);

        startActivity(intent);
    }


}