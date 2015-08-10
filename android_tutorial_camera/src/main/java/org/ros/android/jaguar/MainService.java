package org.ros.android.jaguar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    private String tag = "ROS";
    private GPSThread gpsThread;
    private ROSServiceManager jaguarManager;
    Location currentLocation;
    private Talker talker;
    private Listener listener;



    @Override
    public IBinder onBind(Intent intent) {
        Log.d(tag, "ROS Service onBind");
        gpsThread = new GPSThread();
        gpsThread.start();
        //Intent testIntent = new Intent(this, org.ros.android.Controller.class);
        //startActivity(testIntent);
        Log.d(tag,"Returning Binder");
        return rosBinder;
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
            Log.d(tag, "ROS Service Thread Interrupted");
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
            Log.d(tag,"In Stub, Sending Motor Command");
            if(commandString==null)
                Log.d("ROS","Command String Null");
            if(talker==null)
                Log.d("ROS", "talker is Null");
            talker.publishMessage(commandString);
        }
    };


    @Override
    public void onDestroy() {
        if(gpsThread != null){
            gpsThread.interrupt();
            gpsThread = null;
        }
        Log.d(tag,"ROS onDestroy");
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, "ROS MainService onCreate");

        jaguarManager = new ROSServiceManager(this, new ROSServiceManager.OnConnectedListener() {
            @Override public void onConnected() {
                Log.d(tag, "Jaguar connected - adding reporter");
                jaguarManager.add(jaguarServiceReporter);

            }
            @Override public void onDisconnected() {
                Log.d(tag, "Jaguar disconnected - removing reporter");
                jaguarManager.remove(jaguarServiceReporter);
            }
        });

    }

    private ROSServiceReporter jaguarServiceReporter = new ROSServiceReporter.Stub() {
        @Override
        public void reportGPS(Location location) throws RemoteException {
            currentLocation=location;
        }
    };


}
