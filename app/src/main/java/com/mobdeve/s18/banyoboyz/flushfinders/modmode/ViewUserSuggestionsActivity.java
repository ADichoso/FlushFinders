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

public class ViewUserSuggestionsActivity extends AppCompatActivity {

    RecyclerView rv_restroom_user_reports;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_user_reports);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_restroom_user_reports = findViewById(R.id.rv_restroom_user_reports);
        rv_restroom_user_reports.setHasFixedSize(true);
        rv_restroom_user_reports.setLayoutManager(new LinearLayoutManager(this));

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

        BuildingRestroomAdapter buildingRestroomAdapter = new BuildingRestroomAdapter(restroomData, ViewUserSuggestionsActivity.this);
        rv_restroom_user_reports.setAdapter(buildingRestroomAdapter);
    }
}