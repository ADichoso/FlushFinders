package com.mobdeve.s18.banyoboyz.flushfinders.adminmode;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.AccountAdapter;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class ManageModAccountsActivity extends AppCompatActivity {

    RecyclerView rv_mod_accounts;

    // DB reference
    private CollectionReference accountsDBRef;
    private AccountAdapter accountAdapter;


    SharedPreferences sharedpreferences;
    String account_email;
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

        // Initialize accounts DB reference
        this.accountsDBRef = FirebaseFirestore.getInstance().collection(FirestoreReferences.Accounts.COLLECTION);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        // Check if user is already logged in
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");


        //Query the accounts from the database
        Query query = accountsDBRef.orderBy(FirestoreReferences.Accounts.NAME);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    Log.d("ManageModAccountsActivity", "Account Results:" + task.getResult().toString());
                    Log.d("ManageModAccountsActivity", "Account Results Length:" + task.getResult().size());

                    ArrayList<AccountData> accountData = new ArrayList<AccountData>();
                    int i = 0;
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        Map<String, Object> data = document.getData();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            accountData.add(new AccountData(
                                    document.getId(),
                                    data.get(FirestoreReferences.Accounts.NAME).toString(),
                                    data.get(FirestoreReferences.Accounts.PASSWORD).toString(),
                                    Boolean.parseBoolean(data.get(FirestoreReferences.Accounts.IS_ACTIVE).toString()),
                                    data.get(FirestoreReferences.Accounts.PROFILE_PICTURE).toString(),
                                    Instant.ofEpochSecond(Long.parseLong(data.get(FirestoreReferences.Accounts.CREATION_TIME).toString())),
                                    AccountData.convertType(data.get(FirestoreReferences.Accounts.TYPE).toString()),
                                    document.getId().equals(account_email)
                                    )
                            );
                        }
                    }

                    accountAdapter = new AccountAdapter(accountData, ManageModAccountsActivity.this, accountsDBRef);
                    rv_mod_accounts.setAdapter(accountAdapter);
                }
                else
                {
                    Log.w("ManageModAccountsActivity", "TASK NOT SUCCESSFUL", task.getException());
                }
            }
        });
    }
}