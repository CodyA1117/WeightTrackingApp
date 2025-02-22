package com.example.weighttrackingapp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseHelper = new DatabaseHelper(getApplicationContext());


        currentGoal = findViewById(R.id.currentGoal);
        Button addWeightButton = findViewById(R.id.addWeightButton);
        weightDataGrid = findViewById(R.id.weightDataGrid);

        addWeightButton.setOnClickListener(v -> {
            if (databaseHelper.addWeight(175.0, "2025-02-20")) {
                Toast.makeText(DashboardActivity.this, "Weight added!", Toast.LENGTH_SHORT).show();
                loadWeightData();
            } else {
                Toast.makeText(DashboardActivity.this, "Error adding weight", Toast.LENGTH_SHORT).show();
            }
        });

        loadWeightData();
    }

    private void loadWeightData() {
        Cursor cursor = databaseHelper.getWeightData();
        ArrayList<String> weightList = new ArrayList<>();

        while (cursor.moveToNext()) {
            weightList.add("Weight: " + cursor.getDouble(1) + " lbs on " + cursor.getString(2));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, weightList);
        weightDataGrid.setAdapter(adapter);
    }
}
