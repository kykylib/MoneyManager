package com.example.moneymanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanager.DAO.UserDAO;

public class RegistrationActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextUserName;
    private EditText editTextPassword;
    private EditText editConfirmTextPassword;
    private Button buttonSignUp;
    private TextView textView;

    private UserDAO userDAO = new UserDAO(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        editTextEmail = findViewById(R.id.editTextRegistrationEmail);
        editTextUserName = findViewById(R.id.editTextFullName);
        editTextPassword = findViewById(R.id.editTextPasswordRegistration);
        editConfirmTextPassword = findViewById(R.id.editTextConfirmPassword);

        buttonSignUp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String userName = editTextUserName.getText().toString().trim();
            String confirmPassword = editConfirmTextPassword.getText().toString().trim();

            if (password.equals(confirmPassword)) {
                try {
                    userDAO.addUser(userName,email,password);
                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(RegistrationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });

        textView = findViewById(R.id.textViewRegisterLogin);

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

}


