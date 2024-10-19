package com.mobdeve.s18.banyoboyz.flushfinders.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.MainActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.RateRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ReviewReportRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SavedRestroomsActivity;

public class AccountHomeActivity extends AppCompatActivity {

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
    }

    public void viewSavedRestroomsButton(View view)
    {
        Intent intent = new Intent(AccountHomeActivity.this, SavedRestroomsActivity.class);

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