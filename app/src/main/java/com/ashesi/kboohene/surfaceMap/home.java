package com.ashesi.kboohene.surfaceMap;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


/**
 * @author Kwabena Boohene on 4/10/2017.
 * Welcome page of the application
 */
public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater mInflater =getMenuInflater();
        mInflater.inflate(R.menu.my_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_about_us){
            AlertDialog.Builder mBuilder= new AlertDialog.Builder(home.this);
            View mView = getLayoutInflater().inflate(R.layout.about_us_dialog,null);
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
            return true;
        }

        else{
            return super.onOptionsItemSelected(item);
        }
    }

    //Record button
    public void recordView(View v){
        if(v.getId()==R.id.button_record){
            Intent rec = new Intent(home.this,MainActivity.class);
            startActivity(rec);
        }
    }

    //Map button
    public void mapView(View v){
        if(v.getId()==R.id.button_webview){
            Intent web = new Intent(home.this,WebDisplay.class);
            startActivity(web);
        }
    }
}
