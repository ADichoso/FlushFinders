package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.BuildingRestroomAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class ViewBuildingActivity extends AppCompatActivity {

    String building_id;
    GeoPoint building_location;
    ImageView iv_building;
    TextView tv_building_name;
    RecyclerView rv_building_restrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_building);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iv_building = findViewById(R.id.iv_building_pic);
        tv_building_name = findViewById(R.id.tv_building_name);

        rv_building_restrooms = findViewById(R.id.rv_building_restrooms);
        rv_building_restrooms.setHasFixedSize(true);
        rv_building_restrooms.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        building_id = intent.getStringExtra(MapHomeActivity.BUILDING_ID);

        building_location = MapHelper.getInstance().decodeBuildingLocation(building_id);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(building_location == null)
            return;

        //Get restroom information from the building

        ArrayList<RestroomData> restroomData = new ArrayList<RestroomData>();

        FirestoreHelper.getInstance().readBuilding(building_location.getLatitude(), building_location.getLongitude(), task ->
        {
            if(task.isSuccessful())
            {
                //Found the building, get the restrooms
                DocumentSnapshot building_document = task.getResult();

                iv_building.setImageBitmap(PictureHelper.decodeBase64ToBitmap(building_document.getString(FirestoreReferences.Buildings.BUILDING_PICTURE)));
                tv_building_name.setText(building_document.getString(FirestoreReferences.Buildings.NAME));

                ArrayList<String> restroom_ids = (ArrayList<String>) building_document.get(FirestoreReferences.Buildings.RESTROOMS);

                FirestoreHelper.getInstance().getRestroomsDBRef().whereIn(FieldPath.documentId(), restroom_ids)
                .get()
                .addOnCompleteListener(task1 ->
                {
                    if(task1.isSuccessful())
                    {

                        Log.d("ViewBuildingActivity", String.valueOf(task1.getResult().size()));
                        //Got the restrooms, add its info onto the Restroom Data
                        for(QueryDocumentSnapshot restroom_document : task1.getResult())
                        {
                            restroomData.add(new RestroomData(
                                    restroom_document.getId(),
                                    building_id,
                                    building_document.getString(FirestoreReferences.Buildings.BUILDING_PICTURE),
                                    building_document.getString(FirestoreReferences.Buildings.NAME),
                                    building_document.getString(FirestoreReferences.Buildings.ADDRESS),
                                    restroom_document.getString(FirestoreReferences.Restrooms.NAME),
                                    restroom_document.get(FirestoreReferences.Restrooms.CLEANLINESS, Integer.class),
                                    restroom_document.get(FirestoreReferences.Restrooms.MAINTENANCE, Integer.class),
                                    restroom_document.get(FirestoreReferences.Restrooms.VACANCY, Integer.class),
                                    new ArrayList<AmenityData>()
                            ));
                        }

                        BuildingRestroomAdapter buildingRestroomAdapter = new BuildingRestroomAdapter(restroomData, ViewBuildingActivity.this);

                        rv_building_restrooms.setAdapter(buildingRestroomAdapter);
                    }
                });
            }
        });
    }
}