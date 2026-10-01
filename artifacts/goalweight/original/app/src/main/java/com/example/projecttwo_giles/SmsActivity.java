package com.example.projecttwo_giles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SmsActivity extends AppCompatActivity {

    private static final int REQUEST_SMS_PERMISSION = 1001;

    private GoalWeightDbHelper dbHelper;
    private int userId;

    private EditText editPhoneNumber;
    private EditText editGoalWeight;
    private Button buttonRequestSmsPermission;
    private TextView textSmsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        dbHelper = new GoalWeightDbHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editGoalWeight = findViewById(R.id.editGoalWeight);
        buttonRequestSmsPermission = findViewById(R.id.buttonRequestSmsPermission);
        textSmsStatus = findViewById(R.id.textSmsStatus);

        // Load any saved phone/goal for this user
        loadUserSmsSettings();

        buttonRequestSmsPermission.setOnClickListener(v -> {
            // Save phone and goal before requesting permission
            saveUserSmsSettings();

            // Check and request permission
            checkAndRequestSmsPermission();
        });

        updateSmsStatusText();
    }

    private void loadUserSmsSettings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                GoalWeightDbHelper.TABLE_USERS,
                new String[]{
                        GoalWeightDbHelper.COLUMN_PHONE,
                        GoalWeightDbHelper.COLUMN_GOAL_WEIGHT
                },
                GoalWeightDbHelper.COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String phone = cursor.getString(
                    cursor.getColumnIndexOrThrow(GoalWeightDbHelper.COLUMN_PHONE)
            );
            float goalWeight = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(GoalWeightDbHelper.COLUMN_GOAL_WEIGHT)
            );

            if (phone != null) {
                editPhoneNumber.setText(phone);
            }
            if (goalWeight > 0) {
                editGoalWeight.setText(String.valueOf(goalWeight));
            }
        }

        cursor.close();
    }

    private void saveUserSmsSettings() {
        String phone = editPhoneNumber.getText().toString().trim();
        String goalStr = editGoalWeight.getText().toString().trim();
        Float goalWeight = null;
        if (!goalStr.isEmpty()) {
            try {
                goalWeight = Float.parseFloat(goalStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid goal weight.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GoalWeightDbHelper.COLUMN_PHONE, phone);
        if (goalWeight != null) {
            values.put(GoalWeightDbHelper.COLUMN_GOAL_WEIGHT, goalWeight);
        }

        db.update(
                GoalWeightDbHelper.TABLE_USERS,
                values,
                GoalWeightDbHelper.COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        );

        Toast.makeText(this, "Settings saved.", Toast.LENGTH_SHORT).show();
    }

    private void checkAndRequestSmsPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission already granted.", Toast.LENGTH_SHORT).show();
            updateSmsStatusText();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION
            );
        }
    }

    private void updateSmsStatusText() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            textSmsStatus.setText("Current status: SMS permission granted.");
        } else {
            textSmsStatus.setText("Current status: SMS permission NOT granted.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
            updateSmsStatusText();
        }
    }
}
