package com.mobdeve.s18.banyoboyz.flushfinders.accounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import android.provider.MediaStore;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccountEditActivity extends AppCompatActivity
{
    public static final String UPDATE_NAME = "UPDATE_NAME";
    public static final String UPDATE_PP = "UPDATE_PP";

    private EditText et_edit_account_name;

    private SharedPreferences shared_preference;
    private String account_name;
    private String account_email;
    private String account_pp;

    private TextView tv_invalid_message;
    private ImageView iv_pp_preview;

    private ActivityResultLauncher<Intent> activity_result_launcher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            public void onActivityResult(ActivityResult result)
            {
                Log.d("AccountEditActivity", "IN HERE!");
                if (result.getResultCode() != RESULT_OK)
                    return;

                //Obtained the new profile picture here
                Uri image_URI = result.getData().getData();

                Log.d("AccountEditActivity", "GOT PHOTO");
                // Load the selected image into a Bitmap
                Bitmap raw_bitmap = null;
                try
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), image_URI);
                        raw_bitmap = ImageDecoder.decodeBitmap(source);
                    } else
                    {
                        raw_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_URI);
                    }
                }
                catch (IOException e)
                {
                    Log.e("AccountEditActivity", "IMAGE NULL", e);
                    throw new RuntimeException(e);
                }

                if(raw_bitmap != null)
                {
                    Log.d("AccountEditActivity", "UPLOADED NEW IMAGE FOR USE");
                    // Crop the center 512 x 512 region of the Bitmap
                    Bitmap scaled_bitmap = PictureHelper.scaleBitmap(raw_bitmap, 512, 512);

                    //Encode the cropped Bitmap into a string to upload later
                    account_pp = PictureHelper.encodeBitmapToBase64(scaled_bitmap);
                    iv_pp_preview.setImageBitmap(scaled_bitmap);
                }
                else
                {
                    Log.d("AccountEditActivity", "IMAGE NULL");
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_invalid_message = findViewById(R.id.tv_invalid_message);
        et_edit_account_name = findViewById(R.id.et_edit_account_name);
        iv_pp_preview = findViewById(R.id.iv_pp_preview);

        Intent result_intent = getIntent();
        account_name = result_intent.getStringExtra(AccountHomeActivity.HOME_NAME);
        account_pp = result_intent.getStringExtra(AccountHomeActivity.HOME_PP);

        tv_invalid_message.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //Shared Preferences
        shared_preference = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);
        account_email = shared_preference.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");

        et_edit_account_name.setText(account_name);

        if(!account_name.isEmpty())
            et_edit_account_name.setText(account_name);
        if(!account_pp.isEmpty())
            iv_pp_preview.setImageBitmap(PictureHelper.decodeBase64ToBitmap(account_pp));
    }

    public void editAccountButton(View view)
    {
        account_name = et_edit_account_name.getText().toString();

        if(account_name.isEmpty() || account_email.isEmpty())
            tv_invalid_message.setVisibility(View.VISIBLE);
        else
        {
            //Update Firestore database entry for account
            Map<String, Object> data  = new HashMap<>();
            data.put(FirestoreReferences.Accounts.NAME, account_name);
            data.put(FirestoreReferences.Accounts.PROFILE_PICTURE, account_pp);

            //1. Get the ID of the person's account.
            //2. Update new fields
            FirestoreHelper.getInstance().updateAccount(account_email, data, task ->
                {
                    if(task.isSuccessful())
                    {
                        //Go to Login Page
                        SharedPreferences.Editor editor = shared_preference.edit();

                        editor.putString(SharedPrefReferences.ACCOUNT_NAME_KEY, account_name);
                        editor.putString(SharedPrefReferences.ACCOUNT_PP_KEY, account_pp);
                        editor.apply();
                        tv_invalid_message.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        tv_invalid_message.setVisibility(View.VISIBLE);
                    }
                }
            );
        }


        //Send an intent to the Account Home regarding the new user information
        Intent return_intent = new Intent();
        return_intent.putExtra(UPDATE_NAME, account_name);
        return_intent.putExtra(UPDATE_PP, account_pp);
        setResult(RESULT_OK, return_intent);

        finish();
    }

    public void getProfilePicture(View view)
    {
        //Get new details
        Intent photo_intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        setResult(RESULT_OK, photo_intent);

        activity_result_launcher.launch(photo_intent);
    }
}