package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.accounts.AccountEditActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;

import java.io.IOException;
import java.util.Map;

public class CreateAmenityActivity extends AppCompatActivity {
    public static final String AMENITY_NAME = "AMENITY_NAME";
    public static final String AMENITY_PICTURE = "AMENITY_PICTURE";

    private ImageView iv_amenity_icon;
    private EditText et_amenity_name;
    private String amenity_picture;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    //Obtained the new profile picture here
                    Uri image_URI = result.getData().getData();

                    // Load the selected image into a Bitmap
                    Bitmap originalBitmap = null;
                    try
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), image_URI);
                            originalBitmap = ImageDecoder.decodeBitmap(source);
                        } else {
                            originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_URI);
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e("CreateAmenityActivity", "IMAGE NULL", e);
                        throw new RuntimeException(e);
                    }

                    if(originalBitmap != null)
                    {
                        Log.d("CreateAmenityActivity", "UPLOADED NEW IMAGE FOR USE");
                        // Crop the center 512 x 512 region of the Bitmap
                        Bitmap scaledBitmap = PictureHelper.scaleBitmap(originalBitmap);

                        //Encode the cropped Bitmap into a string to upload later
                        amenity_picture = PictureHelper.encodeBitmapToBase64(scaledBitmap);

                        iv_amenity_icon.setImageBitmap(scaledBitmap);
                    } else
                    {
                        Log.d("CreateAmenityActivity", "IMAGE NULL");
                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_amenity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_amenity_name = findViewById(R.id.et_amenity_name);
        iv_amenity_icon = findViewById(R.id.iv_amenity_icon);
    }

    public void getAmenityIconButton(View view)
    {
        //Open the gallery and get a photo from there
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        setResult(RESULT_OK, pickPhoto);
        activityResultLauncher.launch(pickPhoto);
    }


    public void createAmenityButton(View view)
    {
        //Check if all items are not empty
        String amenity_name = et_amenity_name.getText().toString();
        Log.d("CreateAmenityActivity", amenity_name);
        if(!amenity_name.isEmpty() && !amenity_picture.isEmpty())
        {
            //Check first if the amenity does not already exist
            FirestoreHelper.getInstance().readAmenity(amenity_name, task ->
            {
                if(task.isSuccessful())
                {
                    Map<String, Object> old_amenity_data = task.getResult().getData();

                    if(old_amenity_data == null || old_amenity_data.isEmpty())
                    {
                        //Amenity does not currently exist, feel free to insert the new amenity
                        FirestoreHelper.getInstance().insertAmenity(amenity_name, amenity_picture, task1 ->
                        {
                            if(task1.isSuccessful())
                            {
                                //Successful
                                Intent return_intent = new Intent(this, ManageAmenitiesActivity.class);

                                return_intent.putExtra(AMENITY_NAME, amenity_name);
                                return_intent.putExtra(AMENITY_PICTURE, amenity_picture);

                                setResult(RESULT_OK, return_intent);
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }
}