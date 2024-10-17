package com.mobdeve.s18.banyoboyz.flushfinders;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    Spinner sp_acc_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp_acc_type = findViewById(R.id.sp_register_acc_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
        (
          this,
          R.array.user_types_array,
          android.R.layout.simple_spinner_dropdown_item
        );

        sp_acc_type.setAdapter(adapter);
    }
}