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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.accounts.AccountHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderBasic;
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

public class MapHomeActivity extends AppCompatActivity implements SensorEventListener
{
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String BUILDING_ID = ViewRestroomActivity.BUILDING_ID;
    public static final String RESTROOM_ID = ViewRestroomActivity.RESTROOM_ID;
    private static final float ALPHA = 0.05f; // Smoothing factor (0 < alpha < 1)
    private float last_azimuth; // Store the last smoothed azimuth
    private SensorManager sensor_manager;
    private float[] gravity;
    private float[] geomagnetic;

    private CardView cv_building_info;
    private CardView cv_route_tracking;
    private CardView cv_restroom_rating;
    private TextView tv_route_directions;
    private TextView tv_route_info;
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

    private GeoPoint destination_location;
    private boolean direction_mode;
    private String building_id;
    private String restroom_id;

    private RotationGestureOverlay rotation_gesture_overlay;

    private ExecutorService network_executor;
    private RoadManager road_manager;
    private Polyline user_route;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Map Config
        Configuration.getInstance().setCacheMapTileCount((short) 12); // Adjust based on memory constraints
        Configuration.getInstance().setCacheMapTileOvershoot((short) 0);
        Configuration.getInstance().setTileFileSystemCacheMaxBytes(50L * 1024 * 1024); // 50 MB
        Configuration.getInstance().setTileDownloadThreads((short) 2); // Limit concurrent downloads
        Configuration.getInstance().setTileDownloadMaxQueueSize((short) 20); // Adjust to prevent overwhelming the memory

        // Initialize Sensor Manager
        sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        last_azimuth = 0f;

        //Initialize map
        cv_building_info = findViewById(R.id.cv_building_info);
        cv_route_tracking = findViewById(R.id.cv_route_tracking);
        cv_restroom_rating = findViewById(R.id.cv_restroom_rating);
        tv_route_directions = findViewById(R.id.tv_route_directions);
        tv_route_info = findViewById(R.id.tv_route_info);
        tv_chosen_building_name = findViewById(R.id.tv_chosen_building_name);
        tv_chosen_building_address = findViewById(R.id.tv_chosen_building_address);
        et_search_restroom_name = findViewById(R.id.et_search_restroom_name);
        btn_reset_tracking = findViewById(R.id.btn_reset_tracking);

        cv_restroom_rating.setActivated(false);
        cv_restroom_rating.setVisibility(View.INVISIBLE);
        btn_reset_tracking.bringToFront();

        map = findViewById(R.id.map);
        MapTileProviderBasic tileProvider = new MapTileProviderBasic(this);
        TileSourceFactory.addTileSource(TileSourceFactory.MAPNIK);
        tileProvider.createTileCache();
        map.setTileProvider(tileProvider);

        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        map.setMinZoomLevel(10.0);
        map.setMaxZoomLevel(25.0);

        map.setZoomLevel(20);

        map.addMapListener(new MapAdapter()
        {
            @Override
            public boolean onScroll(ScrollEvent event)
            {
                toggleTracking(false);
                rotation_gesture_overlay.setEnabled(true);
                MapHelper.getInstance().updateVisibleMarkers(MapHomeActivity.this, map, onMarkerClickListener, existing_locations, false);
                return super.onScroll(event);
            }

            @Override
            public boolean onZoom(ZoomEvent event)
            {
                MapHelper.getInstance().updateVisibleMarkers(MapHomeActivity.this, map, onMarkerClickListener, existing_locations, false);
                return super.onZoom(event);
            }
        });

        //Initialize map variables to use for tracking
        chosen_marker = null;
        existing_locations = new HashMap<GeoPoint, Marker>();
        onMarkerClickListener = (marker, mapView) ->
        {
            updateChosenLocation(marker);
            toggleTracking(false);
            mapView.getController().animateTo(marker.getPosition());
            return false;
        };

        //Initialize rotation overlay to enable map rotation for the user
        rotation_gesture_overlay = new RotationGestureOverlay(map);
        rotation_gesture_overlay.setEnabled(true);

        map.getOverlays().add(rotation_gesture_overlay);


        location_overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        location_overlay.enableMyLocation();
        location_overlay.runOnFirstFix(() -> curr_location = new GeoPoint(location_overlay.getMyLocation()));

        map.getOverlays().add(location_overlay);

        Intent intent = getIntent();
        building_id = intent.getStringExtra(BUILDING_ID);
        restroom_id = intent.getStringExtra(RESTROOM_ID);

        toggleTracking(true);

