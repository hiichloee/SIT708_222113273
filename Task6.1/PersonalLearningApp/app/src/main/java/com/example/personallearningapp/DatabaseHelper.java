
package com.example.personallearningapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "learning_app.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_INTERESTS = "interests";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "email TEXT, " +
                "password TEXT, " +
                "phone TEXT, " +
                "avatar_uri TEXT)";

        String createInterestsTable = "CREATE TABLE interests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "interest TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createInterestsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS interests");
        onCreate(db);
    }

    public boolean registerUser(String username, String email, String password, String phone, String avatarUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        values.put("avatar_uri", avatarUri);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public Cursor loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username=? AND password=?", new String[]{username, password});
    }

    public void clearAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.delete(TABLE_INTERESTS, null, null);
    }

    public List<String> getInterestsForUser(int userId) {
        List<String> interests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 通过 user_id 查 interests
        Cursor cursor = db.rawQuery("SELECT interest FROM interests WHERE user_id=?", new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            interests.add(cursor.getString(0));
        }
        cursor.close();
        return interests;
    }

}