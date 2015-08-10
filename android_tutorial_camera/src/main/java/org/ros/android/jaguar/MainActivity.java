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

package org.ros.android.jaguar;

import android.location.Location;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import android.widget.ImageView;
import org.ros.node.NodeMainExecutor;

import java.util.ArrayList;
import java.util.List;

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
    Location currentLocation;

    private Talker talker;
    private Listener listener;
    private GPSThread gpsThread;
    private ROSServiceManager jaguarManager;


    public MainActivity() {
        super("RosJaguar", "RosJaguar");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ROS", "ROS MainActivity onCreate");
        setContentView(R.layout.activity_main);
        location = (TextView)findViewById(R.id.location);
        touchable = (ImageView)findViewById(R.id.touchable);
        touchable.setOnTouchListener(MyOnTouchListener);
        maxPower=1000;

        jaguarManager = new ROSServiceManager(this, new ROSServiceManager.OnConnectedListener() {
            @Override public void onConnected() {
                Log.d("ROS", "Jaguar connected - adding reporter");
                jaguarManager.add(jaguarServiceReporter);

            }
            @Override public void onDisconnected() {
                Log.d("ROS", "Jaguar disconnected - removing reporter");
                jaguarManager.remove(jaguarServiceReporter);
            }
        });


    }

    private List<ROSServiceReporter> reporters = new ArrayList<ROSServiceReporter>();

    private class GPSThread extends Thread {

        @Override public void run() {
            while(!isInterrupted()) {
                List<ROSServiceReporter> targets;
                synchronized (reporters) {
                    targets = new ArrayList<ROSServiceReporter>(reporters);
                }
                for(ROSServiceReporter rosServiceReporter : targets) {
                    //This is the part where GPS is reported
                    //do something with currentLocation;
                }
            }
            Log.d("ROS", "ROS Service Thread Interrupted");
        }
    }




    public ROSService.Stub rosBinder = new ROSService.Stub() {
        @Override public void add(ROSServiceReporter reporter) throws RemoteException {
            synchronized (reporters) {
                reporters.add(reporter);
            }
        }

        @Override public void remove(ROSServiceReporter reporter) throws RemoteException {
            synchronized (reporters) {
                reporters.remove(reporter);
            }
        }

        @Override
        public void publishCommand(String commandString) throws RemoteException {
            Log.d("ROS","In Activity Stub, Sending Motor Command");
            if(commandString==null)
                Log.d("ROS","Command String Null");
            if(talker==null)
                 Log.d("ROS", "talker is Null");
            talker.publishMessage(commandString);
        }
    };


    private ROSServiceReporter jaguarServiceReporter = new ROSServiceReporter.Stub() {
        @Override
        public void reportGPS(Location location) throws RemoteException {
            currentLocation=location;
        }
    };


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
               // maxRadius = (float) Math.sqrt(Math.pow(view.getWidth() / 2, 2) + Math.pow(view.getWidth() / 2, 2));
                x = (event.getX() - view.getWidth() / 2) / view.getWidth();
                y = -(event.getY() - view.getHeight() / 2) / view.getHeight();

                theta = (float) ((((float) Math.atan2(y, -x) * 180 / Math.PI) + 180) + 90) % 360 * (float) Math.PI / 180;
                radius = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))*2;

                location.setText("Coordinates: (" + radius + ", " + theta*180/Math.PI + ")");

                //get Jaguar commands

                if(radius>1)
                    radius=1;

                jaguarX = (int) ((int) maxPower * Math.cos(theta - 45*Math.PI/180) * radius);
                jaguarY = (int) ((int) maxPower * Math.sin(theta - 45*Math.PI/180) * radius);

                //location.setText("Coordinates: ("+jaguarX+", "+jaguarY+")");

                talker.publishMessage("MMW !M " + jaguarX + " " + jaguarY);
            }


            return true;
        }



    };


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
