package com.example.projecttwo_giles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class WeightsActivity extends AppCompatActivity {

    private GoalWeightDbHelper dbHelper;
    private int userId;

    private TableLayout tableWeights;
    private EditText editDate;
    private EditText editWeight;
    private Button buttonAddWeight;
    private Button buttonSmsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);

        dbHelper = new GoalWeightDbHelper(this);

        // Get user ID from login
        userId = getIntent().getIntExtra("USER_ID", -1);

        // Hook up UI elements
        tableWeights = findViewById(R.id.tableWeights);
        editDate = findViewById(R.id.editDate);
        editWeight = findViewById(R.id.editWeight);
        buttonAddWeight = findViewById(R.id.buttonAddWeight);
        buttonSmsSettings = findViewById(R.id.buttonSmsSettings);

        // Load all weights at startup
        loadWeights();

        // Handle add button
        buttonAddWeight.setOnClickListener(v -> addWeight());

        // Handle SMS settings button
        buttonSmsSettings.setOnClickListener(v -> {
            Intent intent = new Intent(WeightsActivity.this, SmsActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }

    // Load all weight rows for this user
    private void loadWeights() {
        // remove all rows except header (index 0)
        tableWeights.removeViews(1, Math.max(0, tableWeights.getChildCount() - 1));

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                GoalWeightDbHelper.TABLE_WEIGHTS,
                null,
                GoalWeightDbHelper.COLUMN_WEIGHT_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                GoalWeightDbHelper.COLUMN_WEIGHT_DATE + " DESC"
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(GoalWeightDbHelper.COLUMN_WEIGHT_ID));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(GoalWeightDbHelper.COLUMN_WEIGHT_DATE));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(GoalWeightDbHelper.COLUMN_WEIGHT_VALUE));

            addTableRow(id, date, weight);
        }

        cursor.close();
    }

    // Dynamically add a row to the table
    private void addTableRow(int id, String date, float weight) {
        TableRow row = new TableRow(this);

        TextView textDate = new TextView(this);
        textDate.setText(date);
        textDate.setPadding(8, 8, 8, 8);
        row.addView(textDate);

        TextView textWeight = new TextView(this);
        textWeight.setText(String.valueOf(weight));
        textWeight.setPadding(8, 8, 8, 8);
        row.addView(textWeight);

        Button deleteButton = new Button(this);
        deleteButton.setText("X");
        deleteButton.setOnClickListener(v -> deleteWeight(id));
        row.addView(deleteButton);

        tableWeights.addView(row);
    }

    // Add a new entry
    private void addWeight() {
        String date = editDate.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();

        if (date.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter date and weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        float weight;
        try {
            weight = Float.parseFloat(weightStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GoalWeightDbHelper.COLUMN_WEIGHT_USER_ID, userId);
        values.put(GoalWeightDbHelper.COLUMN_WEIGHT_DATE, date);
        values.put(GoalWeightDbHelper.COLUMN_WEIGHT_VALUE, weight);

        db.insert(GoalWeightDbHelper.TABLE_WEIGHTS, null, values);

        editDate.setText("");
        editWeight.setText("");

        loadWeights();
    }

    // Delete a weight entry
    private void deleteWeight(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                GoalWeightDbHelper.TABLE_WEIGHTS,
                GoalWeightDbHelper.COLUMN_WEIGHT_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        loadWeights();
    }
}
