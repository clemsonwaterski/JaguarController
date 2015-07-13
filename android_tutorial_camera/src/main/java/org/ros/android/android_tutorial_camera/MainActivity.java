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
import android.widget.ImageView;
import org.ros.node.NodeMainExecutor;

import org.ros.node.topic.Subscriber;

import java.net.URI;

/**
 * @author ethan.rublee@gmail.com (Ethan Rublee)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity<T> extends RosActivity {

    TextView location;
    ImageView touchable;
    float x;
    float y;
    float maxRadius;
    int jaguarX;
    int jaguarY;
    float theta;
    float radius;
    float maxPower;

    private Talker talker;
    private Listener listener;


    public MainActivity() {
        super("RosJaguar", "RosJaguar");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = (TextView)findViewById(R.id.location);
        touchable = (ImageView)findViewById(R.id.touchable);
        touchable.setOnTouchListener(MyOnTouchListener);
        maxPower=565;


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

    View.OnTouchListener MyOnTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                talker.publishMessage("MMW !M 0 0");
            }
            else {
                maxRadius = (float) Math.sqrt(Math.pow(view.getWidth() / 2, 2) + Math.pow(view.getWidth() / 2, 2));
                x = (event.getX() - view.getWidth() / 2) / maxRadius;
                y = -(event.getY() - view.getHeight() / 2) / maxRadius;

                theta = (float) ((((float) Math.atan2(y, -x) * 180 / Math.PI) + 180) + 90) % 360 * (float) Math.PI / 180;
                radius = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

                location.setText("Coordinates: (" + radius + ", " + theta + ")");

                //get Jaguar commands

                jaguarX = (int) ((int) maxPower * Math.cos(theta - 45) * radius);
                jaguarY = (int) ((int) maxPower * Math.sin(theta - 45) * radius);

                //location.setText("Coordinates: ("+jaguarX+", "+jaguarY+")");

                talker.publishMessage("MMW !M " + jaguarX + " " + jaguarY);
            }


            return true;
        }



    };
/*
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
    */
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
        //nodeMainExecutor.execute(rosTextView, nodeConfiguration);
        nodeMainExecutor.execute(listener, nodeConfiguration);
        nodeMainExecutor.execute(talker, nodeConfiguration);
    }
}
