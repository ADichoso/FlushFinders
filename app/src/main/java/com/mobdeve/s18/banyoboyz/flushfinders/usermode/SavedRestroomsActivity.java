package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.BuildingRestroomAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.SavedRestroomAdapter;

import org.osmdroid.util.BoundingBox;

import java.util.ArrayList;
import java.util.Collections;

public class SavedRestroomsActivity extends AppCompatActivity {
    private static final String SAVED = "Saved Restrooms";
    private static final String TOP = "Top Restrooms Near You";
    private static final String SIMILAR = "Similar Restrooms Near You";


    private double latitude;
    private double longitude;
    private String current_state;

    SharedPreferences sharedpreferences;
    String account_email;
    ArrayList<RestroomData> restroomData;
    SavedRestroomAdapter savedRestroomAdapter;
    RecyclerView rv_recommended_restrooms;
    Spinner sp_restroom_filters;

    BoundingBox boundingBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_restrooms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        current_state = SAVED;

        sp_restroom_filters = findViewById(R.id.sp_restroom_filters);
        rv_recommended_restrooms = findViewById(R.id.rv_recommended_restrooms);
        rv_recommended_restrooms.setHasFixedSize(true);
        rv_recommended_restrooms.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();

        latitude = intent.getDoubleExtra(MapHomeActivity.LATITUDE, 0.0);
        longitude = intent.getDoubleExtra(MapHomeActivity.LONGITUDE, 0.0);

