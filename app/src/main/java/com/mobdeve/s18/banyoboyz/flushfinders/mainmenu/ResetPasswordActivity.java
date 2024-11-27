package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.SharedPrefReferences;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity
{
    private String account_email;
    private TextView tv_reset_password_email;
    private EditText et_reset_password_new_password;
    private TextView tv_reset_pass_invalid_message;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_reset_password_email = findViewById(R.id.tv_reset_password_email);
        et_reset_password_new_password = findViewById(R.id.et_reset_password_new_password);
        tv_reset_pass_invalid_message = findViewById(R.id.tv_reset_pass_invalid_message);

        tv_reset_pass_invalid_message.setVisibility(View.INVISIBLE);

        //Get intent extra from ForgotPasswordActivity
        Intent intent = getIntent();
        account_email = intent.getStringExtra(ForgotPasswordActivity.FP_EMAIL);
        tv_reset_password_email.setText(account_email);
    }

    public void resetPasswordButton(View view)
    {
        String account_password = et_reset_password_new_password.getText().toString();

        if(!account_password.isEmpty())
        {
            //Update the entry in the database to have a new password
            tv_reset_pass_invalid_message.setVisibility(View.INVISIBLE);

            //1. Get the ID of the person's account.
            //2. Update the password
            Map<String, Object> update_data = new HashMap<>();

            update_data.put(FirestoreReferences.Accounts.PASSWORD, BCrypt.hashpw(account_password, BCrypt.gensalt(10)));

            FirestoreHelper.getInstance().updateAccount(account_email, update_data, task -> {
                if(task.isSuccessful())
                {
                    //CLEAR SHARED PREFERENCES
                    SharedPrefReferences.clearSharedPreferences(ResetPasswordActivity.this);

                    //Go to Login Page
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                    tv_reset_pass_invalid_message.setVisibility(View.VISIBLE);
            });
        }
        else
            tv_reset_pass_invalid_message.setVisibility(View.VISIBLE);


    }
}