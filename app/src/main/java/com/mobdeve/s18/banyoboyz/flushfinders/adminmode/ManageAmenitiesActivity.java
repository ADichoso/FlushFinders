package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.ManageAmenitiesAdapter;

import java.util.ArrayList;
import java.util.Map;

public class ManageAmenitiesActivity extends AppCompatActivity {
    private ArrayList<AmenityData> amenityData;
    private ManageAmenitiesAdapter manageAmenitiesAdapter;
    private RecyclerView rv_manage_amenities;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    //New amenity has been added, update adapter and Recycler View
                    Intent intent = result.getData();
                    String amenity_name = intent.getStringExtra(CreateAmenityActivity.AMENITY_NAME);
                    String amenity_picture = intent.getStringExtra(CreateAmenityActivity.AMENITY_PICTURE);

                    amenityData.add(new AmenityData(amenity_name, amenity_picture));
                    manageAmenitiesAdapter.notifyItemInserted(amenityData.size() - 1);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_amenities);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        amenityData = new ArrayList<AmenityData>();

        rv_manage_amenities = findViewById(R.id.rv_manage_amenities);
        rv_manage_amenities.setHasFixedSize(true);
        rv_manage_amenities.setLayoutManager(new LinearLayoutManager(this));
    }

    public void createAmenityButton(View view)
    {
        Intent intent = new Intent(this, CreateAmenityActivity.class);

        activityResultLauncher.launch(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirestoreHelper.getInstance().getAmenitiesDBRef();

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                for(QueryDocumentSnapshot document : task.getResult())
                {
                    Map<String, Object> data = document.getData();
                    amenityData.add
                    (
                        new AmenityData
                        (
                            document.getId(),
                            data.get(FirestoreReferences.Amenities.PICTURE).toString()
                        )
                    );
                    Log.d("ManageAmenitiesActivity", document.getId());
                }


                manageAmenitiesAdapter = new ManageAmenitiesAdapter(amenityData, ManageAmenitiesActivity.this);
                rv_manage_amenities.setAdapter(manageAmenitiesAdapter);
            }
            else
            {
                Log.w("ManageAmenitiesActivity", "TASK NOT SUCCESSFUL", task.getException());
            }
        });
    }
}