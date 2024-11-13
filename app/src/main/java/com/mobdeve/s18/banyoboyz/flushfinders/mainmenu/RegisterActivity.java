package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.ProfilePictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import com.google.firebase.firestore.CollectionReference;

import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText et_register_name;
    EditText et_register_email;
    EditText et_register_password;

    private CollectionReference accountsDBRef;
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

        // Initialize accounts DB reference
        this.accountsDBRef = FirebaseFirestore.getInstance().collection(FirestoreReferences.Accounts.COLLECTION);
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
            //Go ahead and create a new field in the firestore
            String hashed_password = BCrypt.hashpw(account_password, BCrypt.gensalt(10));
            Map<String, Object> data = new HashMap<>();

            Bitmap default_profile_picture = BitmapFactory.decodeResource(this.getResources(), R.drawable.mumei);
            default_profile_picture = ProfilePictureHelper.scaleBitmap(default_profile_picture);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                data.put(FirestoreReferences.Accounts.NAME, account_name);
                data.put(FirestoreReferences.Accounts.PASSWORD, hashed_password);
                data.put(FirestoreReferences.Accounts.IS_ACTIVE, true);
                data.put(FirestoreReferences.Accounts.TYPE, "ADMINISTRATOR");
                data.put(FirestoreReferences.Accounts.PROFILE_PICTURE, ProfilePictureHelper.encodeBitmapToBase64(default_profile_picture));
                data.put(FirestoreReferences.Accounts.CREATION_TIME, Instant.now().getEpochSecond());
            }

            accountsDBRef.document(account_email).set(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Log.v("RegisterActivity", "User Account Registration Successful!");

                                //Go to the login activity
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Log.e("RegisterActivity", "User Account Registration FAILED");
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