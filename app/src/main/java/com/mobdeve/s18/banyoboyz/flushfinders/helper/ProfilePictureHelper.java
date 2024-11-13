package com.mobdeve.s18.banyoboyz.flushfinders.helper;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import android.graphics.BitmapFactory;

public class ProfilePictureHelper {
    public static String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); // Compress as JPEG or PNG
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64ToBitmap(String base64String) {
        // Decode the Base64 string into a byte array
        byte[] decodedByteArray = Base64.decode(base64String, Base64.DEFAULT);
        // Convert the byte array into a Bitmap
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public static void uploadProfileImageToFirestore(String encoded_profile_picture, String account_email)
    {
        CollectionReference accountsDBRef = FirebaseFirestore.getInstance().collection(FirestoreReferences.Accounts.COLLECTION);

        //Update Firestore database entry for account
        Map<String, Object> data  = new HashMap<>();

        data.put(FirestoreReferences.Accounts.PROFILE_PICTURE, encoded_profile_picture);

        accountsDBRef.document(account_email).update(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            //Go to Login Page
                            Log.d("AccountEditActivity", "Profile image URL saved successfully");
                        }
                        else
                        {
                            //tv_reset_pass_invalid_message.setVisibility(View.VISIBLE);
                            Log.e("AccountEditActivity", "Failed to save profile image URL");
                        }
                    }
                });
    }


    // Step 4: Function to crop the center portion of a Bitmap
    public static Bitmap scaleBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, 512, 512, true);
    }
}
