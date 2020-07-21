package com.hfad.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnReg;
    private TextView mSignin;
    private EditText mPassConfirm;

    private ProgressDialog mDialog;

    // Firebase

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        registration();
    }

    private void registration() {
        mEmail = findViewById(R.id.email_reg);
        mPass = findViewById(R.id.password_reg);
        btnReg = findViewById(R.id.btn_reg);
        mSignin = findViewById(R.id.signin_here);
        mPassConfirm = findViewById(R.id.password_reg_confirm);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String pass = mPass.getText().toString();
                String passConfirm = mPassConfirm.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email required..");
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password required..");
                }

                if (pass.length() < 8) {
                    mPass.setError("Password must be at least 8 characters long.");
                }

                if (!passConfirm.equals(pass)) {
                    mPassConfirm.setError("Your password and the confirmation password do not match.");
                }

                mDialog.setMessage("Processing..");
                mDialog.show();


                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Registration Complete", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),
                                    HomeActivity.class));
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Registration Field..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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