package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.AdminHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ModHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;

import java.util.Map;
import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    public static final String FP_EMAIL = "FP_EMAIL";

    EditText et_forgot_pass_email;
    TextView tv_forgot_pass_invalid_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_forgot_pass_email = findViewById(R.id.et_forgot_pass_email);
        tv_forgot_pass_invalid_message = findViewById(R.id.tv_forgot_pass_invalid_message);

        tv_forgot_pass_invalid_message.setVisibility(View.INVISIBLE);
    }

    public void resetPasswordButton(View view)
    {
        //Get the given email and check if that email exists
        String account_email = et_forgot_pass_email.getText().toString();

        //Check if all fields are not null
        if(!account_email.isEmpty())
        {
            //Check the database and see if there is an account with the given email & password is correct
            //Query the accounts from the database

            FirestoreHelper.getInstance().readAccount(account_email, task -> {
                //Found the correct account
                if(task.isSuccessful())
                {
                    //Go to the reset password activity and pass the given email there.
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra(FP_EMAIL, account_email);
                    startActivity(intent);
                }
                else
                {
                    tv_forgot_pass_invalid_message.setVisibility(View.VISIBLE);
                    Log.w("ManageModAccountsActivity", "TASK NOT SUCCESSFUL", task.getException());
                }
            });
        }
    }
}