package com.example.weighttrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (databaseHelper.checkUser(username, password)) {
                // Get the user ID after login
                int userId = databaseHelper.getUserId(username);

                if (userId != -1) {
                    // Save the user ID to SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("USER_ID", userId);
                    editor.apply();
                }

                // Move to Dashboard
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });


        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (databaseHelper.registerUser(username, password)) {
                Toast.makeText(LoginActivity.this, "User registered!", Toast.LENGTH_SHORT).show();

                // Get the user ID from the database after registration
                int userId = databaseHelper.getUserId(username);

                if (userId != -1) {
                    // Save the logged-in user ID in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("USER_ID", userId);
                    editor.apply();
                }

            } else {
                Toast.makeText(LoginActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
