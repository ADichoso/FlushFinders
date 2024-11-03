package com.mobdeve.s18.banyoboyz.flushfinders.accounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.AdminHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.LoginActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.MainActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ModHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SavedBuildingsActivity;

import java.util.Objects;

public class AccountHomeActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    String account_name;
    String account_email;
    int account_pp;

    ImageView iv_account_pp;
    TextView tv_account_name;
    TextView tv_account_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iv_account_pp = findViewById(R.id.iv_account_pp);
        tv_account_name = findViewById(R.id.tv_account_name);
        tv_account_email = findViewById(R.id.tv_account_email);

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        // Check if user is already logged in
        account_name = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_NAME_KEY, "");
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");
        account_pp = sharedpreferences.getInt(SharedPrefReferences.ACCOUNT_PP_KEY, -1);

        // check if the fields are not null then one current user is logged in
        if (areFieldsNotEmpty(new String[]{account_name, account_email}) && account_pp != -1)
        {
            //Valid items, go ahead and show the user info
            tv_account_name.setText(account_name);
            tv_account_email.setText(account_email);
            iv_account_pp.setImageResource(account_pp);
        }


    }

    private boolean areFieldsNotEmpty(String[] fields)
    {
        for(String field : fields)
            if (!field.isEmpty())
                return true;

        return false;
    }

    public void viewSavedRestroomsButton(View view)
    {
        Intent intent = new Intent(AccountHomeActivity.this, SavedBuildingsActivity.class);

        startActivity(intent);
    }

    public void editAccountButton(View view)
    {
        Intent intent = new Intent(AccountHomeActivity.this, AccountEditActivity.class);

        startActivity(intent);
    }

    public void deleteAccountButton(View view)
    {
        Intent intent = new Intent(AccountHomeActivity.this, AccountDeleteActivity.class);

        startActivity(intent);
    }

    public void signoutButton(View view)
    {
        //Clear shared preferences
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(AccountHomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void backButton(View view)
    {
        finish();
    }
}