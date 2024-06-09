package com.example.moneymanager.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.moneymanager.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private DatabaseHelper dbHelper;

    public CategoryDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean addCategory(String name, int color) {
        if (!isCategoryExists(name)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, name);
            values.put(DatabaseHelper.COLUMN_CATEGORY_COLOR, color);
            db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
            db.close();
            return true;
        }
        return false;
    }

    @SuppressLint("Range")
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));
                int color = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_COLOR));
                categories.add(new Category(id, name, color));
            }
            cursor.close();
        }
        db.close();
        return categories;
    }
    private boolean isCategoryExists(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES + " WHERE LOWER(" +
                DatabaseHelper.COLUMN_CATEGORY_NAME + ") = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name.toLowerCase()});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}



