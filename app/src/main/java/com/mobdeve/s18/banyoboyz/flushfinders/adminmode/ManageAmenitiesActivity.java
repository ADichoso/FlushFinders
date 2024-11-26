package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.ManageAmenityAdapter;

import java.util.ArrayList;
import java.util.Map;

public class ManageAmenitiesActivity extends AppCompatActivity
{
    private ArrayList<AmenityData> amenity_list;
    private ManageAmenityAdapter manage_amenity_adapter;
    private RecyclerView rv_manage_amenities;

    private final ActivityResultLauncher<Intent> activity_result_launcher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (result.getResultCode() != RESULT_OK)
                return;

            //New amenity has been added, update adapter and Recycler View
            Intent intent = result.getData();
            String amenity_name = intent.getStringExtra(CreateAmenityActivity.AMENITY_NAME);
            String amenity_picture = intent.getStringExtra(CreateAmenityActivity.AMENITY_PICTURE);

            amenity_list.add(new AmenityData(amenity_name, amenity_picture));
            manage_amenity_adapter.notifyItemInserted(amenity_list.size() - 1);
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_amenities);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        amenity_list = new ArrayList<AmenityData>();

        rv_manage_amenities = findViewById(R.id.rv_manage_amenities);
        rv_manage_amenities.setHasFixedSize(true);
        rv_manage_amenities.setLayoutManager(new LinearLayoutManager(this));
    }

    public void createAmenityButton(View view)
    {
        Intent intent = new Intent(this, CreateAmenityActivity.class);

        activity_result_launcher.launch(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        
        //Get all amenities
        FirestoreHelper.getInstance().getAmenitiesDBRef().get().addOnCompleteListener(task ->
        {
            if(!task.isSuccessful())
                return;

            QuerySnapshot amenity_documents = task.getResult();
            
            if(amenity_documents == null || amenity_documents.isEmpty())
                return;
            
            //Populate amenity list
            for(QueryDocumentSnapshot amenity_document : amenity_documents)
            {
                Map<String, Object> data = amenity_document.getData();
                amenity_list.add
                (
                    new AmenityData
                    (
                        amenity_document.getId(),
                        data.get(FirestoreReferences.Amenities.PICTURE).toString()
                    )
                );
            }

            //Assign to recycler view
            manage_amenity_adapter = new ManageAmenityAdapter(amenity_list, ManageAmenitiesActivity.this);
            rv_manage_amenities.setAdapter(manage_amenity_adapter);
        });
    }
}