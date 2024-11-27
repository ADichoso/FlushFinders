package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import static com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.RegisterActivity.EMAIL_ALREADY_EXISTS;
import static com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.RegisterActivity.EMPTY_FIELDS;
import static com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.RegisterActivity.MAX_PASSWORD_LENGTH;
import static com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.RegisterActivity.MIN_PASSWORD_LENGTH;
import static com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.RegisterActivity.PASSWORD_TOO_SHORT;
import static com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.RegisterActivity.SERVER_BUSY;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CreateModAccountActivity extends AppCompatActivity
{
    private EditText et_create_admin_mod_account_name;
    private EditText et_create_admin_mod_account_email;
    private EditText et_create_admin_mod_account_password;
    private Spinner sp_create_admin_mod_account_acc_type;
    private TextView tv_invalid_message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_mod_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_create_admin_mod_account_name = findViewById(R.id.et_create_admin_mod_account_name);
        et_create_admin_mod_account_email = findViewById(R.id.et_create_admin_mod_account_email);
        et_create_admin_mod_account_password = findViewById(R.id.et_create_admin_mod_account_password);
        sp_create_admin_mod_account_acc_type = findViewById(R.id.sp_create_admin_mod_account_acc_type);
        tv_invalid_message = findViewById(R.id.tv_invalid_message);

        tv_invalid_message.setVisibility(View.INVISIBLE);

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
        tv_invalid_message.setVisibility(View.INVISIBLE);

        //Check if all fields are not null
        if(account_name.isEmpty() || account_email.isEmpty() || account_password.isEmpty() || account_type.isEmpty())
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
        //Check if user exists
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

            //User does not currently exist, you are free to create a new account.
            //TODO: Pick a different default profile picture.
            Bitmap default_profile_picture = BitmapFactory.decodeResource(CreateModAccountActivity.this.getResources(), R.drawable.mumei);
            default_profile_picture = PictureHelper.scaleBitmap(default_profile_picture, 512, 512);

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

            FirestoreHelper.getInstance().insertAccount(account_email, data, task1 ->
            {
                if(task1.isSuccessful())
                {
                    Log.v("CreateModAccountActivity", "Mod/Admin Account Registration Successful!");
                    //Save the user details onto FireAuth as well
                    FireAuthHelper.getInstance().createUser(account_email, account_password);

                    //Go back
                    finish();
                }
                else
                {
                    tv_invalid_message.setText(SERVER_BUSY);
                    tv_invalid_message.setVisibility(View.VISIBLE);
                    Log.e("CreateModAccountActivity", "Mod/Admin Account Registration FAILED");
                }
            });
        });

        finish();
    }
}