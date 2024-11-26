package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;

import org.osmdroid.util.GeoPoint;

import java.util.Map;

public class ReviewReportRestroomActivity extends AppCompatActivity
{

    private String building_id;
    private String restroom_id;

    private TextView tv_restroom_name;
    private TextView tv_restroom_floor;
    private SeekBar sb_cleanliness;
    private SeekBar sb_maintenance;
    private SeekBar sb_vacancy;
    private RatingBar rb_restroom_rating;
    private EditText et_restroom_review_report;

    private SharedPreferences sharedpreferences;
    private String account_email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_report_restroom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        building_id = intent.getStringExtra(MapHomeActivity.BUILDING_ID);
        restroom_id = intent.getStringExtra(MapHomeActivity.RESTROOM_ID);

        tv_restroom_name = findViewById(R.id.tv_restroom_name);
        tv_restroom_floor = findViewById(R.id.tv_restroom_floor);
        sb_cleanliness = findViewById(R.id.sb_cleanliness);
        sb_maintenance = findViewById(R.id.sb_maintenance);
        sb_vacancy = findViewById(R.id.sb_vacancy);
        rb_restroom_rating = findViewById(R.id.rb_restroom_rating);
        et_restroom_review_report = findViewById(R.id.et_restroom_review_report);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        //Shared Preferences
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");

        //Update the Text views to show the building name and restroom name

        GeoPoint building_location = MapHelper.getInstance().decodeBuildingLocation(building_id);
        FirestoreHelper.getInstance().readBuilding(building_location.getLatitude(), building_location.getLongitude(), task ->
        {
            if(!task.isSuccessful())
                return;

            DocumentSnapshot building_document = task.getResult();

            if(building_document != null && building_document.exists())
                tv_restroom_name.setText(building_document.getString(FirestoreReferences.Buildings.NAME));
        });

        FirestoreHelper.getInstance().readRestroom(restroom_id, task ->
        {
            if(!task.isSuccessful())
                return;

            DocumentSnapshot restroom_document = task.getResult();

            if(restroom_document != null && restroom_document.exists())
                tv_restroom_floor.setText(restroom_document.getString(FirestoreReferences.Restrooms.NAME));
        });
    }

    public void submitReviewButton(View view)
    {
        if(building_id.isEmpty() || restroom_id.isEmpty() || account_email.isEmpty())
            return;

        //1. Create a new review data object
        float rating = rb_restroom_rating.getRating();
        String report = et_restroom_review_report.getText().toString();
        int cleanliness = sb_cleanliness.getProgress();
        int maintenance = sb_maintenance.getProgress();
        int vacancy = sb_vacancy.getProgress();

        Map<String, Object> review_data = FirestoreHelper.getInstance().createReviewData(
            restroom_id,
            account_email,
            rating,
            report,
            cleanliness,
            maintenance,
            vacancy
        );

        String review_id = FirestoreHelper.getInstance().getReviewsDBRef().document().getId();

        FirestoreHelper.getInstance().insertReview(review_id, review_data, task ->
        {
            if(task.isSuccessful())
                finish();
        });
    }
}