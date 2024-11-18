package com.mobdeve.s18.banyoboyz.flushfinders.models;

import android.graphics.Bitmap;
import android.location.Address;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;

import org.mindrot.jbcrypt.BCrypt;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {
    private static FirestoreHelper instance = null;

    public static FirestoreHelper getInstance()
    {
        if(instance == null){
            instance = new FirestoreHelper();
        }

        return instance;
    }

    public FirestoreHelper()
    {

    }

    public CollectionReference getAccountsDBRef()
    {
        return FirebaseFirestore.getInstance().collection(FirestoreReferences.Accounts.COLLECTION);
    }

    public CollectionReference getBuildingsDBRef()
    {
        return FirebaseFirestore.getInstance().collection(FirestoreReferences.Buildings.COLLECTION);
    }

    public CollectionReference getRestroomsDBRef()
    {
        return FirebaseFirestore.getInstance().collection(FirestoreReferences.Restrooms.COLLECTION);
    }

    public CollectionReference getAmenitiesDBRef()
    {
        return FirebaseFirestore.getInstance().collection(FirestoreReferences.Amenities.COLLECTION);
    }

    public Map<String, Object> createAccountData(String name, String password, boolean isActive, String type, Bitmap profile_picture, Long creation_time_epoch_seconds)
    {
        Map<String, Object> data = new HashMap<>();

        String hashed_password = BCrypt.hashpw(password, BCrypt.gensalt(10));

        data.put(FirestoreReferences.Accounts.NAME, name);
        data.put(FirestoreReferences.Accounts.PASSWORD, hashed_password);
        data.put(FirestoreReferences.Accounts.IS_ACTIVE, isActive);
        data.put(FirestoreReferences.Accounts.TYPE, type);
        data.put(FirestoreReferences.Accounts.PROFILE_PICTURE, PictureHelper.encodeBitmapToBase64(profile_picture));
        data.put(FirestoreReferences.Accounts.CREATION_TIME, creation_time_epoch_seconds);

        return data;
    }

    public Map<String, Object> createBuildingData(String name, String address, Bitmap building_picture, boolean suggestion)
    {
        Map<String, Object> data = new HashMap<>();

        data.put(FirestoreReferences.Buildings.NAME, name);
        data.put(FirestoreReferences.Buildings.ADDRESS, address);
        data.put(FirestoreReferences.Buildings.BUILDING_PICTURE, PictureHelper.encodeBitmapToBase64(building_picture));
        data.put(FirestoreReferences.Buildings.RESTROOMS, new ArrayList<String>());
        data.put(FirestoreReferences.Buildings.SUGGESTION, suggestion);
        return data;
    }

    public Map<String, Object> createRestroomData(String name, int cleanliness, int maintenance, int vacancy, ArrayList<String> amenities)
    {
        Map<String, Object> data = new HashMap<>();

        data.put(FirestoreReferences.Restrooms.NAME, name);
        data.put(FirestoreReferences.Restrooms.CLEANLINESS, cleanliness);
        data.put(FirestoreReferences.Restrooms.MAINTENANCE, maintenance);
        data.put(FirestoreReferences.Restrooms.VACANCY, vacancy);
        data.put(FirestoreReferences.Restrooms.AMENITIES, amenities);

        return data;
    }

    public String generateBuildingID(Double building_latitude, Double building_longitude)
    {
        return Double.toString(building_latitude) + "!" + Double.toString(building_longitude);
    }

    public GeoPoint decodeBuildingLocation(String building_id)
    {
        double latitude = 0;
        double longitude = 0;

        String[] coords = building_id.split("!");

        try {
            latitude = Double.parseDouble(coords[0]);
            longitude = Double.parseDouble(coords[1]);
        }
        catch (Exception e){}

        return new GeoPoint(latitude, longitude);
    }
    public void insertBuilding(Double building_latitude, Double building_longitude, Map<String, Object> building_data,  OnCompleteListener<Void> onCompleteListener)
    {
        insertData(FirestoreReferences.Buildings.COLLECTION, generateBuildingID(building_latitude, building_longitude), building_data, onCompleteListener);
    }

    public void updateBuilding(Double building_latitude, Double building_longitude, Map<String, Object> building_data,  OnCompleteListener<Void> onCompleteListener)
    {
        updateData(FirestoreReferences.Buildings.COLLECTION, generateBuildingID(building_latitude, building_longitude), building_data, onCompleteListener);
    }

    public void readBuilding(Double building_latitude, Double building_longitude, OnCompleteListener<DocumentSnapshot> onCompleteListener)
    {
        readData(FirestoreReferences.Buildings.COLLECTION, generateBuildingID(building_latitude, building_longitude), onCompleteListener);
    }

    public void deleteBuilding(Double building_latitude, Double building_longitude, OnCompleteListener<Void> onCompleteListener)
    {
        deleteData(FirestoreReferences.Buildings.COLLECTION, generateBuildingID(building_latitude, building_longitude), onCompleteListener);
    }

    public void insertRestroom(String id, Map<String, Object> restroom_data,  OnCompleteListener<Void> onCompleteListener)
    {
        insertData(FirestoreReferences.Restrooms.COLLECTION, id, restroom_data, onCompleteListener);
    }

    public void updateRestroom(String id, Map<String, Object> restroom_data,  OnCompleteListener<Void> onCompleteListener)
    {
        updateData(FirestoreReferences.Restrooms.COLLECTION, id, restroom_data, onCompleteListener);
    }

    public void readRestroom(String id, OnCompleteListener<DocumentSnapshot> onCompleteListener)
    {
        readData(FirestoreReferences.Restrooms.COLLECTION, id, onCompleteListener);
    }

    public void deleteRestroom(String id, OnCompleteListener<Void> onCompleteListener)
    {
        deleteData(FirestoreReferences.Restrooms.COLLECTION, id, onCompleteListener);
    }

    public void insertAmenity(String amenity_name, String amenity_picture,  OnCompleteListener<Void> onCompleteListener)
    {
        Map<String, Object> data = new HashMap<>();
        data.put(FirestoreReferences.Amenities.PICTURE, amenity_picture);

        insertData(FirestoreReferences.Amenities.COLLECTION, amenity_name, data, onCompleteListener);
    }

    public void updateAmenity(String amenity_name, String amenity_picture,  OnCompleteListener<Void> onCompleteListener)
    {
        Map<String, Object> data = new HashMap<>();
        data.put(FirestoreReferences.Amenities.PICTURE, amenity_picture);

        insertData(FirestoreReferences.Amenities.COLLECTION, amenity_name, data, onCompleteListener);    }

    public void readAmenity(String amenity_name, OnCompleteListener<DocumentSnapshot> onCompleteListener)
    {
        readData(FirestoreReferences.Amenities.COLLECTION, amenity_name, onCompleteListener);
    }

    public void deleteAmenity(String amenity_name, OnCompleteListener<Void> onCompleteListener)
    {
        deleteData(FirestoreReferences.Amenities.COLLECTION, amenity_name, onCompleteListener);
    }

    public void insertAccount(String account_email, Map<String, Object> account_data,  OnCompleteListener<Void> onCompleteListener)
    {
        insertData(FirestoreReferences.Accounts.COLLECTION, account_email, account_data, onCompleteListener);
    }

    public void updateAccount(String account_email, Map<String, Object> account_data,  OnCompleteListener<Void> onCompleteListener)
    {
        updateData(FirestoreReferences.Accounts.COLLECTION, account_email, account_data, onCompleteListener);
    }

    public void readAccount(String account_email, OnCompleteListener<DocumentSnapshot> onCompleteListener)
    {
        readData(FirestoreReferences.Accounts.COLLECTION, account_email, onCompleteListener);
    }

    public void deleteAccount(String account_email, OnCompleteListener<Void> onCompleteListener)
    {
        deleteData(FirestoreReferences.Accounts.COLLECTION, account_email, onCompleteListener);
    }



    private void updateData(String collection, String document_id, Map<String, Object> data,  OnCompleteListener<Void> onCompleteListener)
    {
        FirebaseFirestore.getInstance().collection(collection).document(document_id).update(data)
                .addOnCompleteListener(onCompleteListener);
    }

    private void insertData(String collection, String document_id, Map<String, Object> data,  OnCompleteListener<Void> onCompleteListener)
    {
        FirebaseFirestore.getInstance().collection(collection).document(document_id).set(data)
                .addOnCompleteListener(onCompleteListener);
    }

    private void readData(String collection, String document_id, OnCompleteListener<DocumentSnapshot> onCompleteListener)
    {
        FirebaseFirestore.getInstance().collection(collection).document(document_id).get()
                .addOnCompleteListener(onCompleteListener);
    }

    private void deleteData(String collection, String document_id, OnCompleteListener<Void> onCompleteListener)
    {
        FirebaseFirestore.getInstance().collection(collection).document(document_id).delete()
                .addOnCompleteListener(onCompleteListener);
    }

    public void appendStringToStringArray(String collection, String document_id, String array_field, String new_string)
    {
        FirebaseFirestore.getInstance().collection(collection)  // Specify the collection name
                .document(document_id)      // Specify the document ID
                .update(array_field, FieldValue.arrayUnion(new_string))  // Update array field
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated

                })
                .addOnFailureListener(e -> {
                    // Error handling

                });
    }
}
