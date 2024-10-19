package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.data.AmenitiesAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.data.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.CreateEditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ModHomeActivity;

public class SuggestRestroomDetailsActivity extends AppCompatActivity {

    RecyclerView rv_restroom_amenities;
    Button btn_submit_restroom_info;
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

        AmenitiesAdapter amenitiesAdapter = new AmenitiesAdapter(amenityData, SuggestRestroomDetailsActivity.this);
        rv_restroom_amenities.setAdapter(amenitiesAdapter);

        btn_submit_restroom_info = findViewById(R.id.btn_submit_restroom_info);
        btn_submit_restroom_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestRestroomButton(v);
            }
        });
    }

    public void suggestRestroomButton(View view)
    {
        Intent intent = new Intent(SuggestRestroomDetailsActivity.this, MapHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}