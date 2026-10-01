package com.example.projecttwo_giles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GoalWeightRepository {

    private final GoalWeightDbHelper dbHelper;

    public GoalWeightRepository(Context context) {
        dbHelper = new GoalWeightDbHelper(context.getApplicationContext());
    }

    // Creates a new user account.
    // Returns the new user ID, or -1 if creation fails.
    public long createUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GoalWeightDbHelper.COLUMN_USERNAME, username);
        values.put(GoalWeightDbHelper.COLUMN_PASSWORD, PasswordUtils.hashPassword(password));

        return db.insert(
                GoalWeightDbHelper.TABLE_USERS,
                null,
                values
        );
    }

    // Checks whether a username already exists.
    public boolean usernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                GoalWeightDbHelper.TABLE_USERS,
                new String[]{GoalWeightDbHelper.COLUMN_USER_ID},
                GoalWeightDbHelper.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        )) {
            return cursor.moveToFirst();
        }
    }

    // Returns the user's ID when the password is correct.
    // Existing plaintext test passwords are upgraded to hashes after a successful login.
    public int authenticateUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try (Cursor cursor = db.query(
                GoalWeightDbHelper.TABLE_USERS,
                new String[]{GoalWeightDbHelper.COLUMN_USER_ID,
                    GoalWeightDbHelper.COLUMN_PASSWORD},
                GoalWeightDbHelper.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        )) {
            if (!cursor.moveToFirst()) {
                return -1;
            }
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(GoalWeightDbHelper.COLUMN_USER_ID));

            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(GoalWeightDbHelper.COLUMN_PASSWORD));

            //Accounts created with the new password hashing code
            if (PasswordUtils.isHashed(storedPassword)) {
                return PasswordUtils.verifyPassword(password, storedPassword)
                        ? userId
                        : -1;
            }

            // Older test accounts still have plaintext passwords
            if (!password.equals(storedPassword)) {
                return -1;
            }

            //Correct legacy password ; replace it with a hash before signing in
            ContentValues values = new ContentValues();
            values.put(GoalWeightDbHelper.COLUMN_PASSWORD, PasswordUtils.hashPassword(password));

            int rowsUpdated = db.update(GoalWeightDbHelper.TABLE_USERS, values,
                    GoalWeightDbHelper.COLUMN_USER_ID + " = ? AND "
                                    + GoalWeightDbHelper.COLUMN_PASSWORD + " = ?",
                    new String[]{String.valueOf(userId), storedPassword});

            return rowsUpdated == 1 ? userId : -1;
        }
    }

    // Retrieve all weight entries for a specific user
    public Cursor getWeightsForUser(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.query(
                GoalWeightDbHelper.TABLE_WEIGHTS,
                null,
                GoalWeightDbHelper.COLUMN_WEIGHT_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                GoalWeightDbHelper.COLUMN_WEIGHT_DATE + " DESC"
        );
    }

    //Add a new weight entry
    // Returns true only if the database insert succeeds
    public boolean addWeight(int userId, String date, float weight) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GoalWeightDbHelper.COLUMN_WEIGHT_USER_ID, userId);
        values.put(GoalWeightDbHelper.COLUMN_WEIGHT_DATE, date);
        values.put(GoalWeightDbHelper.COLUMN_WEIGHT_VALUE, weight);

        long result = db.insert(
                GoalWeightDbHelper.TABLE_WEIGHTS,
                null,
                values
        );

        return result != -1;
    }

    // Delete a weight entry
    // Returns true only if at least one row was deleted
    public boolean deleteWeight(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(
                GoalWeightDbHelper.TABLE_WEIGHTS,
                GoalWeightDbHelper.COLUMN_WEIGHT_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        return rowsDeleted > 0;
    }

    // Retrieve the saved SMS settings for a user
    public Cursor getUserSmsSettings(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.query(
                GoalWeightDbHelper.TABLE_USERS,
                new String[]{
                        GoalWeightDbHelper.COLUMN_PHONE,
                        GoalWeightDbHelper.COLUMN_GOAL_WEIGHT
                },
                GoalWeightDbHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );
    }

    // Save the user's phone number and goal weight.
    // Returns true only if the datbase update succeeds
    public boolean updateUserSmsSettings(int userId, String phone, Float goalWeight) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GoalWeightDbHelper.COLUMN_PHONE, phone);

        if (goalWeight != null) {
            values.put(GoalWeightDbHelper.COLUMN_GOAL_WEIGHT, goalWeight);
        }

        int rowsUpdated = db.update(
                GoalWeightDbHelper.TABLE_USERS, values,
                GoalWeightDbHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rowsUpdated > 0;
    }
}