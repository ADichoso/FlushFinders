package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.accounts.AccountHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapHomeActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    public static final String BUILDING_ID = "BUILDING_ID";
    private static final float ALPHA = 0.05f; // Smoothing factor (0 < alpha < 1)
    private float last_azimuth; // Store the last smoothed azimuth
    private SensorManager sensor_manager;
    private float[] gravity;
    private float[] geomagnetic;

    private TextView tv_chosen_building_name;
    private TextView tv_chosen_building_address;
    private EditText et_search_restroom_name;
    private Button btn_reset_tracking;
    private MapView map;

    private GeoPoint curr_location;
    private Marker chosen_marker;
    private String chosen_location;
    private Map<GeoPoint, Marker> existing_locations;
    private Marker.OnMarkerClickListener onMarkerClickListener;

    private MyLocationNewOverlay location_overlay;
    private boolean live_tracking;

    private RotationGestureOverlay rotation_gesture_overlay;

    private ExecutorService network_executor;
    private RoadManager road_manager;
    private Polyline user_route;
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

        // Initialize Sensor Manager
        sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        last_azimuth = 0f;

        //Initialize map
        tv_chosen_building_name = findViewById(R.id.tv_chosen_building_name);
        tv_chosen_building_address = findViewById(R.id.tv_chosen_building_address);
        et_search_restroom_name = findViewById(R.id.et_search_restroom_name);
        btn_reset_tracking = findViewById(R.id.btn_reset_tracking);

        btn_reset_tracking.bringToFront();

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        map.setZoomLevel(20);

        map.addMapListener(new MapAdapter() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                toggleTracking(false);
                rotation_gesture_overlay.setEnabled(true);
                MapHelper.getInstance().updateVisibleMarkers(MapHomeActivity.this, map, onMarkerClickListener, existing_locations);
                return super.onScroll(event);
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                MapHelper.getInstance().updateVisibleMarkers(MapHomeActivity.this, map, onMarkerClickListener, existing_locations);
                return super.onZoom(event);
            }
        });

        //Initialize map variables to use for tracking
        chosen_marker = null;
        existing_locations = new HashMap<GeoPoint, Marker>();
        onMarkerClickListener = new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                updateChosenLocation(marker);
                toggleTracking(false);
                mapView.getController().animateTo(marker.getPosition());
                return false;
            }
        };
        toggleTracking(true);

        //Initialize rotation overlay to enable map rotation for the user
        rotation_gesture_overlay = new RotationGestureOverlay(map);
        rotation_gesture_overlay.setEnabled(true);

        map.getOverlays().add(rotation_gesture_overlay);


        location_overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        location_overlay.enableMyLocation();
        location_overlay.runOnFirstFix(() -> {
            curr_location = new GeoPoint(location_overlay.getMyLocation());
        });

        map.getOverlays().add(location_overlay);


        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //ROAD generation stuff
        network_executor = Executors.newSingleThreadExecutor();
        road_manager = new OSRMRoadManager(this, "FlushFinder");
        user_route = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume();

        sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensor_manager.SENSOR_DELAY_UI);
        sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), sensor_manager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        sensor_manager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        location_overlay.disableMyLocation(); // Disable location tracking when done
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values;
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values;

        if (gravity != null && geomagnetic != null)
        {
            float[] R = new float[9];
            float[] I = new float[9];
            if (sensor_manager.getRotationMatrix(R, I, gravity, geomagnetic))
            {
                float[] orientation = new float[3];
                sensor_manager.getOrientation(R, orientation);

                float new_azimuth = (float) Math.toDegrees(orientation[0]);

                curr_location = location_overlay.getMyLocation();

                if (live_tracking)
                {
                    map.setZoomLevel(21);
                    map.getController().animateTo(curr_location);
                    rotateMap(new_azimuth, 1f, 0.1f);
                    rotation_gesture_overlay.setEnabled(false);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Nothing
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    public void viewChosenBuildingInfoButton(View view)
    {
        if(chosen_location.isEmpty())
            return;

        //Go the a new activity to show the restrooms available in that building
        Intent intent = new Intent(MapHomeActivity.this, ViewBuildingActivity.class);

        intent.putExtra(BUILDING_ID, chosen_location);

        startActivity(intent);
    }

    public void searchLocationButton(View view) {

        String location_name = et_search_restroom_name.getText().toString();

        new Thread(() -> {
            try {
                List<Address> addresses = MapHelper.getInstance().getGeocoder().getFromLocationName(location_name, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    runOnUiThread(() -> {
                        GeoPoint point = new GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        toggleTracking(false);
                        map.getController().animateTo(point);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void toggleTracking(boolean new_tracking)
    {
        live_tracking = new_tracking;

        if(location_overlay == null)
            return;

        if(new_tracking)
            location_overlay.enableFollowLocation();
        else
            location_overlay.disableFollowLocation();
    }

    public void recommendedRestroomsButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SavedBuildingsActivity.class);

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

    public void resetTrackingButton(View view){
        toggleTracking(true);
        rotation_gesture_overlay.setEnabled(false);
    }

    public void clearBuildingSelectionButton(View view)
    {
        clearLocationSelection();
    }


    private void updateCurrentLocation(GeoPoint point) {
        curr_location = point; // Save the current location as a GeoPoint
    }

    private void clearLocationSelection()
    {
        if(chosen_marker == null)
            return;

        chosen_location = "";

        Marker new_marker = MapHelper.getInstance().createNewMarker(this, map, chosen_marker.getPosition(), onMarkerClickListener);
        new_marker.setTitle(chosen_marker.getTitle());
        existing_locations.put(new_marker.getPosition(), new_marker);

        map.getOverlays().remove(user_route);
        map.getOverlays().remove(chosen_marker);
        chosen_marker = null;

        map.invalidate();

        tv_chosen_building_name.setText("BUILDING NAME");
        tv_chosen_building_address.setText("ADDRESS");
    }

    private float smoothAzimuth(float newAzimuth) {
        last_azimuth += (newAzimuth - last_azimuth) * ALPHA;
        return last_azimuth;
    }

    private void rotateMap(float azimuth, float rotation_threshold, float smoothing_factor) {
        float smoothed_azimuth = smoothAzimuth(azimuth);
        float curr_orientation = map.getMapOrientation(); // Get current orientation
        float target_orientation = -smoothed_azimuth; // OSMdroid uses clockwise rotation

        // Calculate the difference between the orientations
        float difference = target_orientation - curr_orientation;

        // Normalize delta to be within -180 to 180 degrees
        if (difference > 180) difference -= 360;
        if (difference < -180) difference += 360;

        // Set a small threshold for rotation to avoid frequent updates
        if (Math.abs(difference) > rotation_threshold) {
            map.setMapOrientation(curr_orientation + difference * smoothing_factor); // Smoothly rotate towards target
        } // Rotate map according to azimuth
    }

    private void updateChosenLocation(Marker marker)
    {
        /* Three cases may happen when a user selects a marker
         * 1. Has not selected a marker.
         * 2. Has already selected a marker.
         * 3. Selected marker is the SAME as the one already selected
         * */

        //Case 3, DO NOT ACCEPT.
        if(marker.equals(chosen_marker))
            return;

        //Case 1
        if(chosen_marker == null)
        {
            //Assign the chosen marker immediately
            chosen_marker = marker;
        }
        else
        {
            //Case 2

            //1. Instantiate a new marker at the old marker's location.
            Marker new_marker = MapHelper.getInstance().createNewMarker(this, map, chosen_marker.getPosition(), onMarkerClickListener);
            new_marker.setTitle(chosen_marker.getTitle());

            //3. Update the existing location's marker.
            existing_locations.put(new_marker.getPosition(), new_marker);

            //4. Remove the old marker from the map.
            map.getOverlays().remove(chosen_marker);

            //5. Update the position of the old marker
            chosen_marker = marker;
        }

        FirestoreHelper.getInstance().readBuilding(
                chosen_marker.getPosition().getLatitude(),
                chosen_marker.getPosition().getLongitude(), task -> {
                    if(task.isSuccessful())
                    {
                        Map<String, Object> building_data = task.getResult().getData();
                        tv_chosen_building_name.setText(building_data.get(FirestoreReferences.Buildings.NAME).toString());
                        tv_chosen_building_address.setText(building_data.get(FirestoreReferences.Buildings.ADDRESS).toString());
                    }
                });

        chosen_location = MapHelper.getInstance().generateBuildingAddress(chosen_marker.getPosition());
        chosen_marker.setIcon(getDrawable(R.drawable.marker_chosen));

        generateUserRoute();
        map.invalidate();
    }

    private void generateUserRoute()
    {
        network_executor.execute(() ->
        {
            ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

            waypoints.add(curr_location);
            waypoints.add(chosen_marker.getPosition());

            Road current_road = road_manager.getRoad(waypoints);

            if(current_road.mStatus == Road.STATUS_OK)
            {
                if(user_route != null)
                    map.getOverlays().remove(user_route);

                user_route = RoadManager.buildRoadOverlay(current_road);
                user_route.setWidth(10.0f);
                map.getOverlays().add(user_route);
            }
        });

    }
}