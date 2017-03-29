package com.ashesi.kboohene.surfaceMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class splashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView customView = (TextView)findViewById(R.id.customText);
        Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/Jura-Regular.ttf");
        customView.setTypeface(myFont);

        Thread splashThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent mainIntent = new Intent (getApplicationContext(),home.class);
                    startActivity(mainIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splashThread.start();

    }
}
