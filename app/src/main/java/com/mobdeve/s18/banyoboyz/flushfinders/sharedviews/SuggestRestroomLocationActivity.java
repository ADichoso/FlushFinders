package com.mobdeve.s18.banyoboyz.flushfinders.sharedviews;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;

import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestRestroomLocationActivity extends AppCompatActivity{
    public static final String CALLER = "CALLER";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    private Button btn_submit_restroom_location;
    private Button btn_toggle_tracking;
    private EditText et_restroom_name;
    private TextView tv_building_name;

    private String caller;

    private MapView map;

    private double chosen_latitude;
    private double chosen_longitude;
    private Marker chosen_marker;
    private boolean live_tracking;

    private MyLocationNewOverlay location_overlay;
    private Map<GeoPoint, Marker> existing_locations;
    private Marker.OnMarkerClickListener onMarkerClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_restroom_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Get views
        btn_submit_restroom_location = findViewById(R.id.btn_submit_restroom_location);
        btn_toggle_tracking = findViewById(R.id.btn_toggle_tracking);
        et_restroom_name = findViewById(R.id.et_restroom_name);
        tv_building_name = findViewById(R.id.tv_building_name);

        //Get the caller (User Suggestion or Mod/Admin Creation?)
        Intent intent = getIntent();
        caller = intent.getStringExtra("CALLER");

        //Initialize map variables
        chosen_latitude = Double.NaN;
        chosen_longitude = Double.NaN;
        chosen_marker = null;
        live_tracking = true;

        //Hashmap to store Markers to show on device
        existing_locations = new HashMap<GeoPoint, Marker>();
        //What to do when a marker is clicked
        onMarkerClickListener = new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                updateChosenLocation(marker);
                return false;
            }
        };

        //Initialize the map
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setMultiTouchControls(true);

        map.setZoomLevel(20);

        //Update the markers to show on the map whenever the user moves around the map
        map.addMapListener(new MapAdapter()
        {
            @Override
            public boolean onScroll(ScrollEvent event)
            {
                MapHelper.getInstance().updateVisibleMarkers(SuggestRestroomLocationActivity.this, map, onMarkerClickListener, existing_locations);
                return super.onScroll(event);
            }

            @Override
            public boolean onZoom(ZoomEvent event)
            {
                MapHelper.getInstance().updateVisibleMarkers(SuggestRestroomLocationActivity.this, map, onMarkerClickListener, existing_locations);
                return super.onZoom(event);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Initialize MyLocationNewOverlay (Used to get the current location of the user)
        location_overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        location_overlay.enableMyLocation(); // Enable location tracking
        map.getOverlays().add(location_overlay);

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return; // Exit if permissions are not granted
        }

        //Follow the user
        location_overlay.enableFollowLocation();

        //Create a Geopoint spawner (Whenever the user clicks on the map, suggest a new marker location)
        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false; //Do nothing for single taps
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                //For long taps, create a marker in the map
                updateChosenLocation(MapHelper.getInstance().createNewMarker(SuggestRestroomLocationActivity.this, map, p, onMarkerClickListener));

                return true;
            }
        };

        MapEventsOverlay eventsOverlay = new MapEventsOverlay(receiver);
        map.getOverlays().add(eventsOverlay);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        map.onDetach();
    }

    public void toggleTrackingButton(View view)
    {
        live_tracking = !live_tracking;

        if(live_tracking)
        {
            btn_toggle_tracking.setText("Disable Tracking");
            location_overlay.enableFollowLocation();
        }
        else
        {
            btn_toggle_tracking.setText("Enable Tracking");
            location_overlay.disableFollowLocation();
        }
    }

    public void checkAddressButton(View view)
    {
        geocodeLocation(et_restroom_name.getText().toString());
    }

    public void submitRestroomLocationButton(View view)
    {
        if(chosen_marker != null)
        {
            Intent intent = new Intent(SuggestRestroomLocationActivity.this, CreateEditRestroomActivity.class);

            intent.putExtra(CALLER, caller);
            intent.putExtra(LATITUDE, chosen_latitude);
            intent.putExtra(LONGITUDE, chosen_longitude);

            startActivity(intent);

        }
    }

    private void updateChosenLocation(Marker marker)
    {
        /* Four cases may happen when a user selects a marker
        * 1. Has not selected a marker and chose an existing location
        * 2. Has not selected a marker and chose a new location
        * 3. Has already selected a marker and chose an existing location
        * 4. Has already selected a marker and chose a new location
        * 5. Selected marker is the SAME as the one already selected
        * */

        //Case 5, DO NOT ACCEPT.
        if(marker.equals(chosen_marker))
            return;

        if(chosen_marker == null)
        {
            //For case 1 and case 2, assign the chosen marker immediately
            chosen_marker = marker;

            if(!existing_locations.containsValue(marker))
            {
                //Case 2 Additional: Assign the marker to the map overlay
                map.getOverlays().add(chosen_marker);
                chosen_marker.setTitle("NEW BUILDING LOCATION");
            }
        }
        else
        {
            //Cases 3 and 4
            //Do these steps if the previous marker was a location that existed:
            if(existing_locations.containsKey(chosen_marker.getPosition()))
            {
                //1. Instantiate a new marker at the old marker's location.
                Marker new_marker = MapHelper.getInstance().createNewMarker(this, map, chosen_marker.getPosition(), onMarkerClickListener);
                new_marker.setTitle(chosen_marker.getTitle());

                //2. Add this new marker to the map overlays.
                map.getOverlays().add(new_marker);

                //3. Update the existing location's marker.
                existing_locations.put(new_marker.getPosition(), new_marker);
            }

            //4. Remove the old marker from the map.
            map.getOverlays().remove(chosen_marker);

            //5. Update the position of the old marker
            chosen_marker = marker;

            //6. Add the new marker onto the map overlay if it is a new position
            if(!existing_locations.containsKey(chosen_marker.getPosition()))
            {
                map.getOverlays().add(chosen_marker);
                chosen_marker.setTitle("NEW BUILDING LOCATION");
            }
        }

        chosen_latitude = chosen_marker.getPosition().getLatitude();
        chosen_longitude = chosen_marker.getPosition().getLongitude();
        chosen_marker.setIcon(getDrawable(R.drawable.marker_chosen));

        tv_building_name.setText(chosen_marker.getTitle());
        map.invalidate();
    }

    private void geocodeLocation(String location_name)
    {
        new Thread(() ->
        {
            try {
                List<Address> addresses = MapHelper.getInstance().getGeocoder().getFromLocationName(location_name, 1);
                if (addresses != null && !addresses.isEmpty())
                {
                    runOnUiThread(() ->
                    {
                        GeoPoint point = new GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        map.getController().animateTo(point);

                        //Create marker for the location
                        updateChosenLocation(MapHelper.getInstance().createNewMarker(this, map, point, onMarkerClickListener));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}