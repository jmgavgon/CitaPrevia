package com.jmg.citaprevia.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmg.citaprevia.ProviderType;
import com.jmg.citaprevia.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager fbCallbackManager = CallbackManager.Factory.create();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadSession();
        setupView();
    }

    private void setupView(){
        setupButtonResources();
        setupElementsListeners();
        hideActionBar();
    }

    private void setupElementsListeners(){
        setupNewAccountListener();
        setupGoogleButtonListener();
        setupFacebookButtonListener();
        setupEmailButtonListener();
    }

    private void setupEmailButtonListener(){
        Button emailButton = findViewById(R.id.loginEmailButton);
        emailButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, EmailLoginActivity.class);
            startActivity(intent);
        });
    }

    private void setupGoogleButtonListener(){
        Button googleButton = findViewById(R.id.loginGoogleButton);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                                FirebaseAuth.getInstance().signInWithCredential(credential);
                                if (task.isSuccessful()) {
                                    String email = Objects.requireNonNull(account.getEmail());
                                    String name = Objects.requireNonNull(account.getDisplayName());
                                    Map<String, String> values = new HashMap<>();
                                    values.put(getString(R.string.firestore_field_provider), ProviderType.GOOGLE.name());
                                    values.put(getString(R.string.firestore_field_email), email);
                                    values.put(getString(R.string.firestore_field_name), name);
                                    firestore.collection(getString(R.string.firestore_collection_users)).document(email).set(values);
                                    showHome(email, ProviderType.GOOGLE);
                                } else {
                                    showAlert();
                                }
                            }
                        } catch (ApiException e) {
                            showAlert();
                        }
                    }
                });

        googleButton.setOnClickListener(v -> {
            GoogleSignInOptions googleConfBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient googleClient = GoogleSignIn.getClient(this, googleConfBuilder);
            //logout current user logged in with google. Useful when the user has more than one google account
            googleClient.signOut();
            activityResultLauncher.launch(googleClient.getSignInIntent());
        });
    }

    private void setupFacebookButtonListener(){
        fbCallbackManager = CallbackManager.Factory.create();
        Button facebookButton = findViewById(R.id.loginFacebookButton);

        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();
                AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()){
                        String email = Objects.requireNonNull(task.getResult().getUser()).getEmail();
                        String name = Objects.requireNonNull(task.getResult().getUser()).getDisplayName();
                        Map<String, String> values = new HashMap<>();
                        values.put(getString(R.string.firestore_field_provider), ProviderType.FACEBOOK.name());
                        values.put(getString(R.string.firestore_field_email), email);
                        values.put(getString(R.string.firestore_field_name), name);
                        firestore.collection(getString(R.string.firestore_collection_users)).document(Objects.requireNonNull(email)).set(values);
                        showHome(email,ProviderType.FACEBOOK);
                    }else{
                        showAlert();
                    }
                });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(@NonNull FacebookException error) {
                showAlert();
            }
        });

        facebookButton.setOnClickListener(v -> LoginManager.getInstance()
                .logInWithReadPermissions(this, Collections.singletonList(getString(R.string.email_lower_case))));
    }

    private void setupButtonResources(){
        Rect rect = new Rect(0, 0, 80, 80);
        Button googleLogin = findViewById(R.id.loginGoogleButton);
        Drawable googleDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.google,null);
        assert googleDrawable != null;
        googleDrawable.setBounds(rect);
        googleLogin.setCompoundDrawables(googleDrawable, null, null, null);

        Button facebookLogin = findViewById(R.id.loginFacebookButton);
        Drawable facebookDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.facebook,null);
        assert facebookDrawable != null;
        facebookDrawable.setBounds(rect);
        facebookLogin.setCompoundDrawables(facebookDrawable, null, null, null);
    }
    private void setupNewAccountListener(){
        TextView newAccountTextView = findViewById(R.id.registerTextView);
        newAccountTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, NewAccountActivity.class);
            startActivity(intent);
        });
    }

    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
            actionBar.hide();
    }

    private void showHome(String email, ProviderType provider){
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra(getString(R.string.email_lower_case), email);
        homeIntent.putExtra(getString(R.string.provider_lower_case), provider.name());
        startActivity(homeIntent);
    }

    private void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(getString(R.string.err_login));
        builder.setPositiveButton(getString(R.string.accept), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadSession(){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = preferences.getString(getString(R.string.email_lower_case), null);
        String provider = preferences.getString(getString(R.string.provider_lower_case), null);

        if(email != null && provider != null){
            showHome(email, ProviderType.valueOf(provider));
        }
    }
}