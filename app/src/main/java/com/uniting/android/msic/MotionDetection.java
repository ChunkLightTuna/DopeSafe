//package com.uniting.android.msic;
//
//import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.util.Log;
//
///**
// * Created by Chris Oelerich on 5/20/16.
// *
// * totallllllllllly doesn't work, but you're welcome to play with it
// */
//class MotionDetection implements SensorEventListener {
//    private static final String TAG = "MotionDetection";
//    private static final int SHAKE_THRESHOLD = 1200;
//
//    private static MotionDetection motionDetection;
//
//    private final float[] v = new float[3];
//    private final float[] vOld = new float[3];
//    private long lastUpdate;
//    private static boolean motionSupported;
//
//    private MotionDetection() {
//    }
//
//    static MotionDetection getInstance(Context context) {
//
//        if (motionDetection == null) {
//            motionDetection = new MotionDetection();
//        }
//
//        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//
//        if (sensorManager == null) {
//            motionSupported = false;
//        } else {
//            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            sensorManager.registerListener(motionDetection, sensor, SensorManager.SENSOR_DELAY_GAME);
//            motionSupported = true;
//        }
//
//        return motionDetection;
//    }
//
//    @Override
//    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
//    }
//
//    @Override
//    public final void onSensorChanged(SensorEvent event) {
//        long curTime = System.currentTimeMillis();
//        // only allow one update every 100ms.
//        if ((curTime - lastUpdate) > 100) {
//            long diffTime = (curTime - lastUpdate);
//            lastUpdate = curTime;
//
//            v[0] = event.values[0];
//            v[1] = event.values[1];
//            v[2] = event.values[2];
//
//            float speed = Math.abs(v[0] + v[1] + v[0] - vOld[0] - vOld[1] - vOld[2]) / diffTime * 10000;
//
//            if (speed > 3 * SHAKE_THRESHOLD) {
//                Log.d(TAG, "onSensorChanged() called with: " + "event = [" + event + "]");
//            }
//
//            vOld[0] = v[0];
//            vOld[1] = v[1];
//            vOld[2] = v[2];
//        }
//
//    }
//
//}
