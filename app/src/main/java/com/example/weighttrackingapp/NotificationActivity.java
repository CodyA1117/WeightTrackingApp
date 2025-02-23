package com.example.weighttrackingapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NotificationActivity extends AppCompatActivity {

    TextView smsStatus;
    Button requestPermissionButton, enableSmsButton, backToDashboardButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        smsStatus = findViewById(R.id.smsStatus);
        requestPermissionButton = findViewById(R.id.requestPermissionButton);
        enableSmsButton = findViewById(R.id.enableSmsButton);
        backToDashboardButton = findViewById(R.id.backToDashboardButton);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedPhoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "");
        boolean isSmsEnabled = sharedPreferences.getBoolean("SMS_ENABLED", false);

        // Update UI based on saved settings
        smsStatus.setText(isSmsEnabled ? "SMS Notifications Enabled" : "SMS Notifications Disabled");

        // Request SMS permission
        requestPermissionButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                showPhoneNumberDialog(); // Ask user for phone number after permission is granted
            }
        });


        // Enable SMS Notifications
        enableSmsButton.setOnClickListener(v -> {
            if (savedPhoneNumber.isEmpty()) {
                showPhoneNumberDialog(); // Ask user to enter a number before enabling
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("SMS_ENABLED", true);
                editor.apply();
                smsStatus.setText("SMS Notifications Enabled");
                Toast.makeText(this, "SMS Notifications Enabled!", Toast.LENGTH_SHORT).show();
            }
        });

        // Back to Dashboard
        backToDashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void showPhoneNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Phone Number");

        final EditText phoneInput = new EditText(this);
        phoneInput.setHint("Enter your phone number");

        builder.setView(phoneInput);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String phoneNumber = phoneInput.getText().toString().trim();

            if (!phoneNumber.isEmpty()) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PHONE_NUMBER", phoneNumber);
                editor.apply();
                Toast.makeText(this, "Phone number saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}
