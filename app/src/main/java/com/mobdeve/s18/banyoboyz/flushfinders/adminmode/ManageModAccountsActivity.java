package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.data.AccountAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.data.AccountData;
public class ManageModAccountsActivity extends AppCompatActivity {

    RecyclerView rv_mod_accounts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_mod_accounts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_mod_accounts = findViewById(R.id.rv_mod_accounts);
        rv_mod_accounts.setHasFixedSize(true);
        rv_mod_accounts.setLayoutManager(new LinearLayoutManager(this));

        AccountData[] accountData = new AccountData[]{
                new AccountData("Aaron Dichoso","aarondichoso17@gmail.com", true, R.drawable.dichoso, AccountData.AccountType.MODERATOR),
                new AccountData("Nanashi Mumei","Mumei7@gmail.com", true, R.drawable.mumei, AccountData.AccountType.MODERATOR)
        };

        AccountAdapter accountAdapter = new AccountAdapter(accountData, ManageModAccountsActivity.this);
        rv_mod_accounts.setAdapter(accountAdapter);
    }
}