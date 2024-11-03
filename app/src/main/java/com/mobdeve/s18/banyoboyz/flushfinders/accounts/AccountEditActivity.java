package com.mobdeve.s18.banyoboyz.flushfinders.accounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.LoginActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.MainActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.ResetPasswordActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class AccountEditActivity extends AppCompatActivity {
    private CollectionReference accountsDBRef;

    EditText et_edit_account_name;
    EditText et_edit_account_password;

    SharedPreferences sharedpreferences;
    String account_name;
    String account_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize accounts DB reference
        this.accountsDBRef = FirebaseFirestore.getInstance().collection(FirestoreReferences.Accounts.COLLECTION);

        et_edit_account_name = findViewById(R.id.et_edit_account_name);
        et_edit_account_password = findViewById(R.id.et_edit_account_password);

        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        account_name = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_NAME_KEY, "");
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");


        et_edit_account_name.setText(account_name);
    }

    public void editAccountButton(View view)
    {
        account_name = et_edit_account_name.getText().toString();
        String account_password = et_edit_account_password.getText().toString();

        if(areFieldsNotEmpty(new String[]{account_name, account_email, account_password}))
        {
            Map<String, Object> data  = new HashMap<>();

            data.put(FirestoreReferences.Accounts.NAME, account_name);
            data.put(FirestoreReferences.Accounts.PASSWORD, BCrypt.hashpw(account_password, BCrypt.gensalt(10)));
            //Update the entry in the database to have a new password
            //tv_reset_pass_invalid_message.setVisibility(View.INVISIBLE);

            //1. Get the ID of the person's account.
            //2. Update new fields
            accountsDBRef.document(account_email).update(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //Go to Login Page
                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString(SharedPrefReferences.ACCOUNT_NAME_KEY, account_name);
                                editor.apply();
                                finish();
                            }
                            else
                            {
                                //tv_reset_pass_invalid_message.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
        else
        {
            //tv_reset_pass_invalid_message.setVisibility(View.VISIBLE);
        }

        finish();
    }

    private boolean areFieldsNotEmpty(String[] fields)
    {
        for(String field : fields)
            if (!field.isEmpty())
                return true;

        return false;
    }
}