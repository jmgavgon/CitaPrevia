package com.jmg.citaprevia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmg.citaprevia.ProviderType;
import com.jmg.citaprevia.R;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NewAccountActivity extends AppCompatActivity {

    private DateTimeFormatter dateFormatter;
    private AwesomeValidation awesomeValidation;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        setupView();
    }

    private void setupView(){
        dateFormatter = DateTimeFormatter.ofPattern(getString(R.string.date_format), new Locale("es", "ES"));
        setupToolbar();
        setupElements();
        loadFormValidator();
    }

    private void setupToolbar(){
        setTitle(R.string.create_account);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupElements(){
        Button newAccountButton = findViewById(R.id.newAccountEmailButton);
        newAccountButton.setOnClickListener(v -> {
            if(awesomeValidation.validate()){
                EditText nameEditText = findViewById(R.id.newAccountNameEditText);
                EditText emailEditText = findViewById(R.id.newAccountEmailEditText);
                EditText passwordEditText = findViewById(R.id.newAccountPasswordEditText);
                String email = emailEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                trackUser(email, name);

                registerUser(email, password, name);
                showHome(email, ProviderType.BASIC);
            }
        });
    }

    private void trackUser(String email, String name){
        ProviderType provider = ProviderType.BASIC;
        Map<String, String> values = new HashMap<>();
        values.put(getString(R.string.firestore_field_provider), provider.name());
        values.put(getString(R.string.firestore_field_email), email);
        values.put(getString(R.string.firestore_field_name), name);
        firestore.collection(getString(R.string.firestore_collection_users)).document(email).set(values);
    }
    private void registerUser(String email, String password, String name){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, getString(R.string.new_account_welcome, name.split(" ")[0]), Toast.LENGTH_LONG).show();
                    }else{
                        showAlert();
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFormValidator(){
        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.addValidation(this,R.id.newAccountNameEditText,"[a-zA-Z\\s]+",R.string.err_not_empty);
        awesomeValidation.addValidation(this,R.id.newAccountEmailEditText,android.util.Patterns.EMAIL_ADDRESS,R.string.err_not_email);
        awesomeValidation.addValidation(this,R.id.newAccountPasswordEditText,"^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",R.string.err_not_password);
        awesomeValidation.addValidation(this,R.id.newAccountPassword2EditText,"^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",R.string.err_not_password);
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
        builder.setMessage(getString(R.string.err_signup));
        builder.setPositiveButton(getString(R.string.accept), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}