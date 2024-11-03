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

public class SavedBuildingsActivity extends AppCompatActivity {

    RecyclerView rv_buildings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_buildings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_buildings = findViewById(R.id.rv_buildings);
        rv_buildings.setHasFixedSize(true);
        rv_buildings.setLayoutManager(new LinearLayoutManager(this));

        BuildingData[] buildingData = new BuildingData[]{
                new BuildingData(0, "Gokongwei Hall", "De La Salle University - Taft Avenue", "12 Minutes", R.drawable.goks, new RestroomData[]{}),
                new BuildingData(1, "Gokongwei Hall2", "De La Salle University - Taft Avenue", "12 Minutes", R.drawable.goks, new RestroomData[]{}),
                new BuildingData(2, "Gokongwei Hall3", "De La Salle University - Taft Avenue", "12 Minutes", R.drawable.goks, new RestroomData[]{})
        };

        BuildingAdapter buildingAdapter = new BuildingAdapter(buildingData, SavedBuildingsActivity.this);
        rv_buildings.setAdapter(buildingAdapter);
    }
}