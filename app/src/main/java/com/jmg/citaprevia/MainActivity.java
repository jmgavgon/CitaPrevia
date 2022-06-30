package com.jmg.citaprevia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmg.citaprevia.activity.LoginActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        animateSplash();


    }

    private void animateSplash(){
        Animation splashLogoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo);
        Animation splashTitleAnim = AnimationUtils.loadAnimation(this, R.anim.splash_title);

        TextView splashTitleTextView = findViewById(R.id.splashTextView);
        ImageView splashLogoImageView = findViewById(R.id.splashImageView);

        splashLogoImageView.setAnimation(splashLogoAnim);
        splashTitleTextView.setAnimation(splashTitleAnim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 2000);

    }
}