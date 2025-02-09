package com.example.weighttrackingapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button addWeightButton = findViewById(R.id.addWeightButton);
        Button smsPermissionButton = findViewById(R.id.smsPermissionButton);

        addWeightButton.setOnClickListener(v -> {
            // Placeholder: Add weight logic
        });

        smsPermissionButton.setOnClickListener(v -> {
            // Placeholder: Request SMS permissions
        });
    }
}
