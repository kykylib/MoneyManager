package com.example.moneymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanager.DAO.CategoryDAO;
import com.example.moneymanager.database.DatabaseHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText categoryNameEditText;
    private Button pickColorButton;
    private Button saveCategoryButton;
    private int selectedColor = Color.GRAY;
    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        categoryNameEditText = findViewById(R.id.categoryNameEditText);
        pickColorButton = findViewById(R.id.pickColorButton);
        saveCategoryButton = findViewById(R.id.saveCategoryButton);
        categoryDAO = new CategoryDAO(this);

        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });

        saveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });
    }

    private void showColorPickerDialog() {
        final String[] colors = {"Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "Gray", "Black", "White", "Purple"};
        final int[] colorValues = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GRAY, Color.BLACK, Color.WHITE, Color.parseColor("#800080")};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a color")
                .setItems(colors, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedColor = colorValues[which];
                        pickColorButton.setBackgroundColor(selectedColor);
                    }
                });
        builder.create().show();
    }

    private void saveCategory() {
        String categoryName = categoryNameEditText.getText().toString().trim();
        if (categoryName.isEmpty()) {
            Toast.makeText(AddCategoryActivity.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAdded = categoryDAO.addCategory(categoryName, selectedColor);
        if (isAdded) {
            Toast.makeText(AddCategoryActivity.this, "Category added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(AddCategoryActivity.this, "Category already exists", Toast.LENGTH_SHORT).show();
        }
    }
}


