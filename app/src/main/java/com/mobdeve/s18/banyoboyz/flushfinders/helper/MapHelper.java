package com.mobdeve.s18.banyoboyz.flushfinders.helper;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.Map;


public class MapHelper {

    private static MapHelper instance = null;

    private GeocoderNominatim geocoder;
    public static MapHelper getInstance()
    {
        if(instance == null){
            instance = new MapHelper();
        }

        return instance;
    }

    public GeocoderNominatim getGeocoder()
    {
        return geocoder;
    }

    public MapHelper()
    {
        // Initialize Nominatim Geocoder
        geocoder = new GeocoderNominatim("FlushFinder");
    }

    public String generateBuildingAddress(GeoPoint point)
    {
        return generateBuildingAddress(point.getLatitude(), point.getLongitude());
    }

    public String generateBuildingAddress(Double building_latitude, Double building_longitude)
    {
        try
        {
            List<Address> addresses = geocoder.getFromLocation(building_latitude, building_longitude, 1);
            if (addresses != null && !addresses.isEmpty())
            {
                return addresses.get(0).getAddressLine(0);
            } else
            {
                return "NO ADDRESS FOUND";
            }
        }
        catch (Exception e)
        {
            Log.e("MapHelper", e.toString());
            return "ERROR RETRIEVING ADDRESS";
        }
    }

    public void loadVisibleMarkers(Context context, MapView map, Marker.OnMarkerClickListener onMarkerClickListener, Map<GeoPoint, Marker> existingLocations) {
        // Get the current bounding box of the map view
        BoundingBox boundingBox = map.getBoundingBox();

        // Query Firestore for locations within the bounding box
        FirestoreHelper.getInstance().getBuildingsDBRef() // Replace with your collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GeoPoint point = FirestoreHelper.getInstance().decodeBuildingLocation(document.getId());

                            if(existingLocations.containsKey(point) && !boundingBox.contains(point))
                            {
                                //Already exists, but outside of the viewable space. Remove this point.
                                map.getOverlays().remove(existingLocations.get(point));
                                existingLocations.remove(point);
                            }
                            else if (!existingLocations.containsKey(point) && boundingBox.contains(point)) {
                                //Point is not currently on display, but should be seen in the map space. Display this point.
                                Marker marker = createNewMarker(context, map, point, onMarkerClickListener);
                                marker.setTitle(document.getString(FirestoreReferences.Buildings.NAME));
                                existingLocations.put(point, marker);
                            }
                        }

                        map.invalidate();
                    }
                });
    }

    public Marker createNewMarker(Context context, MapView map, GeoPoint point, Marker.OnMarkerClickListener onMarkerClickListener)
    {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setIcon(context.getDrawable(R.drawable.marker_open));
        marker.setOnMarkerClickListener(onMarkerClickListener);

        map.getOverlays().add(marker);

        return marker;
    }

    public void updateVisibleMarkers(Context context, MapView map, Marker.OnMarkerClickListener onMarkerClickListener,  Map<GeoPoint, Marker> existingLocations) {
        Log.d("SuggestRestroomLocationActivity", "Updating Visible Markers");

        // Load markers again based on new visibility
        loadVisibleMarkers(context, map, onMarkerClickListener, existingLocations);
    }
}
