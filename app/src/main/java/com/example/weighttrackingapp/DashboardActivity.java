package com.example.weighttrackingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    TextView currentGoal;
    ListView weightDataGrid;
    int userId;
    ArrayAdapter<String> adapter;
    ArrayList<String> weightList = new ArrayList<>();
    ArrayList<Integer> weightIds = new ArrayList<>();
    Button notificationButton;
    ImageView notificationBell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        currentGoal = findViewById(R.id.currentGoal);
        Button addWeightButton = findViewById(R.id.addWeightButton);
        Button setGoalButton = findViewById(R.id.setGoalButton);
        weightDataGrid = findViewById(R.id.weightDataGrid);
        notificationButton = findViewById(R.id.notificationButton);
        notificationBell = findViewById(R.id.notificationBell);

        // Get logged-in user ID
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load goal weight
        int goalWeight = sharedPreferences.getInt("GOAL_WEIGHT", 180);
        currentGoal.setText("Current Goal: " + goalWeight + " lbs");

        // Add Weight Dialog
        addWeightButton.setOnClickListener(v -> showAddWeightDialog(false, -1, "", ""));

        // Set Goal Dialog
        setGoalButton.setOnClickListener(v -> showSetGoalDialog());

        // Redirect to NotificationActivity
        notificationButton.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));

        // Show notification feedback when bell is clicked
        notificationBell.setOnClickListener(v -> showNotificationFeedback());

        // Click on weight entry to edit or delete
        weightDataGrid.setOnItemClickListener((parent, view, position, id) -> {
            int weightId = weightIds.get(position);
            String weightEntry = weightList.get(position);
            String[] parts = weightEntry.split(" lbs on ");
            showAddWeightDialog(true, weightId, parts[0].split(": ")[1], parts[1]);
        });

        loadWeightData();
    }

    // Show pop-up dialog for adding/updating weight
    private void showAddWeightDialog(boolean isEdit, int weightId, String oldWeight, String oldDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Update or Delete Weight" : "Enter Weight and Date");

        View layout = getLayoutInflater().inflate(R.layout.dialog_weight_input, null);
        EditText weightInput = layout.findViewById(R.id.weightInput);
        EditText dateInput = layout.findViewById(R.id.dateInput);

        if (isEdit) {
            weightInput.setText(oldWeight);
            dateInput.setText(oldDate);
        }

        builder.setView(layout);

        builder.setPositiveButton(isEdit ? "Update" : "Submit", (dialog, which) -> {
            String weightStr = weightInput.getText().toString();
            String dateStr = dateInput.getText().toString();

            if (!weightStr.isEmpty() && !dateStr.isEmpty()) {
                double weight = Double.parseDouble(weightStr);
                if (isEdit) {
                    databaseHelper.updateWeight(weightId, weight, dateStr);
                } else {
                    databaseHelper.addWeight(userId, weight, dateStr);
                }
                Toast.makeText(DashboardActivity.this, "Weight recorded!", Toast.LENGTH_SHORT).show();
                loadWeightData();

                // 🔹 Trigger notification and SMS when a weight is added
                showNotificationFeedback();
            } else {
                Toast.makeText(DashboardActivity.this, "Please enter valid values", Toast.LENGTH_SHORT).show();
            }
        });

        if (isEdit) {
            builder.setNegativeButton("Delete", (dialog, which) -> {
                databaseHelper.deleteWeight(weightId);
                Toast.makeText(DashboardActivity.this, "Weight deleted!", Toast.LENGTH_SHORT).show();
                loadWeightData();
            });
        }

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    // Show pop-up dialog for setting goal weight
    private void showSetGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Goal Weight");

        final EditText goalInput = new EditText(this);
        goalInput.setHint("Enter Goal Weight (lbs)");
        goalInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(goalInput);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String goalStr = goalInput.getText().toString();
            if (!goalStr.isEmpty()) {
                int goalWeight = Integer.parseInt(goalStr);
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("GOAL_WEIGHT", goalWeight);
                editor.apply();

                currentGoal.setText("Current Goal: " + goalWeight + " lbs");
                Toast.makeText(DashboardActivity.this, "Goal Updated!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    // Show notification feedback logic
    private void showNotificationFeedback() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int goalWeight = sharedPreferences.getInt("GOAL_WEIGHT", 180);
        boolean smsEnabled = sharedPreferences.getBoolean("SMS_ENABLED", false);
        String phoneNumber = sharedPreferences.getString("PHONE_NUMBER", "");

        Cursor cursor = databaseHelper.getWeightData(userId);
        if (cursor.getCount() < 1) {
            return;
        }

        double latestWeight = 0, previousWeight = 0;
        if (cursor.moveToLast()) {
            latestWeight = cursor.getDouble(2);
        }
        if (cursor.moveToPrevious()) {
            previousWeight = cursor.getDouble(2);
        }

        String message;
        if (latestWeight == goalWeight) {
            message = "🎉 Congratulations! You reached your goal! Set a new goal and keep tracking!";
        } else if (previousWeight == 0) {
            message = "✅ First weight recorded! Keep progressing!";
        } else if (Math.abs(latestWeight - goalWeight) < Math.abs(previousWeight - goalWeight)) {
            message = "✅ Great job! You're getting closer to your goal!";
        } else {
            message = "⚠️ You're moving away from your goal. Stay focused!";
        }

        new AlertDialog.Builder(this)
                .setTitle("Progress Notification")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();

        // 🔹 Send SMS notification if enabled
        if (smsEnabled && !phoneNumber.isEmpty()) {
            sendSMSNotification(phoneNumber, message);
        }
    }

    // Send SMS notification
    private void sendSMSNotification(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS notification sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show();
        }
    }

    // Load weight data
    private void loadWeightData() {
        Cursor cursor = databaseHelper.getWeightData(userId);
        weightList.clear();
        weightIds.clear();

        while (cursor.moveToNext()) {
            weightIds.add(cursor.getInt(0));
            weightList.add("Weight: " + cursor.getDouble(2) + " lbs on " + cursor.getString(3));
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, weightList);
        weightDataGrid.setAdapter(adapter);
    }
}
