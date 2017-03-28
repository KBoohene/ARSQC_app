package com.ashesi.delalivorgbe.arsqc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
        if(item.getItemId()==R.id.action_record){
            Intent intent1 = new Intent(this, MainActivity.class);
            this.startActivity(intent1);
            return true;
        }

        else if(item.getItemId()==R.id.action_webPage){

            Intent intent2 = new Intent(this, WebDisplay.class);
            this.startActivity(intent2);
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }

    }
}
