package com.mobdeve.s18.banyoboyz.flushfinders.helper;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MapHelper
{
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

    public String encodeBuildingID(GeoPoint point)
    {
        return encodeBuildingID(point.getLatitude(), point.getLongitude());
    }

    public String encodeBuildingID(Double building_latitude, Double building_longitude)
    {
        return Double.toString(building_latitude) + "!" + Double.toString(building_longitude);
    }

    public GeoPoint decodeBuildingLocation(String building_id)
    {
        double latitude = 0;
        double longitude = 0;

        String[] coords = building_id.split("!");

        try
        {
            latitude = Double.parseDouble(coords[0]);
            longitude = Double.parseDouble(coords[1]);
        }
        catch (Exception e){}

        return new GeoPoint(latitude, longitude);
    }

    public void loadVisibleMarkers(Context context, MapView map, Marker.OnMarkerClickListener onMarkerClickListener, Map<GeoPoint, Marker> existingLocations, boolean show_hidden)
    {
        // Get the current bounding box of the map view
        BoundingBox bounding_box = map.getBoundingBox();

        // Query Firestore for locations within the bounding box
        FirestoreHelper.getInstance().getBuildingsDBRef()
            .whereEqualTo(FirestoreReferences.Buildings.SUGGESTION, false) // Filter non-suggested buildings
            .get()
            .addOnCompleteListener(task ->
            {
                if (!task.isSuccessful())
                    return;

                QuerySnapshot current_building_documents = task.getResult();

                if(current_building_documents == null || current_building_documents.isEmpty())
                    return;

                Map<GeoPoint, String> current_locations = new HashMap<GeoPoint, String>();

                //1. Get the current locations that should be visible in the map
                for (QueryDocumentSnapshot document : current_building_documents)
                {
                    //Check if restrooms is empty
                    ArrayList<String> restrooms_ids = (ArrayList<String>) document.get(FirestoreReferences.Buildings.RESTROOMS);

                    if((restrooms_ids == null || restrooms_ids.isEmpty()) && !show_hidden)
                        continue;

                    GeoPoint point = decodeBuildingLocation(document.getId());

                    if(bounding_box.contains(point))
                        current_locations.put(point, document.getString(FirestoreReferences.Buildings.NAME));
                }

                //If nothing changed during the update, no need to update anything.
                if(current_locations.keySet().equals(existingLocations.keySet()))
                    return;

                //2. Go through the current points in the map and remove ones not found in the current locations
                Iterator<GeoPoint> iterator = existingLocations.keySet().iterator();
                while (iterator.hasNext()) {
                    GeoPoint existing_point = iterator.next();
                    if (!current_locations.containsKey(existing_point)) {
                        map.getOverlays().remove(existingLocations.get(existing_point));
                        iterator.remove(); // Safely remove the element using the iterator
                    }
                }

                //3. Update the map to show the newly found points
                for(GeoPoint current_point: current_locations.keySet())
                {
                    if(!existingLocations.containsKey(current_point))
                    {
                        //Point is not currently on display, but should be seen in the map space. Display this point.
                        Marker marker = createNewMarker(context, map, current_point, onMarkerClickListener);
                        marker.setTitle(current_locations.get(current_point));
                        existingLocations.put(current_point, marker);
                    }
                }

                //Refresh map
                map.invalidate();
            });


    }

    public Marker createNewMarker(Context context, MapView map, GeoPoint point, Marker.OnMarkerClickListener onMarkerClickListener)
    {
        //Create a new marker
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setIcon(context.getDrawable(R.drawable.marker_open));
        marker.setOnMarkerClickListener(onMarkerClickListener);

        map.getOverlays().add(marker);

        return marker;
    }

    public void updateVisibleMarkers(Context context, MapView map, Marker.OnMarkerClickListener onMarkerClickListener,  Map<GeoPoint, Marker> existingLocations, boolean showHidden)
    {
        Log.d("SuggestRestroomLocationActivity", "Updating Visible Markers");

        // Load markers again based on new visibility
        loadVisibleMarkers(context, map, onMarkerClickListener, existingLocations, showHidden);
    }
}
