package com.jmg.citaprevia.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmg.citaprevia.ProviderType;
import com.jmg.citaprevia.R;

public class EmailLoginActivity extends AppCompatActivity {

    private AwesomeValidation awesomeValidation;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        setupView();
    }

    private void setupView(){
        setupNewAccountListener();
        setupLoginButtonListener();
        loadFormValidator();
    }

    private void setupLoginButtonListener(){
        Button loginButton = findViewById(R.id.loginEmailEmailButton);
        loginButton.setOnClickListener(v ->{
            if(awesomeValidation.validate()) {
                EditText emailEditText = findViewById(R.id.emailLoginEmailEditText);
                EditText passwordEditText = findViewById(R.id.emailLoginPasswordEditText);
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showHome(email, ProviderType.BASIC);
                            } else {
                                showAlert();
                            }
                        });
            }
        });

    }
    private void setupNewAccountListener(){
        TextView newAccountTextView = findViewById(R.id.registerLoginTextView);
        newAccountTextView.setOnClickListener(v -> {
            Intent intent = new Intent(EmailLoginActivity.this, NewAccountActivity.class);
            startActivity(intent);
        });
    }

    private void loadFormValidator(){
        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.addValidation(this,R.id.emailLoginEmailEditText,android.util.Patterns.EMAIL_ADDRESS,R.string.err_not_email);
        awesomeValidation.addValidation(this,R.id.emailLoginPasswordEditText,"^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",R.string.err_not_password);
    }

    private void showHome(String email, ProviderType provider){
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra(getString(R.string.email_lower_case), email);
        homeIntent.putExtra(getString(R.string.provider_lower_case), provider.name());
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(getString(R.string.err_login));
        builder.setPositiveButton(getString(R.string.accept), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}