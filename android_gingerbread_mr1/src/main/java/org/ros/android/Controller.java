package org.ros.android;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Preconditions;

import org.ros.RosCore;
import org.ros.android.android_gingerbread_mr1.R;

import java.net.URI;
import java.net.URISyntaxException;


public class Controller extends Activity {

    private SeekBar seekBar;
    private TextView powerText;
    private double progress = 0;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;
    private RosCore rosCore;
    private URI masterUri;
    private final String TAG = "Controller";

    /* Only for RosActivity
    public Controller() {
        super("Controller", "controller");
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller);
        try {
            masterUri = new URI("http://localhost:11311/");
        }
        catch (URISyntaxException e) {
            Log.e("TEST", "ERROR: INVALID masterURI in Controller.java");
        }


        // Initialize ROS components
        /*
        **START1**
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();
        int wifiLockType = WifiManager.WIFI_MODE_FULL;
        try {
            wifiLockType = WifiManager.class.getField("WIFI_MODE_FULL_HIGH_PERF").getInt(null);
        } catch (Exception e) {
            // We must be running on a pre-Honeycomb device.
            Log.w(TAG, "Unable to acquire high performance wifi lock.");
        }
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock(wifiLockType, TAG);
        wifiLock.acquire();

        setResult(RESULT_OK, createNewMasterIntent(false));
        **END1**
        */

        /*
        finish();
        */
        /* ROS SERVICE
        rosCore = RosCore.newPublic(11311);
        rosCore.start();
        try {
            rosCore.awaitStart();
        } catch (Exception e) {
            throw new RosRuntimeException(e);
        }
        masterUri = rosCore.getUri();
        */
    }

    public Intent createNewMasterIntent (Boolean isPrivate) {
        Intent intent = new Intent();
        intent.putExtra("NEW_MASTER", true);
        intent.putExtra("ROS_MASTER_PRIVATE", isPrivate);
        return intent;
    }

    public void newMasterButtonClicked(View unused) {
        setResult(RESULT_OK, createNewMasterIntent(false));
        finish();
    }

    public void newPrivateMasterButtonClicked(View unused) {
        setResult(RESULT_OK, createNewMasterIntent(true));
        finish();
    }

    public void cancelButtonClicked(View unused) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void shutdown() {
        //signalOnShutdown();
        // NOTE(damonkohler): This may be called multiple times. Shutting down a
        // NodeMainExecutor multiple times is safe. It simply calls shutdown on all
        // NodeMains.
        //nodeMainExecutor.shutdown();
        if (rosCore != null) {
            rosCore.shutdown();
        }
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
        /* Service only
        stopForeground(true);
        stopSelf();
         */

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // If the Barcode Scanner returned a string then display that string.
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String scanResultFormat = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Preconditions.checkState(scanResultFormat.equals("TEXT_TYPE") || scanResultFormat.equals("QR_CODE"));
                String contents = intent.getStringExtra("SCAN_RESULT");
                try{
                    masterUri = new URI(contents);
                } catch (URISyntaxException e) {
                    Toast.makeText(Controller.this, "Invalid URI", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    /* not needed if using a service
    @Override
    public void onDestroy() {
        shutdown();
        super.onDestroy();
    }
    */

    /* Only for RosActivity
    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeMain nodeMain = new JaguarRosNode(this);
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(nodeMain, nodeConfiguration);
    }
    */

    public URI getMasterUri() {
        return masterUri;
    }

    public void setMasterUri(URI uri) {
        masterUri = uri;
    }

    public void onClickStopButton(View v){
        Log.d("JaguarController", "START ROSCORE SERVICE");
        setResult(RESULT_OK, createNewMasterIntent(false));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.controller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
