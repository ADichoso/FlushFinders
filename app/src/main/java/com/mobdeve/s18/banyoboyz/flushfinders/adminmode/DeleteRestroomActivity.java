package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DeleteRestroomActivity extends AppCompatActivity
{

    private RecyclerView rv_restrooms;
    private EditText et_search_restroom_name;

    private ArrayList<RestroomData> restroom_list;
    private DeleteRestroomAdapter delete_restroom_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_restrooms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Get Views
        et_search_restroom_name = findViewById(R.id.et_search_restroom_name);
        rv_restrooms = findViewById(R.id.rv_restrooms);
        rv_restrooms.setHasFixedSize(true);
        rv_restrooms.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //Populate the Recycler View with restroom data
        restroom_list = new ArrayList<RestroomData>();

        //1. Get all actively used buildings.
        FirestoreHelper.getInstance().getBuildingsDBRef().whereEqualTo(FirestoreReferences.Buildings.SUGGESTION, false).get()
        .addOnCompleteListener(task ->
        {
            if(!task.isSuccessful())
                return;

            QuerySnapshot building_documents = task.getResult();

            if(building_documents == null || building_documents.isEmpty())
                return;

            //2. For every building, get the restrooms.
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

                        //Create restroom object from database info
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
                        buildings.getAndIncrement();
                    });
                }
            }

            //3. Got all restrooms. Create RecyclerView
            if(buildings.get() == building_documents.size())
            {
                delete_restroom_adapter = new DeleteRestroomAdapter(restroom_list, DeleteRestroomActivity.this);
                rv_restrooms.setAdapter(delete_restroom_adapter);
            }
        });
    }

    public void searchRestroomsButton(View view)
    {
        String search_name = et_search_restroom_name.getText().toString();
        ArrayList<RestroomData> filteredRestroomData = new ArrayList<RestroomData>();

        //1. Go through every restroom
        if(search_name.isEmpty())
            filteredRestroomData = (ArrayList<RestroomData>) restroom_list.clone();
        else
        {
            for(RestroomData restroom: restroom_list)
            {
                if(restroom.getBuildingName().contains(search_name) || restroom.getName().contains(search_name))
                    filteredRestroomData.add(restroom);
            }
        }

        //2. Update the data of the restroom adapter
        delete_restroom_adapter = new DeleteRestroomAdapter(filteredRestroomData, DeleteRestroomActivity.this);
        rv_restrooms.setAdapter(delete_restroom_adapter);
    }

    public void deleteRestroomButton(View view)
    {
        AtomicInteger deleted_restrooms = new AtomicInteger();
        for(RestroomData restroom : delete_restroom_adapter.getRestroomData())
        {
            FirestoreHelper.getInstance().deleteRestroom(restroom.getId(), task ->
            {
                if(!task.isSuccessful())
                    return;

                AtomicInteger deleted_reviews = new AtomicInteger();
                //Delete any associated reviews for the restroom
                FirestoreHelper.getInstance().getReviewsDBRef().whereEqualTo(FirestoreReferences.Reviews.RESTROOM, restroom.getId()).get()
                .addOnCompleteListener(task1 ->
                {
                    if(!task1.isSuccessful())
                        return;

                    QuerySnapshot review_documents = task1.getResult();

                    if(review_documents == null || review_documents.isEmpty())
                        return;

                    for(DocumentSnapshot review_document: review_documents)
                    {
                        String review_id = review_document.getId();
                        FirestoreHelper.getInstance().deleteReview(review_id, task2 ->
                        {
                            deleted_reviews.getAndIncrement();
                        });
                    }

                    if(deleted_reviews.get() == review_documents.size())
                    {
                        restroom_list.remove(restroom);
                        deleted_restrooms.getAndIncrement();
                    }
                });
            });
        }

        if(deleted_restrooms.get() == delete_restroom_adapter.getRestroomData().size())
        {
            delete_restroom_adapter = new DeleteRestroomAdapter(restroom_list, DeleteRestroomActivity.this);
            rv_restrooms.setAdapter(delete_restroom_adapter);
        }
    }

    public void removeSelectedRestrooms(View view)
    {
        delete_restroom_adapter.clearSelectedHolders();
    }
}