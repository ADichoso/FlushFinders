package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.AdminHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ModHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity
{
    private EditText et_login_email;
    private EditText et_login_password;
    private TextView tv_login_invalid_message;

    private SharedPreferences sharedpreferences;
    private String account_name;
    private String account_email;
    private String account_type;
    private String account_pp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_login_email = findViewById(R.id.et_login_email);
        et_login_password = findViewById(R.id.et_login_password);
        tv_login_invalid_message = findViewById(R.id.tv_login_invalid_message);

        tv_login_invalid_message.setVisibility(View.INVISIBLE);

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        // Check if user is already logged in
        account_name = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_NAME_KEY, "");
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");
        account_type = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_TYPE_KEY, "");
        account_pp = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_PP_KEY, "");

        // check if the fields are not null then one current user is logged in
        if (account_name.isEmpty() || account_email.isEmpty() || account_type.isEmpty() || account_pp.isEmpty())
            return;

        goToHomePage();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        //Start main activity intent
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void loginButton(View view)
    {
        account_email = et_login_email.getText().toString();
        String account_password = et_login_password.getText().toString();

        //Check if all fields are not null
        if(account_email.isEmpty() || account_password.isEmpty())
            return;
        //Check the database and see if there is an account with the given email & password is correct
        FirestoreHelper.getInstance().readAccount(account_email, task ->
        {
            if(task.isSuccessful())
            {
                Map<String, Object> data = task.getResult().getData();

                if(data == null || data.isEmpty())
                    tv_login_invalid_message.setVisibility(View.VISIBLE);
                else
                {
                    boolean is_active = Boolean.getBoolean(data.get(FirestoreReferences.Accounts.IS_ACTIVE).toString());
                    //Compare Passwords
                    if(is_active && BCrypt.checkpw(account_password, data.get(FirestoreReferences.Accounts.PASSWORD).toString()))
                    {
                        //Successful Login
                        //Save account details for shared preferences
                        account_name = data.get(FirestoreReferences.Accounts.NAME).toString();
                        account_type = data.get(FirestoreReferences.Accounts.TYPE).toString();
                        account_pp = data.get(FirestoreReferences.Accounts.PROFILE_PICTURE).toString();

                        //Go to that home page
                        goToHomePage();
                    }
                    else
                        tv_login_invalid_message.setVisibility(View.VISIBLE);
                }
            }
            else
                tv_login_invalid_message.setVisibility(View.VISIBLE);
        });
    }

    private void goToHomePage()
    {
        Intent intent = null;
        switch(Objects.requireNonNull(AccountData.convertType(account_type)))
        {
            case USER:
                intent = new Intent(LoginActivity.this, MapHomeActivity.class);
                break;
            case MODERATOR:
                intent = new Intent(LoginActivity.this, ModHomeActivity.class);
                break;
            case ADMINISTRATOR:
                intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                break;
            default:
                //INVALID ACCOUNT
                tv_login_invalid_message.setVisibility(View.VISIBLE);
                break;
        }

        if(intent != null)
        {
            tv_login_invalid_message.setVisibility(View.INVISIBLE);

            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.clear();
            editor.apply();

            editor.putString(SharedPrefReferences.ACCOUNT_NAME_KEY, account_name);
            editor.putString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, account_email);
            editor.putString(SharedPrefReferences.ACCOUNT_TYPE_KEY, account_type);
            editor.putString(SharedPrefReferences.ACCOUNT_PP_KEY, account_pp);
            editor.putLong(SharedPrefReferences.ACCOUNT_LOGIN_TIME_KEY, SharedPrefReferences.daysLoginTime);
            editor.apply();

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }

    public void forgotPasswordButton(View view)
    {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);

        startActivity(intent);
    }
}