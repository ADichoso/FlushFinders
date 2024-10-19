package com.mobdeve.s18.banyoboyz.flushfinders.modmode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.data.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.data.BuildingRestroomAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.data.RestroomData;

public class EditRestroomActivity extends AppCompatActivity {

    RecyclerView rv_restrooms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_restroom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_restrooms = findViewById(R.id.rv_restrooms);
        rv_restrooms.setHasFixedSize(true);
        rv_restrooms.setLayoutManager(new LinearLayoutManager(this));

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

        BuildingRestroomAdapter buildingRestroomAdapter = new BuildingRestroomAdapter(restroomData, EditRestroomActivity.this);
        rv_restrooms.setAdapter(buildingRestroomAdapter);
    }
}