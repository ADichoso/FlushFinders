package com.mobdeve.s18.banyoboyz.flushfinders.helper;

import android.location.Address;
import android.util.Log;

import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;


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
}
