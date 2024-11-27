package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FireAuthHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreHelper;

import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 32;
    public static final String PASSWORD_TOO_SHORT = "A password should be between " + String.valueOf(MIN_PASSWORD_LENGTH) + " and " + String.valueOf(MAX_PASSWORD_LENGTH) + " characters long!";
    public static final String EMAIL_ALREADY_EXISTS = "An account already exists with that email!";
    public static final String EMPTY_FIELDS = "Please fill out ALL fields!";
    public static final String SERVER_BUSY = "The server appears busy at the moment. Please try again later.";



    private EditText et_register_name;
    private EditText et_register_email;
    private EditText et_register_password;
    private TextView tv_invalid_message;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
        tv_invalid_message = findViewById(R.id.tv_invalid_message);

        tv_invalid_message.setVisibility(View.INVISIBLE);
    }

    public void registerButton(View view)
    {
        //Check filled up fields
        String account_name = et_register_name.getText().toString();
        String account_email = et_register_email.getText().toString();
        String account_password = et_register_password.getText().toString();

        tv_invalid_message.setVisibility(View.INVISIBLE);

        //Check if all fields are not null
        if(account_name.isEmpty() || account_email.isEmpty() || account_password.isEmpty())
        {
            tv_invalid_message.setText(EMPTY_FIELDS);
            tv_invalid_message.setVisibility(View.VISIBLE);
            return;
        }
        if(account_password.length() < MIN_PASSWORD_LENGTH || account_password.length() > MAX_PASSWORD_LENGTH)
        {
            //Password too short.
            tv_invalid_message.setText(PASSWORD_TOO_SHORT);
            tv_invalid_message.setVisibility(View.VISIBLE);
            return;
        }
        FirestoreHelper.getInstance().readAccount(account_email, task ->
        {
            if(!task.isSuccessful())
            {
                tv_invalid_message.setText(SERVER_BUSY);
                tv_invalid_message.setVisibility(View.VISIBLE);
                return;
            }

            DocumentSnapshot account_document = task.getResult();

            if(account_document != null && account_document.exists())
            {
                tv_invalid_message.setText(EMAIL_ALREADY_EXISTS);
                tv_invalid_message.setVisibility(View.VISIBLE);
                return;
            }

            //THE USER WITH THIS EMAIL DOES NOT EXIST
            //Generate Default Profile Picture
            //TODO: Pick a different default profile picture.
            Bitmap default_profile_picture = BitmapFactory.decodeResource(RegisterActivity.this.getResources(), R.drawable.looey);
            default_profile_picture = PictureHelper.scaleBitmap(default_profile_picture, 512, 512);

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
            FirestoreHelper.getInstance().insertAccount(account_email, data, task1 ->
            {
                if(task1.isSuccessful())
                {
                    Log.v("RegisterActivity", "User Account Registration Successful!");
                    //Save the user details onto FireAuth as well
                    FireAuthHelper.getInstance().createUser(account_email, account_password);
                    //Go back
                    finish();
                }
                else
                {
                    tv_invalid_message.setText(SERVER_BUSY);
                    tv_invalid_message.setVisibility(View.VISIBLE);
                    Log.e("RegisterActivity", "User Account Registration FAILED");
                }
            });
        });
    }
}