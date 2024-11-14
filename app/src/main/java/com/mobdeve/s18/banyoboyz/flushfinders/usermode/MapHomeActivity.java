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
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapHomeActivity extends AppCompatActivity implements SensorEventListener{

    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private SensorManager sensorManager;
    private float[] gravity;
    private float[] geomagnetic;

    private boolean live_tracking = true;
    public RotationGestureOverlay mRotationGestureOverlay;

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

        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);

        map.getOverlays().add(mRotationGestureOverlay);

        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        map.setZoomLevel(20);

        map.setMapListener(new MapListener() {
            public boolean onZoom(ZoomEvent arg0) {
                return false;
            }

            public boolean onScroll(ScrollEvent arg0) {
                //onExtentChange();
                live_tracking = false;
                mRotationGestureOverlay.setEnabled(true);
                return false;
            }
        } );;


        // Initialize MyLocationNewOverlay
        myLocationOverlay = new MyLocationNewOverlay(map);
        myLocationOverlay.enableMyLocation(); // Enable location tracking
        map.getOverlays().add(myLocationOverlay);

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return; // Exit if permissions are not granted
        }

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

                if (live_tracking){
                    myLocationOverlay.enableFollowLocation();
                    map.setZoomLevel(21);
                    rotateMap(azimuthInDegrees);

                    live_tracking = true;
                    mRotationGestureOverlay.setEnabled(false);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private float lastAzimuth = 0f; // Store the last smoothed azimuth
    private static final float ALPHA = 0.1f; // Smoothing factor (0 < alpha < 1)

    private float smoothAzimuth(float newAzimuth) {
        lastAzimuth += (newAzimuth - lastAzimuth) * ALPHA;
        return lastAzimuth;
    }

    private void rotateMap(float azimuth) {
        float smoothedAzimuth = smoothAzimuth(azimuth);
        float currentOrientation = map.getMapOrientation(); // Get current orientation
        float targetOrientation = -smoothedAzimuth; // OSMdroid uses clockwise rotation

        // Calculate the difference
        float delta = targetOrientation - currentOrientation;

        // Normalize delta to be within -180 to 180 degrees
        if (delta > 180) delta -= 360;
        if (delta < -180) delta += 360;

        // Set a small threshold for rotation to avoid frequent updates
        if (Math.abs(delta) > 1) { // Only rotate if change is significant
            map.setMapOrientation(currentOrientation + delta * 0.1f); // Smoothly rotate towards target
        } // Rotate map according to azimuth
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

    public void resetNav(View view){
        live_tracking = true;
        mRotationGestureOverlay.setEnabled(false);
   }


}