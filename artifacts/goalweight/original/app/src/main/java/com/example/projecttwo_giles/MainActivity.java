package com.example.projecttwo_giles;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button buttonLogin;
    private Button buttonCreateAccount;

    private GoalWeightDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use activity_login.xml as the layout for this screen
        setContentView(R.layout.activity_login);

        // Get references to UI elements from activity_login.xml
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        // Create database helper
        dbHelper = new GoalWeightDbHelper(this);

        // Handle "Create Account" button click
        buttonCreateAccount.setOnClickListener(v -> createAccount());

        // Handle "Log In" button click
        buttonLogin.setOnClickListener(v -> loginUser());
    }

    // Create a new user account
    private void createAccount() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the username already exists
        Cursor cursor = db.query(
                GoalWeightDbHelper.TABLE_USERS,
                new String[]{GoalWeightDbHelper.COLUMN_USER_ID},
                GoalWeightDbHelper.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            // Username already taken
            cursor.close();
            Toast.makeText(this, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
        } else {
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(GoalWeightDbHelper.COLUMN_USERNAME, username);
            values.put(GoalWeightDbHelper.COLUMN_PASSWORD, password);

            long newId = db.insert(GoalWeightDbHelper.TABLE_USERS, null, values);

            if (newId == -1) {
                Toast.makeText(this, "Error creating account.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Account created! You can log in now.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Log in an existing user
    private void loginUser() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                GoalWeightDbHelper.TABLE_USERS,
                new String[]{GoalWeightDbHelper.COLUMN_USER_ID},
                GoalWeightDbHelper.COLUMN_USERNAME + " = ? AND " +
                        GoalWeightDbHelper.COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(GoalWeightDbHelper.COLUMN_USER_ID)
            );
            cursor.close();

            // For now, just show a success message.
            // Later, we'll navigate to the weight screen and pass this userId.
            Toast.makeText(this, "Login successful! User ID: " + userId, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, WeightsActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);

        } else {
            cursor.close();
            Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
        }
    }
}
