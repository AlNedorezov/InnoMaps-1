package com.innopolis.maps.innomaps.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

public class SplashScreenActivity extends Activity {

    // Set Duration of the Splash Screen
    long DELAY = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove the Title Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        // Get the view from splash_screen.xml
        setContentView(R.layout.splash_screen);

        if (Utils.isNetworkAvailable(SplashScreenActivity.this)) {
            new com.innopolis.maps.innomaps.database.DBUpdater(SplashScreenActivity.this);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Executed after timer is finished (Opens MainActivity)
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, DELAY);

    }
}