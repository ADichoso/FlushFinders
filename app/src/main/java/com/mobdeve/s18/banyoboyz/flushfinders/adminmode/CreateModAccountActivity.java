package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

public class CreateModAccountActivity extends AppCompatActivity {
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
            FirestoreHelper.getInstance().readAccount(account_email, task -> {
                if(task.isSuccessful())
                {
                    Map<String, Object> user_check_data = task.getResult().getData();
                    if(user_check_data == null || user_check_data.isEmpty())
                    {
                        Bitmap default_profile_picture = BitmapFactory.decodeResource(CreateModAccountActivity.this.getResources(), R.drawable.mumei);
                        default_profile_picture = PictureHelper.scaleBitmap(default_profile_picture);

                        //THE USER WITH THIS EMAIL DOES NOT EXIST
                        //ID of accounts will be the email (Unique Identifier)
                        Map<String, Object> data = FirestoreHelper.getInstance().createAccountData
                                (
                                        account_name,
                                        account_password,
                                        true,
                                        account_type,
                                        default_profile_picture,
                                        Instant.now().getEpochSecond()
                                );

                        FirestoreHelper.getInstance().insertAccount(account_email, data, task1 -> {
                            if(task1.isSuccessful())
                            {
                                Log.v("CreateModAccountActivity", "Mod/Admin Account Registration Successful!");

                                //Go back
                                finish();
                            }
                            else
                            {
                                Log.e("CreateModAccountActivity", "Mod/Admin Account Registration FAILED");
                            }
                        });
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