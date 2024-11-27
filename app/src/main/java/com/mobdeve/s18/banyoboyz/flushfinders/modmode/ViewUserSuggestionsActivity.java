package com.mobdeve.s18.banyoboyz.flushfinders.modmode;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.SuggestedRestroomAdapter;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewUserSuggestionsActivity extends AppCompatActivity
{

    private RecyclerView rv_restroom_user_reports;
    private SuggestedRestroomAdapter suggested_restroom_adapter;
    private ArrayList<RestroomData> restroom_list;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_user_reports);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_restroom_user_reports = findViewById(R.id.rv_restroom_user_reports);
        rv_restroom_user_reports.setHasFixedSize(true);
        rv_restroom_user_reports.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        restroom_list = new ArrayList<RestroomData>();
        suggested_restroom_adapter = new SuggestedRestroomAdapter(restroom_list, ViewUserSuggestionsActivity.this);
        rv_restroom_user_reports.setAdapter(suggested_restroom_adapter);

        FirestoreHelper.getInstance().getBuildingsDBRef().whereEqualTo(FirestoreReferences.Buildings.SUGGESTION, true).get()
        .addOnCompleteListener(task ->
        {
            if(!task.isSuccessful())
                return;

            QuerySnapshot building_documents = task.getResult();

            if(building_documents == null || building_documents.isEmpty())
                return;

            Log.d("ViewUserSuggestionsActivity", "GOT BUILDINGS");
            for(DocumentSnapshot building_document : building_documents)
            {
                //Get the restrooms in here
                ArrayList<String> restrooms_ids = (ArrayList<String>) building_document.get(FirestoreReferences.Buildings.RESTROOMS);

                if(restrooms_ids == null || restrooms_ids.isEmpty())
                    continue;

                Log.d("ViewUserSuggestionsActivity", "GOT IDS");
                FirestoreHelper.getInstance().getRestroomsDBRef().whereIn(FieldPath.documentId(), restrooms_ids).get().addOnCompleteListener(task1 ->
                {
                    if(!task1.isSuccessful())
                        return;

                    QuerySnapshot restroom_documents = task1.getResult();

                    if(restroom_documents == null || restroom_documents.isEmpty())
                        return;

                    Log.d("ViewUserSuggestionsActivity", "GOT RESTROOMS");
                    for(DocumentSnapshot restroom_document : restroom_documents)
                    {
                        restroom_list.add(new RestroomData
                        (
                                restroom_document.getId(),
                                building_document.getId(),
                                building_document.getString(FirestoreReferences.Buildings.BUILDING_PICTURE),
                                building_document.getString(FirestoreReferences.Buildings.NAME),
                                building_document.getString(FirestoreReferences.Buildings.ADDRESS),
                                restroom_document.getString(FirestoreReferences.Restrooms.NAME),
                                restroom_document.get(FirestoreReferences.Restrooms.CLEANLINESS, Integer.class),
                                restroom_document.get(FirestoreReferences.Restrooms.MAINTENANCE, Integer.class),
                                restroom_document.get(FirestoreReferences.Restrooms.VACANCY, Integer.class),
                                new ArrayList<AmenityData>()
                        ));
                        Log.d("ViewUserSuggestionsActivity", "ADDED NEW REVIEW");
                        suggested_restroom_adapter.notifyItemInserted(restroom_list.size() - 1);
                    }
                });
            }
        });
    }

    public void deleteUserSuggestion(String building_id)
    {
        //Delete the building with the id (DELETE RESTROOMS ASSOCIATED WITH THIS BUILDING FIRST)
        GeoPoint point = MapHelper.getInstance().decodeBuildingLocation(building_id);
        FirestoreHelper.getInstance().readBuilding(point.getLatitude(), point.getLongitude(), task ->
        {
            if(!task.isSuccessful())
                return;

            DocumentSnapshot building_document = task.getResult();

            if(building_document == null || !building_document.exists())
                return;

            //Get the restrooms
            ArrayList<String> restroom_ids = (ArrayList<String>) building_document.get(FirestoreReferences.Buildings.RESTROOMS);

            if(restroom_ids == null || restroom_ids.isEmpty())
                return;

            AtomicInteger deleted_restrooms = new AtomicInteger();
            for(String restroom_id :restroom_ids)
            {
                FirestoreHelper.getInstance().deleteRestroom(restroom_id, task1 -> {
                    if(task1.isSuccessful())
                        deleted_restrooms.getAndIncrement();
                });
            }

            FirestoreHelper.getInstance().deleteBuilding(point.getLatitude(), point.getLongitude(), task2 -> {
                if(!task2.isSuccessful())
                    return;

                suggested_restroom_adapter.removeAllRestroomsWithIDs(restroom_ids);
                suggested_restroom_adapter.notifyDataSetChanged();
            });
        });
    }
}