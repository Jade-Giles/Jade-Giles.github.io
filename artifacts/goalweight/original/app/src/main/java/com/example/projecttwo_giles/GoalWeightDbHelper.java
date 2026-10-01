package com.example.projecttwo_giles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Database helper for GoalWeight Tracker app
public class GoalWeightDbHelper extends SQLiteOpenHelper {

    // Name and version of the database
    public static final String DATABASE_NAME = "goalweight.db";
    public static final int DATABASE_VERSION = 1;

    // ---------- USERS TABLE ----------
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_GOAL_WEIGHT = "goal_weight";
    public static final String COLUMN_PHONE = "phone_number";

    // ---------- WEIGHTS TABLE ----------
    public static final String TABLE_WEIGHTS = "weights";
    public static final String COLUMN_WEIGHT_ID = "_id";
    public static final String COLUMN_WEIGHT_USER_ID = "user_id";
    public static final String COLUMN_WEIGHT_DATE = "entry_date";
    public static final String COLUMN_WEIGHT_VALUE = "weight";

    // SQL to create users table
    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_GOAL_WEIGHT + " REAL, " +
                    COLUMN_PHONE + " TEXT);";

    // SQL to create weights table
    private static final String SQL_CREATE_WEIGHTS =
            "CREATE TABLE " + TABLE_WEIGHTS + " (" +
                    COLUMN_WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WEIGHT_USER_ID + " INTEGER NOT NULL, " +
                    COLUMN_WEIGHT_DATE + " TEXT, " +
                    COLUMN_WEIGHT_VALUE + " REAL NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_WEIGHT_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USER_ID + "));";

    // SQL to drop tables (for upgrades)
    private static final String SQL_DROP_USERS =
            "DROP TABLE IF EXISTS " + TABLE_USERS;
    private static final String SQL_DROP_WEIGHTS =
            "DROP TABLE IF EXISTS " + TABLE_WEIGHTS;

    // Constructor
    public GoalWeightDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the DB is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_WEIGHTS);
    }

    // Called when DATABASE_VERSION changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_WEIGHTS);
        db.execSQL(SQL_DROP_USERS);
        onCreate(db);
    }
}
