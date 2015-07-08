package org.ros.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.ros.android.android_gingerbread_mr1.R;

public class JaguarMenu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jaguar_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.jaguar_menu, menu);
        return true;
    }

    public void onClickController(View v) {
        Log.d("JaguarController", "Load Controller Activity");
        Intent intent = new Intent(this, Controller.class);
        startActivity(intent);
    }

    public void onClickMapView(View v) {
        Log.d("JaguarController", "Load MapView Activity");
        // Intent intent = new Intent(this, MapView.class);
        // startActivity(intent);
    }

    public void onClickLIDARData(View v) {
        Log.d("JaguarController", "Load LIDARData Activity");
        // Intent intent = new Intent(this, LIDARData.class);
        // startActivity(intent);
    }

    public void onClickMessageTraffic(View v) {
        Log.d("JaguarController", "Load MessageTraffic Activity");
        // Intent intent = new Intent(this, MessageTraffic.class);
        // startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
