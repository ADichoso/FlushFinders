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
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.BuildingAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.BuildingData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;

public class SearchBuildingActivity extends AppCompatActivity {

    RecyclerView rv_buildings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_building);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_buildings = findViewById(R.id.rv_buildings);
        rv_buildings.setHasFixedSize(true);
        rv_buildings.setLayoutManager(new LinearLayoutManager(this));

        BuildingData[] buildingData = new BuildingData[]{};

        BuildingAdapter buildingAdapter = new BuildingAdapter(buildingData, SearchBuildingActivity.this);
        rv_buildings.setAdapter(buildingAdapter);
    }
}