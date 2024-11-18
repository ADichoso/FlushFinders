package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.AmenitiesAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.BuildingRestroomAdapter;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class ViewRestroomActivity extends AppCompatActivity {
    public static final String BUILDING_ID = "BUILDING_ID";

    TextView tv_restroom_building;
    TextView tv_restroom_floor;
    ProgressBar pb_cleanliness;
    ProgressBar pb_maintenance;
    ProgressBar pb_vacancy;
    RecyclerView rv_restroom_amenities;

    private String building_id;
    private String restroom_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_restroom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_restroom_building = findViewById(R.id.tv_restroom_building);
        tv_restroom_floor = findViewById(R.id.tv_restroom_floor);
        pb_cleanliness = findViewById(R.id.pb_cleanliness);
        pb_maintenance = findViewById(R.id.pb_maintenance);
        pb_vacancy = findViewById(R.id.pb_vacancy);

        rv_restroom_amenities = findViewById(R.id.rv_restroom_amenities);
        rv_restroom_amenities.setHasFixedSize(true);
        rv_restroom_amenities.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        building_id = intent.getStringExtra(BuildingRestroomAdapter.BUILDING_ID);
        restroom_id = intent.getStringExtra(BuildingRestroomAdapter.RESTROOM_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(restroom_id.isEmpty() || building_id.isEmpty())
            return;

        GeoPoint building_location = MapHelper.getInstance().decodeBuildingLocation(building_id);
        FirestoreHelper.getInstance().readBuilding(building_location.getLatitude(), building_location.getLongitude(), task -> {
            if(task.isSuccessful() && task.getResult().exists())
            {
                tv_restroom_building.setText(task.getResult().getString(FirestoreReferences.Buildings.NAME));
            }
        });

        FirestoreHelper.getInstance().readRestroom(restroom_id, task ->
        {
            if(task.isSuccessful())
            {
                //Found the building, get the restrooms
                DocumentSnapshot restroom_document = task.getResult();

                tv_restroom_floor.setText(restroom_document.getString(FirestoreReferences.Restrooms.NAME));
                pb_cleanliness.setProgress(restroom_document.get(FirestoreReferences.Restrooms.CLEANLINESS, Integer.class));
                pb_maintenance.setProgress(restroom_document.get(FirestoreReferences.Restrooms.MAINTENANCE, Integer.class));
                pb_vacancy.setProgress(restroom_document.get(FirestoreReferences.Restrooms.VACANCY, Integer.class));

                ArrayList<String> amenities_ids = (ArrayList<String>) restroom_document.get(FirestoreReferences.Restrooms.AMENITIES);

                if(amenities_ids == null || amenities_ids.isEmpty())
                {
                    ArrayList<AmenityData> amenityData = new ArrayList<AmenityData>();

                    AmenitiesAdapter amenitiesAdapter = new AmenitiesAdapter(amenityData, ViewRestroomActivity.this);
                    rv_restroom_amenities.setAdapter(amenitiesAdapter);
                }
                else
                {
                    FirestoreHelper.getInstance().getAmenitiesDBRef().whereIn(FieldPath.documentId(), amenities_ids)
                    .get()
                    .addOnCompleteListener(task2 ->
                    {
                        //Got amenities
                        if(task2.isSuccessful())
                        {
                            ArrayList<AmenityData> amenityData = new ArrayList<AmenityData>();

                            for(QueryDocumentSnapshot amenity_document : task2.getResult())
                            {
                                amenityData.add(new AmenityData
                                    (
                                        amenity_document.getId(),
                                        amenity_document.getString(FirestoreReferences.Amenities.PICTURE)
                                    )
                                );
                            }

                            AmenitiesAdapter amenitiesAdapter = new AmenitiesAdapter(amenityData, ViewRestroomActivity.this);
                            rv_restroom_amenities.setAdapter(amenitiesAdapter);
                        }
                    });
                }
            }
        });
    }

    public void getRestroomDirectionsButton(View view)
    {
        Intent intent = new Intent(ViewRestroomActivity.this, MapHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BUILDING_ID, building_id);
        startActivity(intent);
        finish();
    }
}