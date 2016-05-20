package edu.unh.cs.android.dopesafe;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Chris Oelerich on 5/20/16.
 */
public class MotionDetection implements SensorEventListener {
  private static final String TAG = "MotionDetection";
  private static final int SHAKE_THRESHOLD = 1200;

  private float[] v = new float[3];
  private float[] vOld = new float[3];
  private long lastUpdate;


  public MotionDetection(Context context) {

    SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    if (sensor != null) {
      sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
  }

  @Override
  public final void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  @Override
  public final void onSensorChanged(SensorEvent event) {
    long curTime = System.currentTimeMillis();
    // only allow one update every 100ms.
    if ((curTime - lastUpdate) > 100) {
      long diffTime = (curTime - lastUpdate);
      lastUpdate = curTime;

      v[0] = event.values[0];
      v[1] = event.values[1];
      v[2] = event.values[2];

      float speed = Math.abs(v[0] + v[1] + v[0] - vOld[0] - vOld[1] - vOld[2]) / diffTime * 10000;

      if (speed > 3 * SHAKE_THRESHOLD) {
        Log.d(TAG, "onSensorChanged() called with: " + "event = [" + event + "]");
      }

      vOld[0] = v[0];
      vOld[1] = v[1];
      vOld[2] = v[2];
    }

  }

}
