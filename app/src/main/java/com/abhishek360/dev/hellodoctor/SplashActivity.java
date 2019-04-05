package com.abhishek360.dev.hellodoctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity
{
    private static int SPLASH_TIME_OUT=1000;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences(LoginActivity.spKey,MODE_PRIVATE);
        final String fullName =sharedPreferences.getString(LoginActivity.spFullNameKey,null);
        final String email =sharedPreferences.getString(LoginActivity.spEmailKey,null);
        final  boolean isLoggedIn = sharedPreferences.getBoolean(LoginActivity.spIsLoggedIn,false);
        final boolean isDoc = sharedPreferences.getBoolean(LoginActivity.spIsDoc,false);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (isLoggedIn)
                {
                    if(isDoc)
                    {
                        Intent in = new Intent(SplashActivity.this,ChatHistoryActivity.class);
                        in.putExtra("name",fullName);
                        in.putExtra("email",email);
                        startActivity(in);
                        finish();
                    }
                    else
                    {
                        Intent in = new Intent(getApplicationContext(),MainActivity.class);
                        in.putExtra("name",fullName);
                        in.putExtra("email",email);
                        startActivity(in);
                        finish();
                    }
                }
                else {

                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }

            }
        },SPLASH_TIME_OUT);
    }
}
