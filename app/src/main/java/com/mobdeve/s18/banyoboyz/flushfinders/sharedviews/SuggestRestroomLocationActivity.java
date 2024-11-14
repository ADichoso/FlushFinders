package com.mobdeve.s18.banyoboyz.flushfinders.sharedviews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.CreateEditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SuggestRestroomDetailsActivity;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.List;

public class SuggestRestroomLocationActivity extends AppCompatActivity {
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String BUILDING_PICTURE = "BUILDING_PICTURE";

    private MapView map;
    private GeocoderNominatim geocoder;

    private double chosenLatitude;
    private double chosenLongitude;
    Button btn_submit_restroom_location;
    EditText et_restroom_name;


    private String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_restroom_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        double chosenLatitude = Double.NaN;
        double chosenLongitude = Double.NaN;

        btn_submit_restroom_location = findViewById(R.id.btn_submit_restroom_location);
        et_restroom_name = findViewById(R.id.et_restroom_name);

        Intent intent = getIntent();
        caller = intent.getStringExtra("CALLER");

        geocoder = MapHelper.getInstance().getGeocoder();

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        map.setZoomLevel(20);
    }

    public void checkAddress(View view)
    {
        Log.d("SuggestRestroomLocationActivity", et_restroom_name.getText().toString());
        geocodeLocation(et_restroom_name.getText().toString());
    }

    private void geocodeLocation(String locationName) {
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    chosenLatitude = addresses.get(0).getLatitude();
                    chosenLongitude = addresses.get(0).getLongitude();
                    runOnUiThread(() -> {
                        GeoPoint point = new GeoPoint(chosenLatitude, chosenLongitude);
                        map.getController().animateTo(point);
                        // Optionally add a marker at the location

                        Marker marker = new Marker(map);
                        marker.setPosition(point);
                        map.getOverlays().add(marker);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void submitRestroomLocation(View view)
    {
        if(chosenLatitude != Double.NaN && chosenLongitude != Double.NaN)
        {
            Intent i = new Intent(SuggestRestroomLocationActivity.this, SuggestRestroomDetailsActivity.class);

            if(caller != null && caller.equals("MOD_ADMIN"))
            {
                i = new Intent(SuggestRestroomLocationActivity.this, CreateEditRestroomActivity.class);
            }

            i.putExtra(LATITUDE, chosenLatitude);
            i.putExtra(LONGITUDE, chosenLongitude);

            startActivity(i);

        }
    }
}