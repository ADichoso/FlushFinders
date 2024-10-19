package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.DeleteRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.data.AmenitiesAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.data.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.data.BuildingRestroomAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.data.RestroomData;

public class ViewRestroomActivity extends AppCompatActivity {

    RecyclerView rv_restroom_amenities;
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

        rv_restroom_amenities = findViewById(R.id.rv_restroom_amenities);
        rv_restroom_amenities.setHasFixedSize(true);
        rv_restroom_amenities.setLayoutManager(new LinearLayoutManager(this));

        AmenityData[] amenityData = new AmenityData[]{
                new AmenityData("For Males", R.drawable.male),
                new AmenityData("For Females", R.drawable.female),
                new AmenityData("Bidet", R.drawable.bidet),
                new AmenityData("Footwash", R.drawable.footwash),
                new AmenityData("Masks", R.drawable.mask),
                new AmenityData("Sanitizer", R.drawable.sanitizer)
        };

        AmenitiesAdapter amenitiesAdapter = new AmenitiesAdapter(amenityData, ViewRestroomActivity.this);
        rv_restroom_amenities.setAdapter(amenitiesAdapter);
    }
}