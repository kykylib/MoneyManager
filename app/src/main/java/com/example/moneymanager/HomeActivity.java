package com.example.moneymanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanager.DAO.UserDAO;
import com.example.moneymanager.database.DatabaseHelper;
import com.example.moneymanager.database.SessionOfCurrentUser;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeText;
    private PieChart pieChart;
    private TextView totalExpenses;
    private Button addExpenseButton;
    private Button logoutButton;
    private DatabaseHelper dbHelper;

    private static final int ADD_EXPENSE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText = findViewById(R.id.welcomeTextView);
        pieChart = findViewById(R.id.pieChart);
        totalExpenses = findViewById(R.id.totalExpensesTextView);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        logoutButton = findViewById(R.id.logoutButton);
        dbHelper = new DatabaseHelper(this);

        String userEmail = SessionOfCurrentUser.getUserMail();
        String userName = new UserDAO(this).getUsername(userEmail);
        welcomeText.setText("Welcome, " + userName);

        loadExpensesData();

        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, ADD_EXPENSE_REQUEST_CODE); // Используем startActivityForResult
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionOfCurrentUser.saveUserMail(null);
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EXPENSE_REQUEST_CODE && resultCode == RESULT_OK) {
            loadExpensesData();
        }
    }

    private void loadExpensesData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String userEmail = SessionOfCurrentUser.getUserMail();
        int userId = getUserIdByEmail(userEmail);

        String[] categoryColumns = {
                DatabaseHelper.COLUMN_CATEGORY_ID,
                DatabaseHelper.COLUMN_CATEGORY_NAME,
                DatabaseHelper.COLUMN_CATEGORY_COLOR
        };

        String[] expenseColumns = {
                DatabaseHelper.COLUMN_EXPENSE_ID,
                DatabaseHelper.COLUMN_EXPENSE_AMOUNT,
                DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID
        };

        Cursor categoryCursor = db.query(DatabaseHelper.TABLE_CATEGORIES, categoryColumns, null, null, null, null, null);
        Cursor expenseCursor = db.query(DatabaseHelper.TABLE_EXPENSES, expenseColumns,
                DatabaseHelper.COLUMN_EXPENSE_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);

        float total = 0;
        Map<String, Float> categoryExpenses = new HashMap<>();
        Map<String, Integer> categoryColors = new HashMap<>();

        if (expenseCursor != null && expenseCursor.getCount() > 0) {
            while (expenseCursor.moveToNext()) {
                float amount = expenseCursor.getFloat(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT));
                int categoryId = expenseCursor.getInt(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID));
                total += amount;

                if (categoryCursor.moveToFirst()) {
                    do {
                        int id = categoryCursor.getInt(categoryCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                        if (id == categoryId) {
                            String categoryName = categoryCursor.getString(categoryCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                            int categoryColor = categoryCursor.getInt(categoryCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_COLOR)); // Получаем цвет категории
                            categoryColors.put(categoryName, categoryColor);

                            if (categoryExpenses.containsKey(categoryName)) {
                                categoryExpenses.put(categoryName, categoryExpenses.get(categoryName) + amount);
                            } else {
                                categoryExpenses.put(categoryName, amount);
                            }
                            break;
                        }
                    } while (categoryCursor.moveToNext());
                }
            }
        } else {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(1, "No Expenses"));
            PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
            dataSet.setColor(Color.GRAY);
            dataSet.setDrawValues(false);
            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.invalidate();
            totalExpenses.setText("Total Expenses: $0.00");
            categoryCursor.close();
            if (expenseCursor != null) expenseCursor.close();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryExpenses.entrySet()) {
            float percentage = (entry.getValue() / total) * 100;
            entries.add(new PieEntry(percentage, entry.getKey()));
            colors.add(categoryColors.get(entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(18f);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

        totalExpenses.setText("Total Expenses: $" + String.format("%.2f", total));

        categoryCursor.close();
        expenseCursor.close();
    }


    private int getUserIdByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_USER_ID};
        String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID));
            cursor.close();
            return userId;
        } else {
            return -1;
        }
    }
}




