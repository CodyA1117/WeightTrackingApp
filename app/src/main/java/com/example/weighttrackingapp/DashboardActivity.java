package com.example.weighttrackingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI components
        TextView currentGoal = findViewById(R.id.currentGoal);
        Button addWeightButton = findViewById(R.id.addWeightButton);
        Button setGoalButton = findViewById(R.id.setGoalButton);
        ListView weightDataGrid = findViewById(R.id.weightDataGrid);


    }
}
