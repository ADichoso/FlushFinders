package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.helper.FireAuthHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.R;

public class MainActivity extends AppCompatActivity 
{
    private SharedPreferences shared_preferences;
    private Long account_login_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // getting the data which is stored in shared preferences.
        shared_preferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);
        // Check if user is already logged in
        account_login_time = shared_preferences.getLong(SharedPrefReferences.ACCOUNT_LOGIN_TIME_KEY, -1);

        if(account_login_time == -1 || (account_login_time > 0 && SharedPrefReferences.isLoginExpired(account_login_time)))
            //Login has expired, clear sharedPreferences
            signout();
        else
            //Still logged in
            login();
    }

    public void useGuestButton(View view)
    {
        signout();

        Intent intent = new Intent(MainActivity.this, MapHomeActivity.class);

        startActivity(intent);
    }

    public void loginButton(View view)
    {
        login();
    }

    public void registerButton(View view)
    {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

        startActivity(intent);
    }

    private void login()
    {
        //Still logged in
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void signout()
    {
        SharedPrefReferences.clearSharedPreferences(this);
        FireAuthHelper.getInstance().signOutUser();
    }
}