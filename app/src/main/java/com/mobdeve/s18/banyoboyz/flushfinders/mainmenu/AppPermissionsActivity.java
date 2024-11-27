package com.mobdeve.s18.banyoboyz.flushfinders.mainmenu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.AdminHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.SharedPrefReferences;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ModHomeActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.MapHomeActivity;

import java.util.Objects;

public class AppPermissionsActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;
    private String account_name;
    private String account_email;
    private String account_type;
    private String account_pp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_permissions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences(SharedPrefReferences.SHARED_PREFS, Context.MODE_PRIVATE);

        // Check if user is already logged in
        account_name = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_NAME_KEY, "");
        account_email = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_EMAIL_KEY, "");
        account_type = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_TYPE_KEY, "");
        account_pp = sharedpreferences.getString(SharedPrefReferences.ACCOUNT_PP_KEY, "");
    }


    @Override
    protected void onResume() {
        super.onResume();

        //Check if location is turned on
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if either GPS or Network provider is enabled
        if (locationManager != null) {
            boolean is_GPS_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean is_network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (is_GPS_enabled || is_network_enabled) {
                // Location services are turned on
                Toast.makeText(this, "Location services are enabled.", Toast.LENGTH_SHORT).show();
            } else {
                // Location services are turned off
                Toast.makeText(this, "Location services are turned off. Please enable them.", Toast.LENGTH_LONG).show();
                return;
            }
        } else
        {
            Toast.makeText(this, "Location services are turned off. Please enable them.", Toast.LENGTH_LONG).show();

            // Redirect to the location settings
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

            return;
        }

        //Check user permissions here first
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        goToHomePage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                goToHomePage();
            }
            else
            {
                Toast.makeText(this, "Permission denied. Cannot access location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToHomePage()
    {
        if (account_name.isEmpty() || account_email.isEmpty() || account_type.isEmpty() || account_pp.isEmpty())
        {
            goToMainMenu();
            return;
        }

        Intent intent = null;
        switch(Objects.requireNonNull(AccountData.convertType(account_type)))
        {
            case USER:
                intent = new Intent(this, MapHomeActivity.class);
                break;
            case MODERATOR:
                intent = new Intent(this, ModHomeActivity.class);
                break;
            case ADMINISTRATOR:
                intent = new Intent(this, AdminHomeActivity.class);
                break;
            default:
                //INVALID ACCOUNT
                break;
        }

        if(intent != null)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void mainMenuButton(View view)
    {
        goToMainMenu();
    }

    private void goToMainMenu()
    {
        //Clear shared preferences
        SharedPrefReferences.clearSharedPreferences(this);

        //Go back to main menu
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}