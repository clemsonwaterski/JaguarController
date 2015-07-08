/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_tutorial_camera;

import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ros.RosCore;
import org.ros.address.InetAddressFactory;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import org.ros.node.topic.Subscriber;

import java.net.URI;

/**
 * @author ethan.rublee@gmail.com (Ethan Rublee)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity<T> extends RosActivity {

    private RosTextView rosTextView;
    private SeekBar seekBar;
    private TextView powerText;
    private double progress = 0;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;
    private RosCore rosCore;
    private URI masterUri;
    private final String TAG = "Controller";

    private RosTextView<std_msgs.String> rosTextView2;
    private Talker talker;
    private Listener listener;


    public MainActivity() {
        super("RosJaguar", "RosJaguar");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        seekBar = (SeekBar) findViewById(org.ros.android.android_tutorial_camera.R.id.seekBar);
        powerText = (TextView) findViewById(org.ros.android.android_tutorial_camera.R.id.textView);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = (progressValue - 10) / 10.0;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                powerText.setText(Double.toString(progress));
            }
        });

        rosTextView = (RosTextView) findViewById(R.id.ros_text_view);
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int numberOfCameras = Camera.getNumberOfCameras();
            final Toast toast;
            if (numberOfCameras > 1) {
                cameraId = (cameraId + 1) % numberOfCameras;
                rosCameraPreviewView.releaseCamera();
                rosCameraPreviewView.setCamera(Camera.open(cameraId));
                toast = Toast.makeText(this, "Switching cameras.", Toast.LENGTH_SHORT);
            } else {
                toast = Toast.makeText(this, "No alternative cameras to switch to.", Toast.LENGTH_SHORT);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            });
        }
        return true;
    }
    */

    public void onClickUpButton(View v) throws InterruptedException {
        Log.d("TEST", "Up: " + Double.toString(progress));
        double x = progress * 1000;
        double y = progress * -1000;
        talker.publishMessage("MMW !M " + x + " " + y);
        //talker.publishMessage("MMW !M 100 -100");
        Thread.sleep(1000);
        talker.publishMessage("MMW !M 0 0");
    }

    public void onClickLeftButton(View v) throws InterruptedException {
        Log.d("TEST", "Left: " + Double.toString(progress));
        double x = progress * -1000;
        double y = progress * -1000;
        talker.publishMessage("MMW !M " + x + " " + y);
        //talker.publishMessage("MMW !M -100 -100");
        Thread.sleep(1000);
        talker.publishMessage("MMW !M 0 0");
    }

    public void onClickRightButton(View v) throws InterruptedException {
        Log.d("TEST", "Right: " + Double.toString(progress));
        double x = progress * 1000;
        double y = progress * 1000;
        talker.publishMessage("MMW !M " + x + " " + y);
        //talker.publishMessage("MMW !M 100 100");
        Thread.sleep(1000);
        talker.publishMessage("MMW !M 0 0");
    }

    public void onClickDownButton(View v) throws InterruptedException {
        Log.d("TEST", "Down: " + Double.toString(progress));
        double x = progress * -1000;
        double y = progress * 1000;
        talker.publishMessage("MMW !M " + x + " " + y);
        //talker.publishMessage("MMW !M -100 100");
        Thread.sleep(1000);
        talker.publishMessage("MMW !M 0 0");
    }

    public void onClickStopButton(View v){
        Log.d("TEST", "EMERGENCY STOP ALL");
        talker.publishMessage("MMW !MG");
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        listener = new Listener();
        talker = new Talker();
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(rosTextView, nodeConfiguration);
        nodeMainExecutor.execute(listener, nodeConfiguration);
        nodeMainExecutor.execute(talker, nodeConfiguration);
    }
}
