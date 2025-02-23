package com.example.weighttrackingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    TextView currentGoal;
    ListView weightDataGrid;
    int userId; // Store logged-in user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        currentGoal = findViewById(R.id.currentGoal);
        Button addWeightButton = findViewById(R.id.addWeightButton);
        weightDataGrid = findViewById(R.id.weightDataGrid);

        // Get the logged-in user ID
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no user is logged in
            return;
        }


        addWeightButton.setOnClickListener(v -> {
            double weight = 175.0;  // Temporary test value
            String date = "2025-02-20";  // Temporary test date

            if (databaseHelper.addWeight(userId, weight, date)) {
                Toast.makeText(DashboardActivity.this, "Weight added!", Toast.LENGTH_SHORT).show();
                loadWeightData();
            } else {
                Toast.makeText(DashboardActivity.this, "Error adding weight", Toast.LENGTH_SHORT).show();
            }
        });

        loadWeightData();
    }

    private void loadWeightData() {
        Cursor cursor = databaseHelper.getWeightData(userId);  // Pass userId
        ArrayList<String> weightList = new ArrayList<>();

        while (cursor.moveToNext()) {
            weightList.add("Weight: " + cursor.getDouble(2) + " lbs on " + cursor.getString(3));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, weightList);
        weightDataGrid.setAdapter(adapter);
    }
}
