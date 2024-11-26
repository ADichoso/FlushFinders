package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.AccountAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class ManageModAccountsActivity extends AppCompatActivity
{

    private RecyclerView rv_mod_accounts;
    private AccountAdapter account_adapter;

    private SharedPreferences shared_preferences;
    private String account_email;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_mod_accounts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> 
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv_mod_accounts = findViewById(R.id.rv_mod_accounts);
        rv_mod_accounts.setHasFixedSize(true);
        rv_mod_accounts.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() 
    {
        super.onStart();

        // getting the data which is stored in shared preferences.
        shared_preferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        // Check if user is already logged in
        account_email = shared_preferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");
        
        if(account_email.isEmpty())
            return;
        
        //Query the accounts from the database
        FirestoreHelper.getInstance().getAccountsDBRef().orderBy(FirestoreReferences.Accounts.NAME).get().addOnCompleteListener(task -> 
        {
            if(task.isSuccessful())
                return;

            ArrayList<AccountData> account_list = new ArrayList<AccountData>();

            QuerySnapshot account_documents = task.getResult();

            if(account_documents == null || account_documents.isEmpty())
                return;

            //Populate account list
            for(QueryDocumentSnapshot account_document : account_documents)
            {
                Map<String, Object> data = account_document.getData();

                account_list.add
                (
                    new AccountData
                    (
                        account_document.getId(),
                        data.get(FirestoreReferences.Accounts.NAME).toString(),
                        data.get(FirestoreReferences.Accounts.PASSWORD).toString(),
                        Boolean.parseBoolean(data.get(FirestoreReferences.Accounts.IS_ACTIVE).toString()),
                        data.get(FirestoreReferences.Accounts.PROFILE_PICTURE).toString(),
                        Instant.ofEpochSecond(Long.parseLong(data.get(FirestoreReferences.Accounts.CREATION_TIME).toString())),
                        AccountData.convertType(data.get(FirestoreReferences.Accounts.TYPE).toString()),
                        account_document.getId().equals(account_email),
                        new ArrayList<RestroomData>()
                    )
                );
            }

            //Assign to recycler view
            account_adapter = new AccountAdapter(account_list, ManageModAccountsActivity.this);
            rv_mod_accounts.setAdapter(account_adapter);
        });
    }
}