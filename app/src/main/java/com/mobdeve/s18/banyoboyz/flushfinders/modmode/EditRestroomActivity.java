package com.mobdeve.s18.banyoboyz.flushfinders.modmode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.EditRestroomAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class EditRestroomActivity extends AppCompatActivity {

    private RecyclerView rv_restrooms;
    private EditText et_search_restroom_name;

    private ArrayList<RestroomData> restroom_list;
    private EditRestroomAdapter edit_restroom_adapter;

    public ActivityResultLauncher<Intent> activity_result_launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
            {
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() != RESULT_OK)
                        return;

                    Intent result_intent = result.getData();

                    Double building_latitude = result_intent.getDoubleExtra(SuggestRestroomLocationActivity.LATITUDE, Double.NaN);
                    Double building_longitude = result_intent.getDoubleExtra(SuggestRestroomLocationActivity.LONGITUDE, Double.NaN);
                    String restroom_id = result_intent.getStringExtra(EditRestroomAdapter.RESTROOM_ID);

                    for(int i = 0; i < restroom_list.size(); i++)
                    {
                        if(!restroom_list.get(i).getId().equals(restroom_id))
                            return;

                        int finalI = i;

                        FirestoreHelper.getInstance().readBuilding(building_latitude, building_longitude, task ->
                        {
                            if(!task.isSuccessful())
                                return;

                            DocumentSnapshot building_document = task.getResult();


                            if(building_document == null || !building_document.exists())
                                return;

                            FirestoreHelper.getInstance().readRestroom(restroom_id, task1 ->
                            {
                                if(!task1.isSuccessful())
                                    return;

                                DocumentSnapshot restroom_document = task.getResult();

                                if(restroom_document == null || !restroom_document.exists())
                                    return;

                                ArrayList<String> amenities_ids = (ArrayList<String>) restroom_document.get(FirestoreReferences.Restrooms.AMENITIES);

                                FirestoreHelper.getInstance().getAmenitiesDBRef().whereIn(FieldPath.documentId(), amenities_ids)
                                .get()
                                .addOnCompleteListener(task2 ->
                                {
                                    //Got amenities
                                    if(!task2.isSuccessful())
                                        return;

                                    QuerySnapshot amenity_documents = task2.getResult();

                                    if(amenity_documents == null || amenity_documents.isEmpty())
                                        return;

                                    ArrayList<AmenityData> amenityData = new ArrayList<AmenityData>();

                                    for(QueryDocumentSnapshot amenity_document : amenity_documents)
                                    {
                                        amenityData.add(new AmenityData
                                                (
                                                        amenity_document.getId(),
                                                        amenity_document.getString(FirestoreReferences.Amenities.PICTURE)
                                                )
                                        );
                                    }

                                    restroom_list.set(finalI, new RestroomData(
                                        restroom_id,
                                        building_document.getId(),
                                        building_document.getString(FirestoreReferences.Buildings.BUILDING_PICTURE),
                                        building_document.getString(FirestoreReferences.Buildings.NAME),
                                        building_document.getString(FirestoreReferences.Buildings.ADDRESS),
                                        restroom_document.getString(FirestoreReferences.Restrooms.NAME),
                                        restroom_document.get(FirestoreReferences.Restrooms.CLEANLINESS, Integer.class),
                                        restroom_document.get(FirestoreReferences.Restrooms.MAINTENANCE, Integer.class),
                                        restroom_document.get(FirestoreReferences.Restrooms.VACANCY, Integer.class),
                                        amenityData));

                                    edit_restroom_adapter.notifyItemChanged(finalI);
                                });
                            });
                        });


                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_restroom);
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
    protected void onStart()
    {
        super.onStart();

        restroom_list = new ArrayList<RestroomData>();

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

            if(buildings.get() == building_documents.size())
            {
                edit_restroom_adapter = new EditRestroomAdapter(restroom_list, this);
                rv_restrooms.setAdapter(edit_restroom_adapter);
            }
        });
    }

    public void searchRestroomsButton(View view)
    {
        String search_name = et_search_restroom_name.getText().toString();
        restroom_list.clear();

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

                        restroom_list.add(new RestroomData
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
                edit_restroom_adapter = new EditRestroomAdapter(restroom_list, this);
                rv_restrooms.setAdapter(edit_restroom_adapter);
            }
        });
    }
}