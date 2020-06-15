package com.hfad.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnReg;
    private TextView mSignin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registration();
    }

    private void registration() {
        mEmail = findViewById(R.id.email_reg);
        mPass = findViewById(R.id.password_reg);
        btnReg = findViewById(R.id.btn_reg);
        mSignin = findViewById(R.id.signin_here);
        db = new DatabaseHelper(this);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String pass = mPass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email required..");
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password required..");
                }

                db.insertRegistration(email, pass);

                Toast toast = Toast.makeText(RegistrationActivity.this,
                        "You have registered", Toast.LENGTH_SHORT);
                toast.show();
                Intent moveToLogin = new Intent(RegistrationActivity.this,
                        MainActivity.class);
                startActivity(moveToLogin);
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}