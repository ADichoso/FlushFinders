package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;

import org.mindrot.jbcrypt.BCrypt;

public class ResetPasswordActivity extends AppCompatActivity {

    private CollectionReference accountsDBRef;
    private String account_email;
    TextView tv_reset_password_email;
    EditText et_reset_password_new_password;
    TextView tv_reset_pass_invalid_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize accounts DB reference
        this.accountsDBRef = FirebaseFirestore.getInstance().collection(FirestoreReferences.Accounts.COLLECTION);

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
            accountsDBRef.document(account_email).update(FirestoreReferences.Accounts.PASSWORD, BCrypt.hashpw(account_password, BCrypt.gensalt(10)))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
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
                            {
                                tv_reset_pass_invalid_message.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
        else
        {
            tv_reset_pass_invalid_message.setVisibility(View.VISIBLE);
        }


    }
}