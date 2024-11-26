package com.mobdeve.s18.banyoboyz.flushfinders.usermode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.AmenityAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.BuildingRestroomAdapter;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class ViewRestroomActivity extends AppCompatActivity
{
    public static final String BUILDING_ID = "BUILDING_ID";
    public static final String RESTROOM_ID = "RESTROOM_ID";

    SharedPreferences sharedpreferences;
    String account_email;

    ImageButton btn_toggle_favorite;
    TextView tv_restroom_building;
    TextView tv_restroom_floor;
    ProgressBar pb_cleanliness;
    ProgressBar pb_maintenance;
    ProgressBar pb_vacancy;
    RecyclerView rv_restroom_amenities;

    private String building_id;
    private String restroom_id;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_restroom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_toggle_favorite = findViewById(R.id.btn_toggle_favorite);
        tv_restroom_building = findViewById(R.id.tv_restroom_building);
        tv_restroom_floor = findViewById(R.id.tv_restroom_floor);
        pb_cleanliness = findViewById(R.id.pb_cleanliness);
        pb_maintenance = findViewById(R.id.pb_maintenance);
        pb_vacancy = findViewById(R.id.pb_vacancy);

        rv_restroom_amenities = findViewById(R.id.rv_restroom_amenities);
        rv_restroom_amenities.setHasFixedSize(true);
        rv_restroom_amenities.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        building_id = intent.getStringExtra(BuildingRestroomAdapter.BUILDING_ID);
        restroom_id = intent.getStringExtra(BuildingRestroomAdapter.RESTROOM_ID);
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        //SHARED PREFERENCES
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");

        //Toggle the image button at the start according to if the restroom is already favorited by the user.
        FirestoreHelper.getInstance().readAccount(account_email, task ->
        {
            if(!task.isSuccessful())
                return;
            DocumentSnapshot account_document = task.getResult();

            if(account_document != null && account_document.exists()) {
                //2. User exists. Get their favorite restrooms
                ArrayList<String> restrooms_ids = (ArrayList<String>) account_document.get(FirestoreReferences.Accounts.FAVORITE_RESTROOMS);

                toggleFavoriteButton(restrooms_ids != null && !restrooms_ids.isEmpty() && restrooms_ids.contains(restroom_id));
            }
        });

        //GETTING RESTROOM DATA HERE
        if(restroom_id.isEmpty() || building_id.isEmpty() || account_email.isEmpty())
            return;

        GeoPoint building_location = MapHelper.getInstance().decodeBuildingLocation(building_id);
        FirestoreHelper.getInstance().readBuilding(building_location.getLatitude(), building_location.getLongitude(), task ->
        {
            if(task.isSuccessful() && task.getResult().exists())
                tv_restroom_building.setText(task.getResult().getString(FirestoreReferences.Buildings.NAME));
        });

        FirestoreHelper.getInstance().readRestroom(restroom_id, task ->
        {
            if(!task.isSuccessful())
                return;
            //Found the building, get the restrooms
            DocumentSnapshot restroom_document = task.getResult();

            tv_restroom_floor.setText(restroom_document.getString(FirestoreReferences.Restrooms.NAME));
            pb_cleanliness.setProgress(restroom_document.get(FirestoreReferences.Restrooms.CLEANLINESS, Integer.class));
            pb_maintenance.setProgress(restroom_document.get(FirestoreReferences.Restrooms.MAINTENANCE, Integer.class));
            pb_vacancy.setProgress(restroom_document.get(FirestoreReferences.Restrooms.VACANCY, Integer.class));

            ArrayList<String> amenities_ids = (ArrayList<String>) restroom_document.get(FirestoreReferences.Restrooms.AMENITIES);

            if(amenities_ids == null || amenities_ids.isEmpty())
            {
                ArrayList<AmenityData> amenityData = new ArrayList<AmenityData>();

                AmenityAdapter amenityAdapter = new AmenityAdapter(amenityData, ViewRestroomActivity.this);
                rv_restroom_amenities.setAdapter(amenityAdapter);
            }
            else
            {
                FirestoreHelper.getInstance().getAmenitiesDBRef().whereIn(FieldPath.documentId(), amenities_ids)
                .get()
                .addOnCompleteListener(task2 ->
                {
                    //Got amenities
                    if(!task2.isSuccessful())
                        return;
                    ArrayList<AmenityData> amenityData = new ArrayList<AmenityData>();

                    for(QueryDocumentSnapshot amenity_document : task2.getResult())
                    {
                        amenityData.add(new AmenityData
                            (
                                amenity_document.getId(),
                                amenity_document.getString(FirestoreReferences.Amenities.PICTURE)
                            )
                        );
                    }

                    AmenityAdapter amenityAdapter = new AmenityAdapter(amenityData, ViewRestroomActivity.this);
                    rv_restroom_amenities.setAdapter(amenityAdapter);
                });
            }
        });
    }

    public void getRestroomDirectionsButton(View view)
    {
        Intent intent = new Intent(ViewRestroomActivity.this, MapHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BUILDING_ID, building_id);
        intent.putExtra(RESTROOM_ID, restroom_id);
        startActivity(intent);
        finish();
    }

    public void toggleRestroomAsFavoriteButton(View view)
    {
        //Toggle the restroom as the user's favorite, then toggle the image displayed accordingly

        //1. Get the user first

        FirestoreHelper.getInstance().readAccount(account_email, task ->
        {
            if(!task.isSuccessful())
                return;
            DocumentSnapshot account_document = task.getResult();

            if(account_document == null || !account_document.exists())
                return;

            //2. User exists. Get their favorite restrooms
            ArrayList<String> restrooms_ids = (ArrayList<String>) account_document.get(FirestoreReferences.Accounts.FAVORITE_RESTROOMS);

            if(restrooms_ids != null && !restrooms_ids.isEmpty() && restrooms_ids.contains(restroom_id))
            {
                //3. The user currently has this restroom as a favorite. Remove it from the array
                FirestoreHelper.getInstance().getAccountsDBRef().document(account_email)
                .update(FirestoreReferences.Accounts.FAVORITE_RESTROOMS, FieldValue.arrayRemove(restroom_id))
                .addOnCompleteListener(task1 ->
                {
                    if(task1.isSuccessful())
                        //4. Update the button design
                        toggleFavoriteButton(false);
                });
            }
            else
            {
                //5. User currently does not have the restroom favorited. Add it to the array.
                FirestoreHelper.getInstance().getAccountsDBRef().document(account_email)
                .update(FirestoreReferences.Accounts.FAVORITE_RESTROOMS, FieldValue.arrayUnion(restroom_id))
                .addOnCompleteListener(task1 ->
                {
                    if(task1.isSuccessful())
                        //4. Update the button design
                        toggleFavoriteButton(true);
                });
            }

        });
    }

    private void toggleFavoriteButton(boolean is_favorite)
    {
        if(is_favorite)
            btn_toggle_favorite.setImageResource(R.drawable.icon_star_glow);
        else
            btn_toggle_favorite.setImageResource(R.drawable.icon_star);
    }
}