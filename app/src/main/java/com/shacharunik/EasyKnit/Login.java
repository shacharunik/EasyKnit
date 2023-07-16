package com.shacharunik.EasyKnit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login  extends AppCompatActivity {
    private EditText login_EDIT_email, login_EDIT_password;
    private Button login_BTN_login, login_BTN_signUP;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        findViews();
        auth = FirebaseAuth.getInstance();

        login_BTN_login.setOnClickListener(v -> {

            String email = login_EDIT_email.getText().toString().trim();
            String password = login_EDIT_password.getText().toString().trim();

            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!password.isEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();
                            }).addOnFailureListener(e -> SignalGenerator.getInstance().showToast("Incorrect email or password", 1500));
                } else {
                    login_EDIT_password.setError("Need to be filled");
                }
            } else if (email.isEmpty()) {
                login_EDIT_email.setError("Need to be filled");
            } else {
                login_EDIT_email.setError("Need to be filled");
            }
        });

        login_BTN_signUP.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));


    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void findViews() {
        login_EDIT_email = findViewById(R.id.login_EDIT_email);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_BTN_signUP = findViewById(R.id.login_BTN_signUP);
        login_EDIT_password = findViewById(R.id.login_EDIT_password);
    }

}
