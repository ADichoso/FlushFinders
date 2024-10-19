package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.accounts.AccountHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;

public class MapHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void recommendedRestroomsButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SavedBuildingsActivity.class);

        startActivity(intent);
    }

    public void searchRestroomButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SearchBuildingActivity.class);

        startActivity(intent);
    }

    public void accountHomeButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, AccountHomeActivity.class);

        startActivity(intent);
    }

    public void rateRestroomsButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, ReviewReportRestroomActivity.class);

        startActivity(intent);
    }

    public void suggestRestroomButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, SuggestRestroomLocationActivity.class);

        intent.putExtra("CALLER", "USER");
        startActivity(intent);
    }

    public void viewBuildingButton(View view)
    {
        Intent intent = new Intent(MapHomeActivity.this, ViewBuildingActivity.class);

        startActivity(intent);
    }
}