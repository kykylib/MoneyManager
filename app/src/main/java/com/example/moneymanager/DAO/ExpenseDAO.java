package com.example.moneymanager.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.moneymanager.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {
    private DatabaseHelper dbHelper;

    public ExpenseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addExpense(int userId, int categoryId, double amount, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EXPENSE_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID, categoryId);
        values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, amount);
        values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, date);
        db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
        db.close();
    }

    public List<Expense> getUserExpenses(int userId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_EXPENSE_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_EXPENSES, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int expenseId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_ID));
                @SuppressLint("Range") int categoryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_AMOUNT));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_DATE));
                expenses.add(new Expense(expenseId, userId, categoryId, amount, date));
            }
            cursor.close();
        }
        db.close();
        return expenses;
    }

    public List<Expense> getExpensesByUser(String username) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT e.*, c." + DatabaseHelper.COLUMN_CATEGORY_NAME + " FROM " + DatabaseHelper.TABLE_EXPENSES + " e " +
                "JOIN " + DatabaseHelper.TABLE_USERS + " u ON e." + DatabaseHelper.COLUMN_EXPENSE_USER_ID + " = u." + DatabaseHelper.COLUMN_USER_ID + " " +
                "JOIN " + DatabaseHelper.TABLE_CATEGORIES + " c ON e." + DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID + " = c." + DatabaseHelper.COLUMN_CATEGORY_ID + " " +
                "WHERE u." + DatabaseHelper.COLUMN_USER_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int expenseId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_ID));
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_USER_ID));
                @SuppressLint("Range") int categoryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_AMOUNT));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_DATE));
                @SuppressLint("Range") String categoryName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME));

                expenses.add(new Expense(expenseId, userId, categoryId, amount, date, categoryName));
            }
            cursor.close();
        }
        db.close();
        return expenses;
    }
}


