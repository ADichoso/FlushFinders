package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.ProfilePictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CreateModAccountActivity extends AppCompatActivity {
    private CollectionReference accountsDBRef;

    EditText et_create_admin_mod_account_name;
    EditText et_create_admin_mod_account_email;
    EditText et_create_admin_mod_account_password;
    Spinner sp_create_admin_mod_account_acc_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_mod_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize accounts DB reference
        this.accountsDBRef = FirebaseFirestore.getInstance().collection(FirestoreReferences.Accounts.COLLECTION);

        et_create_admin_mod_account_name = findViewById(R.id.et_create_admin_mod_account_name);
        et_create_admin_mod_account_email = findViewById(R.id.et_create_admin_mod_account_email);
        et_create_admin_mod_account_password = findViewById(R.id.et_create_admin_mod_account_password);
        sp_create_admin_mod_account_acc_type = findViewById(R.id.sp_create_admin_mod_account_acc_type);

        //Set spinner options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mod_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_create_admin_mod_account_acc_type.setAdapter(adapter);
    }

    public void createAccountButton(View view)
    {
        String account_name = et_create_admin_mod_account_name.getText().toString();
        String account_email = et_create_admin_mod_account_email.getText().toString();
        String account_password = et_create_admin_mod_account_password.getText().toString();
        String account_type = sp_create_admin_mod_account_acc_type.getSelectedItem().toString();

        //Check if all fields are not null
        if(!areFieldsEmpty(new String[]{account_name, account_email, account_password, account_type}))
        {
            accountsDBRef.document(account_email).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                Map<String, Object> user_check_data = task.getResult().getData();
                                if(user_check_data == null || user_check_data.isEmpty())
                                {
                                    //Go ahead and create a new field in the firestore
                                    String hashed_password = BCrypt.hashpw(account_password, BCrypt.gensalt(10));

                                    Bitmap default_profile_picture = BitmapFactory.decodeResource(CreateModAccountActivity.this.getResources(), R.drawable.mumei);
                                    default_profile_picture = ProfilePictureHelper.scaleBitmap(default_profile_picture);

                                    //THE USER WITH THIS EMAIL DOES NOT EXIST
                                    //ID of accounts will be the email (Unique Identifier)
                                    Map<String, Object> data = new HashMap<>();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        data.put(FirestoreReferences.Accounts.NAME, account_name);
                                        data.put(FirestoreReferences.Accounts.PASSWORD, hashed_password);
                                        data.put(FirestoreReferences.Accounts.IS_ACTIVE, true);
                                        data.put(FirestoreReferences.Accounts.TYPE, account_type);
                                        data.put(FirestoreReferences.Accounts.PROFILE_PICTURE, ProfilePictureHelper.encodeBitmapToBase64(default_profile_picture));
                                        data.put(FirestoreReferences.Accounts.CREATION_TIME, Instant.now().getEpochSecond());
                                    }

                                    accountsDBRef.document(account_email).set(data)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Log.v("CreateModAccountActivity", "Mod/Admin Account Registration Successful!");

                                                        //Go back
                                                        finish();
                                                    }
                                                    else
                                                    {
                                                        Log.e("CreateModAccountActivity", "Mod/Admin Account Registration FAILED");
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });


        }
        finish();
    }

    private boolean areFieldsEmpty(String[] fields)
    {
        for(String field : fields)
            if (!field.isEmpty())
                return false;

        return true;
    }
}