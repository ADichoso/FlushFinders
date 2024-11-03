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
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.RestroomReviewReportAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.RestroomReviewReportData;

public class ReviewUserReportsActivity extends AppCompatActivity {

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

        RestroomReviewReportData[] restroomReviewReportData = new RestroomReviewReportData[]{
                new RestroomReviewReportData(R.drawable.goks, 5.0f, "Very cool restroom, very chill", "Gokongwei Hall", "1st Floor", 20, 50, 80),
                new RestroomReviewReportData(R.drawable.goks,5.0f, "Very cool restroom, very chill", "Gokongwei Hall", "2nd Floor", 10, 60, 80),
                new RestroomReviewReportData(R.drawable.goks,5.0f, "Very cool restroom, very chill", "Gokongwei Hall", "3rd Floor", 30, 60, 40),
                new RestroomReviewReportData(R.drawable.goks, 5.0f, "Very cool restroom, very chill","Gokongwei Hall", "44th Floor", 50, 60, 90)
        };

        RestroomReviewReportAdapter restroomReviewReportAdapter = new RestroomReviewReportAdapter(restroomReviewReportData, ReviewUserReportsActivity.this);
        rv_restroom_user_reports.setAdapter(restroomReviewReportAdapter);
    }
}