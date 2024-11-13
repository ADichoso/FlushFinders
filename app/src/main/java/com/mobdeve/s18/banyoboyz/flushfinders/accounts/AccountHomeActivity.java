package com.mobdeve.s18.banyoboyz.flushfinders.accounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.AdminHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.ProfilePictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.ForgotPasswordActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.LoginActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.MainActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.mainmenu.ResetPasswordActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ModHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SavedBuildingsActivity;

import java.io.IOException;
import java.util.Objects;

public class AccountHomeActivity extends AppCompatActivity {
    public static final String HOME_NAME = "HOME_NAME";
    public static final String HOME_PP = "HOME_PP";

    SharedPreferences sharedpreferences;
    String account_name;
    String account_email;
    String account_pp;

    ImageView iv_account_pp;
    TextView tv_account_name;
    TextView tv_account_email;


    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                public void onActivityResult(ActivityResult result) {
                    Log.d("AccountHomeActivity", "2222WEWEEWWEW");
                    if (result.getResultCode() == RESULT_OK) {
                        //Update Profile Picture things
                        Intent intent = result.getData();
                        account_name = intent.getStringExtra(AccountEditActivity.UPDATE_NAME);
                        account_pp = intent.getStringExtra(AccountEditActivity.UPDATE_PP);
                        updateAccountHome();
                    }
                }
            }
    );

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        // Check if user is already logged in
        account_name = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_NAME_KEY, "");
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");
        account_pp = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_PP_KEY, "");

        updateAccountHome();
    }

    private void updateAccountHome()
    {
        // check if the fields are not null then one current user is logged in
        if (areFieldsNotEmpty(new String[]{account_name, account_email, account_pp}))
        {
            //Valid items, go ahead and show the user info
            Log.d("AccountHomeActivity", "UPDATING UI NOW!" + account_name);
            tv_account_name.setText(account_name);
            tv_account_email.setText(account_email);
            iv_account_pp.setImageBitmap(ProfilePictureHelper.decodeBase64ToBitmap(account_pp));
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
        intent.putExtra(HOME_NAME, account_name);
        intent.putExtra(HOME_PP, account_pp);

        activityResultLauncher.launch(intent);
    }

    public void resetPasswordButton(View view)
    {
        //Check if all fields are not null
        if(!account_email.isEmpty())
        {
            //Go to the reset password activity and pass the given email there.
            Intent intent = new Intent(AccountHomeActivity.this, ResetPasswordActivity.class);
            intent.putExtra(ForgotPasswordActivity.FP_EMAIL, account_email);
            startActivity(intent);
        }
    }
    public void deleteAccountButton(View view)
    {
        Intent intent = new Intent(AccountHomeActivity.this, AccountDeleteActivity.class);

        startActivity(intent);
    }

    public void signoutButton(View view)
    {
        //Clear shared preferences
        SharedPrefReferences.clearSharedPreferences(this);

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