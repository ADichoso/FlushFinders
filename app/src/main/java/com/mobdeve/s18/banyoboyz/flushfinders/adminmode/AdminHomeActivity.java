package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.accounts.AccountHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.EditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ReviewUserReportsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ViewUserSuggestionsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;

public class AdminHomeActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void accountHomeButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, AccountHomeActivity.class);

        startActivity(intent);
    }

    public void editRestroomButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, EditRestroomActivity.class);

        startActivity(intent);
    }

    public void createRestroomButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, SuggestRestroomLocationActivity.class);

        intent.putExtra("CALLER", "MOD_ADMIN");
        startActivity(intent);
    }

    public void manageAmenitiesButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, ManageAmenitiesActivity.class);

        startActivity(intent);
    }

    public void reviewRestroomsButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, ReviewUserReportsActivity.class);

        startActivity(intent);
    }

    public void viewUserSuggestionsButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, ViewUserSuggestionsActivity.class);

        startActivity(intent);
    }

    public void createModAdminAccountButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, CreateModAccountActivity.class);

        startActivity(intent);
    }

    public void manageModAccountsButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, ManageModAccountsActivity.class);

        startActivity(intent);
    }

    public void deleteRestroomButton(View view)
    {
        Intent intent = new Intent(AdminHomeActivity.this, DeleteRestroomActivity.class);

        startActivity(intent);
    }
}