        initializeBoundaries();
    }

    private static final double EARTH_RADIUS = 6371000; // Earth's radius in meters
    private static final double SEARCH_RADIUS = 250; //Search radius in meters
    private void initializeBoundaries()
    {
        double latRadians = Math.toRadians(latitude);
        double lonRadians = Math.toRadians(longitude);

        double angularDistance = SEARCH_RADIUS / EARTH_RADIUS;

        double min_lat = Math.toDegrees(latRadians - angularDistance);
        double max_lat = Math.toDegrees(latRadians + angularDistance);
        double min_lon = Math.toDegrees(lonRadians - angularDistance / Math.cos(latRadians));
        double max_lon = Math.toDegrees(lonRadians + angularDistance / Math.cos(latRadians));

        boundingBox = new BoundingBox(max_lat, max_lon, min_lat, min_lon);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Shared Preferences
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");


        //Set spinner options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.restroom_filters_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_restroom_filters.setAdapter(adapter);

        restroomData = new ArrayList<RestroomData>();
        savedRestroomAdapter = new SavedRestroomAdapter(restroomData, this);
        rv_recommended_restrooms.setAdapter(savedRestroomAdapter);

        sp_restroom_filters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                //Change the contents of the buildingData arraylist depending on the chosen state of the user.

                //1. Get selected state
                current_state = adapterView.getItemAtPosition(i).toString();
                restroomData.clear();

                if(current_state.equals(SAVED))
                {
                    getSavedRestrooms();
                }
                else if(current_state.equals(TOP))
                {
                    getRestroomsByTop();
                }
                else if(current_state.equals(SIMILAR))
                {
                    getRestroomsBySimilarity();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                //NOTHING.
            }
        });

        sp_restroom_filters.setSelection(0);
    }
    
    private void getSavedRestrooms()
    {
        Log.d("SavedRestroomsActivity", "Getting Saved Restrooms");
        //2.a. get the saved restrooms by the user (Shared preferences)
        FirestoreHelper.getInstance().readAccount(account_email, task -> {
            if(task.isSuccessful())
            {
                DocumentSnapshot account_document = task.getResult();
        
                ArrayList<String> saved_restrooms_ids = (ArrayList<String>) account_document.get(FirestoreReferences.Accounts.FAVORITE_RESTROOMS);
        
                if(saved_restrooms_ids != null && !saved_restrooms_ids.isEmpty())
                {
                    Log.d("SavedRestroomsActivity", "Found Saved Restrooms");
                    FirestoreHelper.getInstance().getRestroomsDBRef().whereIn(FieldPath.documentId(), saved_restrooms_ids).get()
                    .addOnCompleteListener(task1 ->
                    {
                        if(task1.isSuccessful())
                        {
                            //Obtained the restrooms here, construct the array list
                            for(QueryDocumentSnapshot restroom_document : task1.getResult())
                            {
                                Log.d("SavedRestroomsActivity", "Getting Saved Restrooms' Buildings");

                                //3.a. Get the building associated with this restroom
                                FirestoreHelper.getInstance().getBuildingsDBRef()
                                .whereArrayContains(FirestoreReferences.Buildings.RESTROOMS, restroom_document.getId())
                                .limit(1)
                                .get()
                                .addOnCompleteListener(task2 ->
                                {
                                    if(task2.isSuccessful())
                                    {
                                        DocumentSnapshot building_document = task2.getResult().getDocuments().get(0);

                                        if(building_document != null && building_document.exists())
                                        {
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
                                        }
                                    }

                                    // Notify after processing the last element
                                    if (restroomData.size() == task1.getResult().size()) {
                                        Log.d("SavedRestroomsActivity", "Constructing Restrooms, size: " + Integer.valueOf(restroomData.size()));
                                        savedRestroomAdapter = new SavedRestroomAdapter(restroomData, this);
                                        rv_recommended_restrooms.setAdapter(savedRestroomAdapter);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    
    private void getRestroomsByTop()
    {
        Log.d("SavedRestroomsActivity", "Getting Top Restrooms");

        //Get the top rated buildings near the user's current location
        // Query Firestore for locations within the bounding box
        savedRestroomAdapter = new SavedRestroomAdapter(restroomData, this);
        rv_recommended_restrooms.setAdapter(savedRestroomAdapter);

        FirestoreHelper.getInstance().getBuildingsDBRef()
        .whereEqualTo(FirestoreReferences.Buildings.SUGGESTION, false) // Filter non-suggested buildings
        .get()
        .addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) 
            {
                QuerySnapshot current_building_documents = task.getResult();

                //1. Get the current locations that should be visible in the map
                if(current_building_documents != null && !current_building_documents.isEmpty()) 
                {
                    Log.d("SavedRestroomsActivity", "Getting nearby buildings " + Integer.valueOf(restroomData.size()));

                    for (QueryDocumentSnapshot building_document : task.getResult())
                    {
                        if(!boundingBox.contains(MapHelper.getInstance().decodeBuildingLocation(building_document.getId())))
                            continue;

                        //2. Get all of the restrooms across all of the buildings in here.
                        ArrayList<String> restroom_ids = (ArrayList<String>) building_document.get(FirestoreReferences.Buildings.RESTROOMS);

                        if(restroom_ids != null && !restroom_ids.isEmpty())
                        {
                            for(String restroom_id : restroom_ids)
                            {
                                FirestoreHelper.getInstance().readRestroom(restroom_id, task1 -> {
                                    if(task1.isSuccessful())
                                    {
                                        if(task1.getResult() != null && task1.getResult().exists())
                                        {
                                            DocumentSnapshot restroom_document = task1.getResult();
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
                                            //Sort restroom data
                                            Collections.sort(restroomData, new RestroomData("", "", "", "", "", "", 0, 0, 0, null));

                                            Log.d("SavedRestroomsActivity", "Constructing Restrooms, size: " + Integer.valueOf(restroomData.size()));
                                            savedRestroomAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
    
    private void getRestroomsBySimilarity()
    {
        Log.d("SavedRestroomsActivity", "Getting Similar Restrooms");

        //1. Get the restrooms that the user has already favorited
        FirestoreHelper.getInstance().readAccount(account_email, task -> 
        {
            if(task.isSuccessful())
            {
                ArrayList<String> saved_restrooms_ids = (ArrayList<String>) task.getResult().get(FirestoreReferences.Accounts.FAVORITE_RESTROOMS);
                
                if(saved_restrooms_ids != null && !saved_restrooms_ids.isEmpty())
                {
                    FirestoreHelper.getInstance().getRestroomsDBRef().whereIn(FieldPath.documentId(), saved_restrooms_ids)
                    .get()
                    .addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            QuerySnapshot saved_restrooms_documents = task1.getResult();
                            double avg_cleanliness = -1;
                            double avg_maintenance = -1;
                            double avg_vacancy = -1;
                            if (saved_restrooms_documents != null && !saved_restrooms_documents.isEmpty()) {
                                avg_cleanliness++;
                                avg_maintenance++;
                                avg_vacancy++;
                                for (QueryDocumentSnapshot saved_restroom_document : saved_restrooms_documents) {
                                    avg_cleanliness += saved_restroom_document.get(FirestoreReferences.Restrooms.CLEANLINESS, Integer.class);
                                    avg_maintenance += saved_restroom_document.get(FirestoreReferences.Restrooms.MAINTENANCE, Integer.class);
                                    avg_vacancy += saved_restroom_document.get(FirestoreReferences.Restrooms.VACANCY, Integer.class);
                                }

                                if (avg_cleanliness > 0 && avg_maintenance > 0 && avg_vacancy > 0) {
                                    avg_cleanliness /= saved_restrooms_documents.size();
                                    avg_maintenance /= saved_restrooms_documents.size();
                                    avg_vacancy /= saved_restrooms_documents.size();
                                    //2. Get the current locations that should be visible in the map
                                    double finalAvg_cleanliness = avg_cleanliness;
                                    double finalAvg_maintenance = avg_maintenance;
                                    double finalAvg_vacancy = avg_vacancy;
                                    FirestoreHelper.getInstance().getBuildingsDBRef()
                                            .whereEqualTo(FirestoreReferences.Buildings.SUGGESTION, false) // Filter non-suggested buildings
                                            .get()
                                            .addOnCompleteListener(task2 ->
                                            {
                                                if (task2.isSuccessful()) {
                                                    QuerySnapshot current_building_documents = task2.getResult();

                                                    if (current_building_documents != null && !current_building_documents.isEmpty()) {
                                                        for (QueryDocumentSnapshot building_document : current_building_documents) {
                                                            if (!boundingBox.contains(MapHelper.getInstance().decodeBuildingLocation(building_document.getId())))
                                                                continue;

                                                            //3. Get all of the restrooms across all of the buildings in here.
                                                            ArrayList<String> restroom_ids = (ArrayList<String>) building_document.get(FirestoreReferences.Buildings.RESTROOMS);
                                                            if (restroom_ids != null && !restroom_ids.isEmpty()) {
                                                                for (String restroom_id : restroom_ids) {
                                                                    FirestoreHelper.getInstance().readRestroom(restroom_id, task3 -> {
                                                                        if (task3.isSuccessful()) {
                                                                            if (task3.getResult() != null && task3.getResult().exists()) {
                                                                                DocumentSnapshot restroom_document = task3.getResult();
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
                                                                                //Sort restroom data
                                                                                sortRestroomsBySimilarity(finalAvg_cleanliness, finalAvg_maintenance, finalAvg_vacancy);
                                                                                Log.d("SavedRestroomsActivity", "Constructing Restrooms, size: " + Integer.valueOf(restroomData.size()));
                                                                                savedRestroomAdapter.notifyDataSetChanged();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });

                }
            }
        });
        
    }
    
    private void sortRestroomsBySimilarity(double avg_cleanliness, double avg_maintenance, double avg_vacancy)
    {
        //1. Sort using selection sort according to metrics above
        for(int i = 0; i < restroomData.size() - 1; i++)
        {
            double min_distance = computeRestroomDistance(restroomData.get(i), avg_cleanliness, avg_maintenance, avg_vacancy);
            for(int j = i; j < restroomData.size(); j++)
            {
                double curr_distance = computeRestroomDistance(restroomData.get(j), avg_cleanliness, avg_maintenance, avg_vacancy);
                if(min_distance > curr_distance)
                {
                    //SWAP the positions of these two things
                    RestroomData temp = restroomData.get(j);
                    restroomData.set(j, restroomData.get(i));
                    restroomData.set(i, temp);

                    min_distance = curr_distance;
                }
            }
        }
    }
    
    private double computeRestroomDistance(RestroomData restroom, double avg_cleanliness, double avg_maintenance, double avg_vacancy)
    {
        double delta_cleanliness = restroom.getCleanliness() - avg_cleanliness;
        double delta_maintenance = restroom.getMaintenance() - avg_maintenance;
        double delta_vacancy = restroom.getVacancy() - avg_vacancy;
        
        return Math.sqrt(delta_cleanliness * delta_cleanliness + delta_maintenance * delta_maintenance + delta_vacancy * delta_vacancy);
    }
}