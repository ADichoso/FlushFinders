package com.mobdeve.s18.banyoboyz.flushfinders.modmode;

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
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.ManageAmenitiesActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.AmenitiesAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.ManageAmenitiesAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEditRestroomActivity extends AppCompatActivity {

    RecyclerView rv_restroom_amenities;

    EditText et_restroom_name;
    EditText et_restroom_floor;
    SeekBar sb_cleanliness;
    SeekBar sb_maintenance;
    SeekBar sb_vacancy;
    Button btn_submit_restroom_info;

    private double building_latitude;
    private double building_longitude;
    private String building_name;
    private String building_picture;


    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    //Obtained the new profile picture here
                    Uri image_URI = result.getData().getData();

                    // Load the selected image into a Bitmap
                    Bitmap originalBitmap = null;
                    try
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), image_URI);
                            originalBitmap = ImageDecoder.decodeBitmap(source);
                        } else {
                            originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_URI);
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e("CreateAmenityActivity", "IMAGE NULL", e);
                        throw new RuntimeException(e);
                    }

                    if(originalBitmap != null)
                    {
                        Log.d("CreateAmenityActivity", "UPLOADED NEW IMAGE FOR USE");
                        // Crop the center 512 x 512 region of the Bitmap
                        Bitmap scaledBitmap = PictureHelper.scaleBitmap(originalBitmap);

                        building_picture = PictureHelper.encodeBitmapToBase64(scaledBitmap);
                    } else
                    {
                        Log.d("CreateAmenityActivity", "IMAGE NULL");
                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_restroom_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_restroom_amenities = findViewById(R.id.rv_restroom_amenities);
        et_restroom_name = findViewById(R.id.et_restroom_name);
        et_restroom_floor = findViewById(R.id.et_restroom_floor);
        sb_cleanliness = findViewById(R.id.sb_cleanliness);
        sb_maintenance = findViewById(R.id.sb_maintenance);
        sb_vacancy = findViewById(R.id.sb_vacancy);
        rv_restroom_amenities.setHasFixedSize(true);
        rv_restroom_amenities.setLayoutManager(new LinearLayoutManager(this));

        Intent result_intent = getIntent();
        building_latitude = result_intent.getDoubleExtra(SuggestRestroomLocationActivity.LATITUDE, Double.NaN);
        building_longitude = result_intent.getDoubleExtra(SuggestRestroomLocationActivity.LONGITUDE, Double.NaN);

        btn_submit_restroom_info = findViewById(R.id.btn_submit_restroom_info);
        btn_submit_restroom_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBuildingButton();
            }
        });

        //Open the gallery and get a photo from there
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        setResult(RESULT_OK, pickPhoto);
        activityResultLauncher.launch(pickPhoto);
    }


    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirestoreHelper.getInstance().getAmenitiesDBRef();

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                ArrayList<AmenityData> amenityData = new ArrayList<AmenityData>();

                for(QueryDocumentSnapshot document : task.getResult())
                {
                    Map<String, Object> data = document.getData();
                    amenityData.add
                    (
                        new AmenityData
                        (
                                document.getId(),
                                data.get(FirestoreReferences.Amenities.PICTURE).toString()
                        )
                    );
                    Log.d("ManageAmenitiesActivity", document.getId());
                }
                AmenitiesAdapter amenitiesAdapter = new AmenitiesAdapter(amenityData, CreateEditRestroomActivity.this);
                rv_restroom_amenities.setAdapter(amenitiesAdapter);
            }
            else
            {
                Log.w("ManageAmenitiesActivity", "TASK NOT SUCCESSFUL", task.getException());
            }
        });
    }

    public void createBuildingButton()
    {
        //1. Check if the chosen building's location already exists
        FirestoreHelper.getInstance().readBuilding(building_latitude, building_longitude, task ->
        {
            if(task.isSuccessful())
            {
                Map<String, Object> old_building_data = task.getResult().getData();

                if(old_building_data == null || old_building_data.isEmpty())
                {
                    //The building DOES NOT EXIST, You need to insert a building entry first

                    getAddressFromGeoPoint(address ->
                    {
                        building_name = et_restroom_name.getText().toString();

                        Map<String, Object> new_building_data = FirestoreHelper.getInstance().createBuildingData
                                (
                                        building_name,
                                        address,
                                        PictureHelper.decodeBase64ToBitmap(building_picture)
                                );

                        FirestoreHelper.getInstance().insertBuilding(building_latitude, building_longitude, new_building_data, task1 ->
                        {
                            if(task1.isSuccessful())
                            {
                                //Building has been inserted
                                createNewRestroom();
                            }
                        });
                    });

                }
                else
                {
                    //Set the building name in the edit text already, and disable all changes for that
                    building_name = old_building_data.get(FirestoreReferences.Buildings.NAME).toString();

                    et_restroom_name.setText(building_name);
                    et_restroom_name.setFocusable(false);
                    et_restroom_name.setFocusableInTouchMode(false);
                    et_restroom_name.setCursorVisible(false); // Hide the cursor
                    et_restroom_name.setKeyListener(null);
                }
            }
            else
            {
                //Create the restroom
                createNewRestroom();
            }
        });


    }

    public void createNewRestroom()
    {
        //In this point, the building for the restroom should already exist.

        //TODO: Create Restroom Support
        String restroom_name = et_restroom_floor.getText().toString();
        int cleanliness = sb_cleanliness.getProgress();
        int maintenance = sb_maintenance.getProgress();
        int vacancy = sb_vacancy.getProgress();

        ArrayList<String> enabledAmenities = new ArrayList<String>();

        //Go through amenities to check which ones are switched on
        for (int i = 0; i < rv_restroom_amenities.getChildCount(); i++) {
            View itemView = rv_restroom_amenities.getChildAt(i);
            AmenitiesAdapter.AmenityHolder viewHolder = (AmenitiesAdapter.AmenityHolder) rv_restroom_amenities.getChildViewHolder(itemView);

            //Check if enabled yung switch thingy
            if(viewHolder.isSwitchOn())
            {
                //Switch is on, get the name of the amenity
                enabledAmenities.add(viewHolder.getAmenityName());
            }
        }

        Map<String, Object> data = FirestoreHelper.getInstance().createRestroomData
        (
            restroom_name,
            cleanliness,
            maintenance,
            vacancy,
            enabledAmenities
        );

        String restroom_id = FirebaseFirestore.getInstance().collection(FirestoreReferences.Restrooms.COLLECTION).document().getId();
        FirestoreHelper.getInstance().insertRestroom(restroom_id, data, task -> {
            if(task.isSuccessful())
            {
                //Update Building with new RestroomData
                FirestoreHelper.getInstance().appendStringToStringArray
                        (
                                FirestoreReferences.Buildings.COLLECTION,
                                FirestoreHelper.getInstance().generateBuildingID(building_latitude, building_longitude),
                                FirestoreReferences.Buildings.RESTROOMS,
                                restroom_id
                        );
                goToHome();
            }
        });


    }

    private void goToHome()
    {
        Intent intent = new Intent(CreateEditRestroomActivity.this, ModHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void getAddressFromGeoPoint(AddressCallback callback) {
        new Thread(() -> {
            try {
                List<Address> addresses = MapHelper.getInstance().getGeocoder().getFromLocation(building_latitude, building_longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    StringBuilder fullAddress = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        fullAddress.append(address.getAddressLine(i));
                        if (i < address.getMaxAddressLineIndex()) {
                            fullAddress.append(", "); // Add comma between lines
                        }
                    }

                    runOnUiThread(() -> callback.onAddressRetrieved(fullAddress.toString()));
                } else {
                    runOnUiThread(() -> callback.onAddressRetrieved(null)); // No address found
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> callback.onAddressRetrieved(null)); // Error case
            }
        }).start();
    }

    public interface AddressCallback {
        void onAddressRetrieved(String address);
    }
}