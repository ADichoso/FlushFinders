package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;

import java.time.Instant;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText et_register_name;
    EditText et_register_email;
    EditText et_register_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_register_name = findViewById(R.id.et_register_name);
        et_register_email = findViewById(R.id.et_register_email);
        et_register_password = findViewById(R.id.et_register_password);

    }

    public void registerButton(View view)
    {
        //Check filled up fields
        String account_name = et_register_name.getText().toString();
        String account_email = et_register_email.getText().toString();
        String account_password = et_register_password.getText().toString();

        //Check if all fields are not null
        if(!areFieldsEmpty(new String[]{account_name, account_email, account_password}))
        {
            FirestoreHelper.getInstance().readAccount(account_email, task -> {
                if(task.isSuccessful())
                {
                    Map<String, Object> user_check_data = task.getResult().getData();
                    if(user_check_data == null || user_check_data.isEmpty())
                    {
                        //THE USER WITH THIS EMAIL DOES NOT EXIST

                        //Generate Default Profile Picture
                        Bitmap default_profile_picture = BitmapFactory.decodeResource(RegisterActivity.this.getResources(), R.drawable.mumei);
                        default_profile_picture = PictureHelper.scaleBitmap(default_profile_picture);

                        //Create account data
                        Map<String, Object> data = FirestoreHelper.getInstance().createAccountData
                                (
                                        account_name,
                                        account_password,
                                        true,
                                        "USER",
                                        default_profile_picture,
                                        Instant.now().getEpochSecond()
                                );

                        //Insert account into Firestore
                        FirestoreHelper.getInstance().insertAccount(account_email, data, task1 -> {
                            if(task1.isSuccessful())
                            {
                                Log.v("RegisterActivity", "User Account Registration Successful!");

                                //Go back
                                finish();
                            }
                            else
                            {
                                Log.e("RegisterActivity", "User Account Registration FAILED");
                            }
                        });
                    }
                }
            });
        }
    }

    private boolean areFieldsEmpty(String[] fields)
    {
        for(String field : fields)
            if (!field.isEmpty())
                return false;

        return true;
    }
}