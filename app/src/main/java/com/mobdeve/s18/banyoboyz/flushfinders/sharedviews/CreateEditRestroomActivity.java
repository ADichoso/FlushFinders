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
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.LoginActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.AmenityAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.EditRestroomAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateEditRestroomActivity extends AppCompatActivity {

    private RecyclerView rv_restroom_amenities;

    private EditText et_restroom_name;
    private EditText et_restroom_floor;
    private SeekBar sb_cleanliness;
    private SeekBar sb_maintenance;
    private SeekBar sb_vacancy;
    private Button btn_submit_restroom_info;

    private boolean suggestion;
    private double building_latitude;
    private double building_longitude;
    private String restroom_id;
    private String building_name;
    private String building_picture;

    private ArrayList<AmenityData> amenity_list;
    private AmenityAdapter amenity_adapter;
    private final ActivityResultLauncher<Intent> activity_result_launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->
            {
                if (result.getResultCode() != RESULT_OK)
                    return;

                //Obtained the new profile picture here
                Uri image_URI = result.getData().getData();

                // Load the selected image into a Bitmap
                Bitmap raw_bitmap = null;
                try
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), image_URI);
                        raw_bitmap = ImageDecoder.decodeBitmap(source);
                    } else
                        raw_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_URI);
                }
                catch (IOException e)
                {
                    Log.e("CreateAmenityActivity", "IMAGE NULL", e);
                    throw new RuntimeException(e);
                }

                if(raw_bitmap != null)
                {
                    Log.d("CreateAmenityActivity", "UPLOADED NEW IMAGE FOR USE");

                    // Crop the center 512 x 512 region of the Bitmap
                    Bitmap scaled_bitmap = PictureHelper.scaleBitmap(raw_bitmap, 256, 256);
                    building_picture = PictureHelper.encodeBitmapToBase64(scaled_bitmap);
                }
            }
    );


    private boolean building_exists;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_restroom_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        amenity_list = new ArrayList<AmenityData>();

        rv_restroom_amenities = findViewById(R.id.rv_restroom_amenities);
        et_restroom_name = findViewById(R.id.et_restroom_name);
        et_restroom_floor = findViewById(R.id.et_restroom_floor);
        sb_cleanliness = findViewById(R.id.sb_cleanliness);
        sb_maintenance = findViewById(R.id.sb_maintenance);
        sb_vacancy = findViewById(R.id.sb_vacancy);
        rv_restroom_amenities.setHasFixedSize(true);
        rv_restroom_amenities.setLayoutManager(new LinearLayoutManager(this));

        Intent result_intent = getIntent();
        suggestion = Objects.equals(result_intent.getStringExtra(SuggestRestroomLocationActivity.CALLER), "USER");
        building_latitude = result_intent.getDoubleExtra(SuggestRestroomLocationActivity.LATITUDE, Double.NaN);
        building_longitude = result_intent.getDoubleExtra(SuggestRestroomLocationActivity.LONGITUDE, Double.NaN);
        restroom_id = result_intent.getStringExtra(EditRestroomAdapter.RESTROOM_ID);

        btn_submit_restroom_info = findViewById(R.id.btn_submit_restroom_info);
        btn_submit_restroom_info.setOnClickListener(view -> createBuildingButton());

        checkBuilding();
    }

    private void fillRestroomInfo()
    {
        FirestoreHelper.getInstance().readRestroom(restroom_id, task ->
        {
            if (!task.isSuccessful())
                return;

            DocumentSnapshot restroom_document = task.getResult();

            if(restroom_document == null || !restroom_document.exists())
                return;

            et_restroom_floor.setText(restroom_document.getString(FirestoreReferences.Restrooms.NAME));
            sb_cleanliness.setProgress(restroom_document.get(FirestoreReferences.Restrooms.CLEANLINESS, Integer.class));
            sb_maintenance.setProgress(restroom_document.get(FirestoreReferences.Restrooms.MAINTENANCE, Integer.class));
            sb_vacancy.setProgress(restroom_document.get(FirestoreReferences.Restrooms.VACANCY, Integer.class));

            Log.d("CreateEditRestroomActivity", "Updated Others");
            //Get the amenities
            ArrayList<String> amenities_ids = (ArrayList<String>) restroom_document.get(FirestoreReferences.Restrooms.AMENITIES);

            if(amenities_ids == null || amenities_ids.isEmpty())
                return;

            Log.d("CreateEditRestroomActivity", "Got amenity ids");
            FirestoreHelper.getInstance().getAmenitiesDBRef().whereIn(FieldPath.documentId(), amenities_ids).get().addOnCompleteListener(task1 ->
            {
                if(!task1.isSuccessful())
                    return;

                QuerySnapshot amenity_documents = task1.getResult();

                if(amenity_documents == null || amenity_documents.isEmpty())
                    return;

                Log.d("CreateEditRestroomActivity", "Found amenities");
                for(DocumentSnapshot amenity_document : amenity_documents)
                {
                    for(AmenityData amenity : amenity_list)
                    {
                        if(amenity.getName().equals(amenity_document.getId()))
                        {
                            int amenity_index = amenity_list.indexOf(amenity);

                            Switch sw_amenity_description = (Switch) rv_restroom_amenities.getChildAt(amenity_index).findViewById(R.id.sw_amenity_description);

                            if(sw_amenity_description != null)
                                sw_amenity_description.setChecked(true);

                            Log.d("CreateEditRestroomActivity", "Toggled amenity");
                        }
                    }
                }
            });
        });
    }
    private void checkBuilding()
    {
        building_exists = false;
        //1. Check if the chosen building's location already exists
        FirestoreHelper.getInstance().readBuilding(building_latitude, building_longitude, task ->
        {
            if(task.isSuccessful())
            {
                Map<String, Object> old_building_data = task.getResult().getData();

                building_exists = old_building_data != null && !old_building_data.isEmpty();

                if(building_exists)
                {
                    //Save that information onto the restroom's building name
                    building_name = old_building_data.get(FirestoreReferences.Buildings.NAME).toString();

                    et_restroom_name.setText(building_name);
                    et_restroom_name.setFocusable(false);
                    et_restroom_name.setFocusableInTouchMode(false);
                    et_restroom_name.setCursorVisible(false); // Hide the cursor
                    et_restroom_name.setKeyListener(null);

                    Log.d("CreateEditRestroomActivity", "Building Exists");
                }
                else
                    takeBuildingPicture();
            }
            else
                takeBuildingPicture();
        });
    }

    private void takeBuildingPicture()
    {
        Log.d("CreateEditRestroomActivity", "Building NOT Exists");

        //Take a picture of the building
        Intent photo_intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        setResult(RESULT_OK, photo_intent);
        activity_result_launcher.launch(photo_intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        amenity_list = new ArrayList<AmenityData>();

        FirestoreHelper.getInstance().getAmenitiesDBRef().get().addOnCompleteListener(task ->
        {
            if(!task.isSuccessful())
                return;

            QuerySnapshot amenity_documents = task.getResult();

            if(amenity_documents == null || amenity_documents.isEmpty())
                return;

            int added_amenities = 0;
            for(DocumentSnapshot amenity_document : amenity_documents)
            {
                amenity_list.add
                (
                    new AmenityData
                    (
                            amenity_document.getId(),
                            amenity_document.getString(FirestoreReferences.Amenities.PICTURE)
                    )
                );
                added_amenities++;
                if(added_amenities == amenity_documents.size())
                {
                    amenity_adapter = new AmenityAdapter(amenity_list, CreateEditRestroomActivity.this);
                    rv_restroom_amenities.setAdapter(amenity_adapter);

                    if(restroom_id != null && !restroom_id.isEmpty())
                        fillRestroomInfo();
                }
                Log.d("CreateEditRestroomActivity", amenity_document.getId());
            }

        });

    }

    public void createBuildingButton()
    {
        if(building_exists)
            createUpdateRestroom();
        else
        {
            //The building DOES NOT EXIST, You need to insert a building entry first
            getAddressFromGeoPoint(address ->
            {
                building_name = et_restroom_name.getText().toString();

                Map<String, Object> new_building_data = FirestoreHelper.getInstance().createBuildingData
                (
                    building_latitude,
                    building_longitude,
                    building_name,
                    address,
                    PictureHelper.decodeBase64ToBitmap(building_picture),
                    suggestion
                );

                FirestoreHelper.getInstance().insertBuilding(building_latitude, building_longitude, new_building_data, task1 ->
                {
                    if(task1.isSuccessful())
                        createUpdateRestroom();
                });
            });
        }
    }

    public void createUpdateRestroom()
    {
        //In this point, the building for the restroom should already exist.

        String restroom_name = et_restroom_floor.getText().toString();
        int cleanliness = sb_cleanliness.getProgress();
        int maintenance = sb_maintenance.getProgress();
        int vacancy = sb_vacancy.getProgress();

        ArrayList<String> enabled_amenities = new ArrayList<String>();

        //Go through amenities to check which ones are switched on
        for (int i = 0; i < rv_restroom_amenities.getChildCount(); i++)
        {
            View itemView = rv_restroom_amenities.getChildAt(i);
            AmenityAdapter.AmenityHolder viewHolder = (AmenityAdapter.AmenityHolder) rv_restroom_amenities.getChildViewHolder(itemView);

            if(viewHolder.isSwitchOn())
                //Switch is on, get the name of the amenity
                enabled_amenities.add(viewHolder.getAmenityName());
        }

        Map<String, Object> data = FirestoreHelper.getInstance().createRestroomData
        (
            restroom_name,
            cleanliness,
            maintenance,
            vacancy,
            enabled_amenities
        );

        if(restroom_id == null || restroom_id.isEmpty()) {
            restroom_id = FirestoreHelper.getInstance().getRestroomsDBRef().document().getId();
            FirestoreHelper.getInstance().insertRestroom(restroom_id, data, task ->
            {
                if (task.isSuccessful())
                {
                    //Update Building with new RestroomData
                    FirestoreHelper.getInstance().appendStringToStringArray
                            (
                                    FirestoreReferences.Buildings.COLLECTION,
                                    MapHelper.getInstance().encodeBuildingID(building_latitude, building_longitude),
                                    FirestoreReferences.Buildings.RESTROOMS,
                                    restroom_id
                            );
                    goToHome();
                }
            });
        }
        else
        {
            //Update the restroom here
            FirestoreHelper.getInstance().updateRestroom(restroom_id, data, task ->
            {
                if(task.isSuccessful())
                    finish();
            });
        }
    }

    private void goToHome()
    {
        Intent intent = new Intent(CreateEditRestroomActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void getAddressFromGeoPoint(AddressCallback callback)
    {
        new Thread(() ->
        {
            try
            {
                List<Address> addresses = MapHelper.getInstance().getGeocoder().getFromLocation(building_latitude, building_longitude, 1);
                if (addresses != null && !addresses.isEmpty())
                {
                    Address address = addresses.get(0);

                    StringBuilder fullAddress = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        fullAddress.append(address.getAddressLine(i));
                        if (i < address.getMaxAddressLineIndex()) {
                            fullAddress.append(", "); // Add comma between lines
                        }
                    }

                    runOnUiThread(() -> callback.onAddressRetrieved(fullAddress.toString()));
                }
                else
                {
                    runOnUiThread(() -> callback.onAddressRetrieved(null)); // No address found
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                runOnUiThread(() -> callback.onAddressRetrieved(null)); // Error case
            }
        }).start();
    }

    public interface AddressCallback
    {
        void onAddressRetrieved(String address);
    }
}