        toggleDirectionMode(!(building_id == null || building_id.isEmpty()));


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
    protected void onResume()
    {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume();

        sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensor_manager.SENSOR_DELAY_UI);
        sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), sensor_manager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        map.onPause();
        sensor_manager.unregisterListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        location_overlay.disableMyLocation(); // Disable location tracking when done
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
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

                if(direction_mode)
                {
                    generateUserRoute(destination_location);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {
        //Nothing
    }

    public void viewChosenBuildingInfoButton(View view)
    {
        if(chosen_location == null || chosen_location.isEmpty() || chosen_marker == null)
            return;

        //Go the a new activity to show the restrooms available in that building
        Intent intent = new Intent(MapHomeActivity.this, ViewBuildingActivity.class);

        intent.putExtra(BUILDING_ID, chosen_location);

        startActivity(intent);
    }

    public void searchLocationButton(View view)
    {
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

    private void toggleDirectionMode(boolean new_direction_mode)
    {
        direction_mode = new_direction_mode;

        if(direction_mode)
        {
            cv_route_tracking.setVisibility(View.VISIBLE);
            cv_route_tracking.setActivated(true);

            cv_building_info.setVisibility(View.INVISIBLE);
            cv_building_info.setActivated(false);

            destination_location = MapHelper.getInstance().decodeBuildingLocation(building_id);
        }
        else
        {
            cv_building_info.setVisibility(View.VISIBLE);
            cv_building_info.setActivated(true);

            cv_route_tracking.setVisibility(View.INVISIBLE);
            cv_route_tracking.setActivated(false);
        }
    }

    public void recommendedRestroomsButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SavedRestroomsActivity.class);

        intent.putExtra(LATITUDE, curr_location.getLatitude());
        intent.putExtra(LONGITUDE, curr_location.getLongitude());

        startActivity(intent);
    }

    public void accountHomeButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, AccountHomeActivity.class);

        startActivity(intent);
    }

    public void rateRestroomsButton(View view)
    {
        if(building_id == null || building_id.isEmpty() || restroom_id == null || restroom_id.isEmpty())
            return;

        Intent intent = new Intent(MapHomeActivity.this, ReviewReportRestroomActivity.class);

        intent.putExtra(BUILDING_ID, building_id);
        intent.putExtra(RESTROOM_ID, restroom_id);

        startActivity(intent);
    }

    public void closeRatingButton(View view)
    {
        cv_restroom_rating.setVisibility(View.INVISIBLE);
        cv_restroom_rating.setActivated(false);
    }

    public void suggestRestroomButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SuggestRestroomLocationActivity.class);

        intent.putExtra(SuggestRestroomLocationActivity.CALLER, "USER");
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

    private void updateCurrentLocation(GeoPoint point)
    {
        curr_location = point; // Save the current location as a GeoPoint
    }

    private void clearLocationSelection()
    {
        if(building_id != null && restroom_id != null && !building_id.isEmpty() && !restroom_id.isEmpty())
            toggleDirectionMode(true);

        if(chosen_marker != null)
        {
            chosen_location = "";
            Marker new_marker = MapHelper.getInstance().createNewMarker(this, map, chosen_marker.getPosition(), onMarkerClickListener);
            new_marker.setTitle(chosen_marker.getTitle());
            existing_locations.put(new_marker.getPosition(), new_marker);
            map.getOverlays().remove(chosen_marker);
            chosen_marker = null;
        }

        if(user_route != null)
            map.getOverlays().remove(user_route);

        map.invalidate();

        tv_chosen_building_name.setText("BUILDING NAME");
        tv_chosen_building_address.setText("ADDRESS");
    }

    private float smoothAzimuth(float newAzimuth)
    {
        last_azimuth += (newAzimuth - last_azimuth) * ALPHA;
        return last_azimuth;
    }

    private void rotateMap(float azimuth, float rotation_threshold, float smoothing_factor)
    {
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

        chosen_location = MapHelper.getInstance().encodeBuildingID(chosen_marker.getPosition());
        chosen_marker.setIcon(getDrawable(R.drawable.marker_chosen));

        if(!direction_mode) generateUserRoute(chosen_marker.getPosition());
        map.invalidate();
    }

    private void generateUserRoute(GeoPoint point)
    {
        network_executor.execute(() ->
        {
            ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

            if(curr_location == null || point == null)
                return;

            waypoints.add(curr_location);
            waypoints.add(point);

            Road current_road = road_manager.getRoad(waypoints);


            if(current_road.mStatus == Road.STATUS_OK)
            {
                if(user_route != null)
                    map.getOverlays().remove(user_route);

                if(direction_mode)
                {
                    waypoints = new ArrayList<GeoPoint>();
                    for(RoadNode node : current_road.mNodes)
                        waypoints.add(node.mLocation);

                    Road refined_road = road_manager.getRoad(waypoints);

                    //Overall distance found at legs
                    current_road.buildLegs(waypoints);

                    //Instructions found at the CLOSEST NODE
                    double min_distance = -1.0;
                    int min_index = -1;
                    for(RoadNode node: current_road.mNodes)
                    {
                        if(node.mInstructions == null || node.mInstructions.isEmpty() || node.mInstructions.contains("waypoint"))
                            continue;

                        double curr_distance = curr_location.distanceToAsDouble(node.mLocation);
                        if(min_index == -1 || curr_distance < min_distance)
                        {
                            min_index = current_road.mNodes.indexOf(node);
                            min_distance = curr_distance;
                        }
                    }

                    //Found Closest Node
                    int finalMin_index = min_index;
                    runOnUiThread(()->
                    {
                        Double distance = refined_road.mLegs.get(0).mLength;
                        String direction_info = distance.toString() + "m";

                        if(finalMin_index == -1)
                        {
                            //TAPOS NA YOU IN DESTINATION
                            tv_route_info.setText("YOU HAVE ARRIVED AT YOUR DESTINATION!");
                            tv_route_directions.setText("0 METERS");

                            toggleDirectionMode(false);
                            cv_restroom_rating.setActivated(true);
                            cv_restroom_rating.setVisibility(View.VISIBLE);
                            map.invalidate();
                        }
                        else
                        {
                            tv_route_info.setText("TOTAL: " + current_road.getLengthDurationText(this, -1));
                            tv_route_directions.setText("( " +  direction_info + ") | " + current_road.mNodes.get(finalMin_index).mInstructions);
                        }


                    });
                }

                user_route = RoadManager.buildRoadOverlay(current_road);
                user_route.setWidth(10.0f);
                map.getOverlays().add(user_route);
            }

            map.invalidate();
        });
    }
}