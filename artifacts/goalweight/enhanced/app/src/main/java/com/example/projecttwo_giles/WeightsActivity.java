package com.example.projecttwo_giles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class WeightsActivity extends AppCompatActivity {

    private GoalWeightViewModel viewModel;
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

        viewModel = new ViewModelProvider(this).get(GoalWeightViewModel.class);

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

        Cursor cursor = viewModel.getWeightsForUser(userId);

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

        if (!date.matches(("\\d{2}/\\d{2}/\\d{4}"))) {
            Toast.makeText(this, "Use MM/DD/YYYY.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            Toast.makeText(this, "Enter a valid date (MM/DD/YYYY).",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        float weight;
        try {
            weight = Float.parseFloat(weightStr);

            if (!Float.isFinite(weight) || weight <= 0) {
                Toast.makeText(this, "Enter a weight greater than zero.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = viewModel.addWeight(userId, date, weight);

        if (success) {
            editDate.setText("");
            editWeight.setText("");

            // Hide the keyboard after successfully adding a weight
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
            editWeight.clearFocus();

            Toast.makeText(
                    this, "Weight added successfully.",
                    Toast.LENGTH_SHORT
            ).show();
            loadWeights();
        } else {
            Toast.makeText(
                    this, "Unable to add weight.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Delete a weight entry
    private void deleteWeight(int id) {
        boolean success = viewModel.deleteWeight(id);

        if (success) {
            Toast.makeText(
                    this, "Weight deleted successfully.",
                    Toast.LENGTH_SHORT
            ).show();

            loadWeights();

        } else {
            Toast.makeText(this, "Unable to delete weight", Toast.LENGTH_SHORT).show();
        }
    }
}
