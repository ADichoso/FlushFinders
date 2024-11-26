package com.mobdeve.s18.banyoboyz.flushfinders.helper;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import android.graphics.BitmapFactory;

public class PictureHelper
{
    public static String encodeBitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // Compress as JPEG or PNG
        byte[] byte_array = baos.toByteArray();
        return Base64.encodeToString(byte_array, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64ToBitmap(String base64String)
    {
        // Decode the Base64 string into a byte array
        byte[] byte_array = Base64.decode(base64String, Base64.DEFAULT);
        // Convert the byte array into a Bitmap
        return BitmapFactory.decodeByteArray(byte_array, 0, byte_array.length);
    }

    public static Bitmap scaleBitmap(Bitmap raw_bitmap, int new_width, int new_height)
    {
        // Get original dimensions
        int raw_width = raw_bitmap.getWidth();
        int raw_height = raw_bitmap.getHeight();

        // Determine the scale factor to fit the target dimensions
        float ratio_width = (float) new_width / raw_width;
        float ratio_height = (float) new_height / raw_height;
        float scale = Math.max(ratio_width, ratio_height); // Ensure the Bitmap fully covers the target dimensions

        // Calculate scaled dimensions
        int scaled_width = Math.round(raw_width * scale);
        int scaled_height = Math.round(ratio_height * scale);

        // Scale the Bitmap to the computed dimensions
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(raw_bitmap, scaled_width, scaled_height, true);

        // Calculate the crop starting points (to crop from the center)
        int cropX = (scaled_width - new_width) / 2;
        int cropY = (scaled_height - new_height) / 2;

        // Crop the Bitmap to the target dimensions
        Bitmap cropped_bitmap = Bitmap.createBitmap(scaledBitmap, cropX, cropY, new_width, new_height);

        return cropped_bitmap;
    }

}
