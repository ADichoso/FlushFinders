package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.AdminHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FireAuthHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ModHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;

import org.mindrot.jbcrypt.BCrypt;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        goToAppPermissions();
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
        Log.d("LoginActivity", account_email);

        //Check if all fields are not null
        if(account_email.isEmpty() || account_password.isEmpty())
            return;
        //Check the database and see if there is an account with the given email & password is correct
        FirestoreHelper.getInstance().readAccount(account_email, task ->
        {
            Log.d("LoginActivity", account_email);

            if(!task.isSuccessful())
            {
                tv_login_invalid_message.setVisibility(View.VISIBLE);
                return;
            }

            DocumentSnapshot account_document = task.getResult();
            Log.d("LoginActivity", account_email);

            if(account_document == null || !account_document.exists())
            {
                tv_login_invalid_message.setVisibility(View.VISIBLE);
                return;
            }

            boolean is_active = account_document.get(FirestoreReferences.Accounts.IS_ACTIVE, Boolean.class);

            if(!is_active)
            {
                tv_login_invalid_message.setVisibility(View.VISIBLE);
                return;
            }

            //Successful Login
            //Save account details for shared preferences
            account_name = account_document.getString(FirestoreReferences.Accounts.NAME);
            account_type = account_document.getString(FirestoreReferences.Accounts.TYPE);
            account_pp = account_document.getString(FirestoreReferences.Accounts.PROFILE_PICTURE);

            //Sign in user in firebase auth
            FireAuthHelper.getInstance().signInUser(account_email, account_password, task1 -> {
                if(!task1.isSuccessful())
                    return;

                //Go to that home page
                if(FireAuthHelper.getInstance().isCurrentUserVerified())
                    goToAppPermissions();
                else
                    goToVerificationPage();
            });


        });
    }

    private void goToAppPermissions()
    {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.clear();
        editor.apply();

        editor.putString(SharedPrefReferences.ACCOUNT_NAME_KEY, account_name);
        editor.putString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, account_email);
        editor.putString(SharedPrefReferences.ACCOUNT_TYPE_KEY, account_type);
        editor.putString(SharedPrefReferences.ACCOUNT_PP_KEY, account_pp);
        editor.putLong(SharedPrefReferences.ACCOUNT_LOGIN_TIME_KEY, SharedPrefReferences.daysLoginTime);
        editor.apply();

        Intent intent = new Intent(this, AppPermissionsActivity.class);
        startActivity(intent);
    }

    private void goToVerificationPage()
    {
        FireAuthHelper.getInstance().verifyUser();

        Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
        startActivity(intent);
    }

    public void forgotPasswordButton(View view)
    {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);

        startActivity(intent);
    }
}