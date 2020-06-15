package com.hfad.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnLogin;
    private TextView mForgetPassword;
    private TextView mSignupHere;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        loginDetails();

        db = new DatabaseHelper(this);
    }

    private void loginDetails() {
        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        mForgetPassword = findViewById(R.id.forget_password);
        mSignupHere = findViewById(R.id.signup_reg);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String pass = mPass.getText().toString();
                Boolean res = db.checkUser(email, pass);

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email required..");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Password required..");
                    return;
                }

                if (res == true) {
                    Toast toast = Toast.makeText(MainActivity.this,
                            "Successfully logged in", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent(MainActivity.this,
                            HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this,
                            "Login error", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        // Registration activity

        mSignupHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

    }
}