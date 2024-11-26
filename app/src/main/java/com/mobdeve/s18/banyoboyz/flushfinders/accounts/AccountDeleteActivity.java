package com.mobdeve.s18.banyoboyz.flushfinders.accounts;

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

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.MainActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;

public class AccountDeleteActivity extends AppCompatActivity
{
    private SharedPreferences shared_preference;
    private String account_email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_delete);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the data which is stored in shared preferences.
        shared_preference = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        account_email = shared_preference.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");
    }

    public void deleteAccountButton(View view)
    {
        //Delete the account
        FirestoreHelper.getInstance().deleteAccount(account_email, task ->
        {
            if(!task.isSuccessful())
                return;

            //Clear shared preferences
            SharedPrefReferences.clearSharedPreferences(AccountDeleteActivity.this);

            //Go back to main menu
            Intent intent = new Intent(AccountDeleteActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    public void backButton(View view)
    {
        finish();
    }
}