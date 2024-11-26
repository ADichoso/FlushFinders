package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.DeleteRestroomAdapter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DeleteRestroomActivity extends AppCompatActivity {

    RecyclerView rv_restrooms;
    EditText et_search_restroom_name;

    private DeleteRestroomAdapter deleteRestroomAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_restrooms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        et_search_restroom_name = findViewById(R.id.et_search_restroom_name);
        rv_restrooms = findViewById(R.id.rv_restrooms);
        rv_restrooms.setHasFixedSize(true);
        rv_restrooms.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        ArrayList<RestroomData> restroomData = new ArrayList<RestroomData>();

        FirestoreHelper.getInstance().getBuildingsDBRef().get()
        .addOnCompleteListener(task ->
        {
            //Get all the buildings
            if(!task.isSuccessful())
                return;

            //Got the restrooms, add it to the restroomdata
            QuerySnapshot building_documents = task.getResult();

            if(building_documents == null || building_documents.isEmpty())
                return;

            AtomicInteger buildings = new AtomicInteger();
            for(DocumentSnapshot building_document : building_documents)
            {
                ArrayList<String> restrooms_ids = (ArrayList<String>) building_document.get(FirestoreReferences.Buildings.RESTROOMS);

                if(restrooms_ids == null || !restrooms_ids.isEmpty())
                    continue;

                for(String restroom_id : restrooms_ids)
                {
                    FirestoreHelper.getInstance().readRestroom(restroom_id, task1 ->
                    {
                        if(!task1.isSuccessful())
                            return;

                        DocumentSnapshot restroom_document = task1.getResult();

                        if(restroom_document == null || !restroom_document.exists())
                            return;

                        restroomData.add(new RestroomData
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
                        buildings.getAndIncrement();
                    });
                }
            }

            if(buildings.get() == building_documents.size())
            {
                deleteRestroomAdapter = new DeleteRestroomAdapter(restroomData, DeleteRestroomActivity.this);
                rv_restrooms.setAdapter(deleteRestroomAdapter);
            }
        });
    }

    public void searchRestroomsButton(View view)
    {
        String search_name = et_search_restroom_name.getText().toString();
        ArrayList<RestroomData> restroomData = new ArrayList<RestroomData>();

        FirestoreHelper.getInstance().getBuildingsDBRef().get()
        .addOnCompleteListener(task ->
        {
            //Get all the buildings
            if(!task.isSuccessful())
                return;

            //Got the restrooms, add it to the restroomdata
            QuerySnapshot building_documents = task.getResult();

            if(building_documents == null || building_documents.isEmpty())
                return;

            AtomicInteger buildings = new AtomicInteger();
            for(DocumentSnapshot building_document : building_documents)
            {
                String building_name = building_document.getString(FirestoreReferences.Buildings.NAME);
                ArrayList<String> restrooms_ids = (ArrayList<String>) building_document.get(FirestoreReferences.Buildings.RESTROOMS);

                if(restrooms_ids == null || !restrooms_ids.isEmpty())
                    continue;

                for(String restroom_id : restrooms_ids)
                {

                    FirestoreHelper.getInstance().readRestroom(restroom_id, task1 ->
                    {
                        if(!task1.isSuccessful())
                            return;

                        DocumentSnapshot restroom_document = task1.getResult();

                        if(restroom_document == null || !restroom_document.exists())
                            return;

                        String restroom_name = restroom_document.getString(FirestoreReferences.Restrooms.NAME);

                        if(!search_name.isEmpty() && !building_name.contains(search_name) && !restroom_name.contains(search_name))
                            return;

                        restroomData.add(new RestroomData
                                (
                                        restroom_document.getId(),
                                        building_document.getId(),
                                        building_document.getString(FirestoreReferences.Buildings.BUILDING_PICTURE),
                                        building_name,
                                        building_document.getString(FirestoreReferences.Buildings.ADDRESS),
                                        restroom_name,
                                        restroom_document.get(FirestoreReferences.Restrooms.CLEANLINESS, Integer.class),
                                        restroom_document.get(FirestoreReferences.Restrooms.MAINTENANCE, Integer.class),
                                        restroom_document.get(FirestoreReferences.Restrooms.VACANCY, Integer.class),
                                        new ArrayList<AmenityData>()
                                ));

                        buildings.getAndIncrement();
                    });
                }
            }

            if(buildings.get() == building_documents.size())
            {
                deleteRestroomAdapter = new DeleteRestroomAdapter(restroomData, DeleteRestroomActivity.this);
                rv_restrooms.setAdapter(deleteRestroomAdapter);
            }
        });
    }

    public void deleteRestroomButton(View view)
    {
        for(String restroom_id : deleteRestroomAdapter.getSelectedRestroomsIds())
        {
                FirestoreHelper.getInstance().deleteRestroom(restroom_id, task -> {
                    if(!task.isSuccessful())
                        return;

                    //Delete any associated reviews for the restroom
                    FirestoreHelper.getInstance().getReviewsDBRef().whereEqualTo(FirestoreReferences.Reviews.RESTROOM, restroom_id).get()
                    .addOnCompleteListener(task1 -> {
                        if(!task1.isSuccessful())
                            return;

                        QuerySnapshot review_documents = task1.getResult();

                        if(review_documents == null || review_documents.isEmpty())
                            return;

                        for(DocumentSnapshot review_document: review_documents)
                        {
                            String review_id = review_document.getId();
                            FirestoreHelper.getInstance().deleteReview(review_id, task2 -> {
                                if(!task2.isSuccessful())
                                    return;
                            });
                        }
                    });
            });
        }
    }

    public void removeSelectedRestrooms(View view)
    {
        deleteRestroomAdapter.clearSelectedHolders();
    }
}