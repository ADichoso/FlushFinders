package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.DeleteRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.data.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.data.BuildingRestroomAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.data.RestroomData;

public class ViewBuildingActivity extends AppCompatActivity {


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

        rv_building_restrooms = findViewById(R.id.rv_building_restrooms);
        rv_building_restrooms.setHasFixedSize(true);
        rv_building_restrooms.setLayoutManager(new LinearLayoutManager(this));

        AmenityData[] amenityData = new AmenityData[]{
                new AmenityData("For Males", R.drawable.male),
                new AmenityData("For Females", R.drawable.female),
                new AmenityData("Bidet", R.drawable.bidet),
                new AmenityData("Footwash", R.drawable.footwash),
                new AmenityData("Masks", R.drawable.mask),
                new AmenityData("Sanitizer", R.drawable.sanitizer)
        };
        RestroomData[] restroomData = new RestroomData[]{
                new RestroomData(R.drawable.goks, "Gokongwei Hall", "DLSU - Taft Avenue","1st Floor", 20, 50, 80, amenityData),
                new RestroomData(R.drawable.goks, "Gokongwei Hall", "DLSU - Taft Avenue","2nd Floor", 10, 60, 80, amenityData),
                new RestroomData(R.drawable.goks, "Gokongwei Hall", "DLSU - Taft Avenue","3rd Floor", 30, 60, 40, amenityData),
                new RestroomData(R.drawable.goks, "Gokongwei Hall", "DLSU - Taft Avenue","44th Floor", 50, 60, 90, amenityData)
        };

        BuildingRestroomAdapter buildingRestroomAdapter = new BuildingRestroomAdapter(restroomData, ViewBuildingActivity.this);
        rv_building_restrooms.setAdapter(buildingRestroomAdapter);
    }

    public void getBuildingDirectionsButton(View view)
    {
        Intent intent = new Intent(ViewBuildingActivity.this, MapHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}