package edu.osu.ride;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mUsernameField;
    private Button mUpdateButton;
    private Button mDeleteButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mEmailField = findViewById(R.id.editEmail);
        mPasswordField = findViewById(R.id.editPassword);
        mUsernameField = findViewById(R.id.editUsername);

        mUpdateButton = findViewById(R.id.updateSettingsButton);
        mUpdateButton.setOnClickListener(this);

        mDeleteButton = findViewById(R.id.delete_Button);
        mDeleteButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }


    public void editSettings() {
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        String username = mUsernameField.getText().toString().trim();

        if (!email.isEmpty()) {
            mAuth.getCurrentUser().updateEmail(email);
        }

        if (!password.isEmpty()) {
            mAuth.getCurrentUser().updatePassword(password);
        }

        if (!username.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_Button:
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(mAuth.getCurrentUser().getUid()).removeValue();

                mAuth.getCurrentUser().delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.updateSettingsButton:
                editSettings();
                Toast.makeText(SettingsActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    }
