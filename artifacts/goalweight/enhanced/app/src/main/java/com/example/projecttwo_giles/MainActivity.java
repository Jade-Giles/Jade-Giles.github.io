package com.example.projecttwo_giles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.lifecycle.ViewModelProvider;



public class MainActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button buttonLogin;
    private Button buttonCreateAccount;

    private GoalWeightViewModel viewModel;

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

        // Connect the UI to the ViewModel
        viewModel = new ViewModelProvider(this).get(GoalWeightViewModel.class);

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
            Toast.makeText(this,
                    "Please enter a username and password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewModel.usernameExists(username)) {
            Toast.makeText(this,
                    "Username already exists. Please choose another.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        long newId = viewModel.createUser(username, password);

        if (newId == -1) {
            Toast.makeText(this,
                    "Error creating account.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Account created! You can log in now.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Log in an existing user
    private void loginUser() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this,
                    "Please enter your username and password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = viewModel.authenticateUser(username, password);

        if (userId != -1) {
            Toast.makeText(this,
                    "Login successful! User ID: " + userId,
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, WeightsActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    "Invalid username or password.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
