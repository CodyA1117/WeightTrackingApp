package com.example.weighttrackingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NotificationActivity extends AppCompatActivity {

    TextView smsStatus;
    Button requestPermissionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        smsStatus = findViewById(R.id.smsStatus);
        requestPermissionButton = findViewById(R.id.requestPermissionButton);

        requestPermissionButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                Toast.makeText(this, "Permission already granted!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
