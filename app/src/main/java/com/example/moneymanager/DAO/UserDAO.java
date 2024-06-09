package com.example.moneymanager.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.moneymanager.database.DatabaseHelper;
import com.example.moneymanager.registration.PasswordHasher;

public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addUser(String username, String email, String password) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] columns = {DatabaseHelper.COLUMN_USER_ID};
        String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            throw new Exception("User with this email already exists");
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, username);
        values.put(DatabaseHelper.COLUMN_USER_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, PasswordHasher.hashPassword(password));
        db.insert(DatabaseHelper.TABLE_USERS, null, values);
        db.close();
    }

    public boolean authenticateUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_USER_ID};
        String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ? AND " + DatabaseHelper.COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, PasswordHasher.hashPassword(password)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public String getUsername(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_USER_NAME};
        String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME));
            cursor.close();
            db.close();
            return username;
        } else {
            return null;
        }
    }

    public int getUserId(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_USER_ID};
        String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
            cursor.close();
            db.close();
            return userId;
        } else {
            return -1;
        }
    }
}