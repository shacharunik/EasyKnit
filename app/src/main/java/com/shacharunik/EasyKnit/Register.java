package com.shacharunik.EasyKnit;

import static com.shacharunik.EasyKnit.DataController.uploadProfileIMG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shacharunik.EasyKnit.interfaces.ImageUploadCallback;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity implements ImageUploadCallback {
    private Button register_BTN_uploadPhoto, register_BTN_register;
    private CircleImageView register_IMG_profile;
    private EditText register_EDIT_email, register_EDIT_password;
    private TextView register_TXT_login;
    private FirebaseAuth auth;
    private Uri imageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        findViews();
        auth = FirebaseAuth.getInstance();

        register_BTN_register.setOnClickListener(view -> {
            String user = register_EDIT_email.getText().toString().trim();
            String pass = register_EDIT_password.getText().toString().trim();

            if (user.isEmpty()){
                register_EDIT_email.setError("Need to be filled");
            }
            if (pass.isEmpty()){
                register_EDIT_password.setError("Need to be filled\nPassword should be more than 5 letters");
            }
            if (pass.length() < 6){
                register_EDIT_password.setError("Password should be more than 5 letters");
            } else{
                auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser userAuth = auth.getCurrentUser();
                        ProgressDialog dialog = ProgressDialog.show(this, "", "Please wait for registration...", true);
                        uploadProfileIMG(imageUrl, userAuth.getUid(), this, dialog);
                    } else {
                        SignalGenerator.getInstance().showToast("Please correct the inputs", 2000);
                    }
                });
            }
        });

        register_BTN_uploadPhoto.setOnClickListener(v -> uploadProfile());

        register_TXT_login.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });
    }

    @Override
    public void onImageUploadSuccess(String imageUrl) {
        startActivity(new Intent(Register.this, Login.class));
    }

    @Override
    public void onImageUploadFailure(Exception exception) {
        SignalGenerator.getInstance().showToast("The picture has not been uploaded\n" +
                "please try again", 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUrl = data.getData();
            register_IMG_profile.setImageURI(imageUrl);
        }
    }

    private void uploadProfile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void findViews() {
        register_BTN_uploadPhoto = findViewById(R.id.register_BTN_uploadPhoto);
        register_BTN_register = findViewById(R.id.register_BTN_register);
        register_IMG_profile = findViewById(R.id.register_IMG_profile);
        register_EDIT_email = findViewById(R.id.register_EDIT_email);
        register_EDIT_password = findViewById(R.id.register_EDIT_password);
        register_TXT_login = findViewById(R.id.register_TXT_login);
    }
}
