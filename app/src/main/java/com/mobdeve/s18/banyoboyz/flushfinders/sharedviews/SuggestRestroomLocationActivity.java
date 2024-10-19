package com.mobdeve.s18.banyoboyz.flushfinders.sharedviews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.CreateEditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SuggestRestroomDetailsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewBuildingActivity;

public class SuggestRestroomLocationActivity extends AppCompatActivity {

    Button btn_submit_restroom_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_restroom_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_submit_restroom_location = findViewById(R.id.btn_submit_restroom_location);

        Intent intent = getIntent();
        String caller = intent.getStringExtra("CALLER");

        btn_submit_restroom_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuggestRestroomLocationActivity.this, ViewBuildingActivity.class);

                if(caller.equals("MOD_ADMIN"))
                    i = new Intent(SuggestRestroomLocationActivity.this, CreateEditRestroomActivity.class);

                startActivity(i);
            }
        });
    }
}