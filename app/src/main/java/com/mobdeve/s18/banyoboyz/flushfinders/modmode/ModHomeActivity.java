package com.mobdeve.s18.banyoboyz.flushfinders.modmode;

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
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;

public class ModHomeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mod_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void accountHomeButton(View view)
    {
        Intent intent = new Intent(ModHomeActivity.this, AccountHomeActivity.class);

        startActivity(intent);
    }

    public void editRestroomButton(View view)
    {
        Intent intent = new Intent(ModHomeActivity.this, EditRestroomActivity.class);

        startActivity(intent);
    }

    public void createRestroomButton(View view)
    {
        Intent intent = new Intent(ModHomeActivity.this, SuggestRestroomLocationActivity.class);

        intent.putExtra(SuggestRestroomLocationActivity.CALLER, "MOD_ADMIN");
        startActivity(intent);
    }

    public void reviewRestroomsButton(View view)
    {
        Intent intent = new Intent(ModHomeActivity.this, ReviewUserReportsActivity.class);

        startActivity(intent);
    }

    public void viewUserSuggestionsButton(View view)
    {
        Intent intent = new Intent(ModHomeActivity.this, ViewUserSuggestionsActivity.class);

        startActivity(intent);
    }
}