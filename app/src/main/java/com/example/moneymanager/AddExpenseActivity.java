package com.example.moneymanager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanager.DAO.UserDAO;
import com.example.moneymanager.database.DatabaseHelper;
import com.example.moneymanager.database.SessionOfCurrentUser;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText amountEditText;
    private Spinner categorySpinner;
    private Button addCategoryButton;
    private Button saveExpenseButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        addCategoryButton = findViewById(R.id.addCategoryButton);
        saveExpenseButton = findViewById(R.id.saveExpenseButton);
        dbHelper = new DatabaseHelper(this);

        loadCategories();

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddExpenseActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        saveExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    @SuppressLint("Range")
    private void loadCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, new String[]{DatabaseHelper.COLUMN_CATEGORY_NAME}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void saveExpense() {
        String amountText = amountEditText.getText().toString().trim();
        if (amountText.isEmpty()) {
            Toast.makeText(AddExpenseActivity.this, "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        float amount = Float.parseFloat(amountText);
        String category = categorySpinner.getSelectedItem().toString();
        int categoryId = getCategoryIdByName(category);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, amount);
        values.put(DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID, categoryId);
        values.put(DatabaseHelper.COLUMN_EXPENSE_USER_ID, getUserId());

        long newRowId = db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
        if (newRowId != -1) {
            Toast.makeText(AddExpenseActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(AddExpenseActivity.this, "Error adding expense", Toast.LENGTH_SHORT).show();
        }
    }

    private int getCategoryIdByName(String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, new String[]{DatabaseHelper.COLUMN_CATEGORY_ID},
                DatabaseHelper.COLUMN_CATEGORY_NAME + "=?", new String[]{categoryName}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int categoryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_ID));
            cursor.close();
            return categoryId;
        }
        return -1;
    }

    private int getUserIdByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COLUMN_USER_ID},
                DatabaseHelper.COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
            cursor.close();
            return userId;
        }
        return -1;
    }

    private int getUserId() {
        UserDAO userDAO = new UserDAO(this);
        return userDAO.getUserId(SessionOfCurrentUser.getUserMail());
    }
}


