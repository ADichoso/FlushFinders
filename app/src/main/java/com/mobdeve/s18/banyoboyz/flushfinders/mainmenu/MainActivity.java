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

import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.AdminHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ModHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    String account_name;
    String account_email;
    String account_type;
    String account_pp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        // Check if user is already logged in
        account_name = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_NAME_KEY, "");
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");
        account_type = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_TYPE_KEY, "");
        account_pp = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_PP_KEY, "");

        // check if the fields are not null then one current user is logged in
        if (areFieldsNotEmpty(new String[]{account_name, account_email, account_type, account_pp}))
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

            startActivity(intent);
        }
    }

    private boolean areFieldsNotEmpty(String[] fields)
    {
        for(String field : fields)
            if (!field.isEmpty())
                return true;

        return false;
    }

    public void useGuestButton(View view)
    {
        Intent intent = new Intent(MainActivity.this, MapHomeActivity.class);

        startActivity(intent);
    }

    public void loginButton(View view)
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

        startActivity(intent);
    }

    public void registerButton(View view)
    {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

        startActivity(intent);
    }
}