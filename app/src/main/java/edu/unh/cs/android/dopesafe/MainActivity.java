package edu.unh.cs.android.dopesafe;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final String TAG = "MainActivity";

  TextView time;
  Button startButton;
  Switch stopButton;
  long startTime = 0L;
  long timeInMilliseconds = 0L;
  long timeSwapBuff = 0L;
  long updatedTime = 0L;
  int t = 1;
  int seconds = 0;
  int minutes = 0;
  Handler handler = new Handler();
  ProgressBar progressCircle;

  Runnable updateTimer;

  private Settings settings;
  private GoogleApiClient mGoogleApiClient;
  protected Location mLastLocation;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    settings = new Settings(this);

    updateValuesFromBundle(savedInstanceState);

    updateTimer = new Runnable() {

      public void run() {

        timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

        updatedTime = timeSwapBuff + timeInMilliseconds;

        seconds = (int) (updatedTime / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes == settings.getTimeMax()) {
          sendSMS(settings.getContact_phone(), settings.getMessage());

          Log.d(TAG, "times up");
          time.setTextColor(Color.RED);
        } else {
          if (minutes != 0 || seconds != 0) {
            time.setText((settings.getTimeMax() - minutes - 1) + ":" + String.format("%02d", (60 - seconds)));
          }
        }

        handler.postDelayed(this, 0);
      }
    };

    setContentView(R.layout.activity_main);
//    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//    setSupportActionBar(toolbar);

    startButton = (Button) findViewById(R.id.start_button);
    stopButton = (Switch) findViewById(R.id.stop_button);
    progressCircle = (ProgressBar) findViewById(R.id.progress_bar);

    stopButton.setVisibility(View.INVISIBLE);
    progressCircle.setVisibility(View.INVISIBLE);
    progressCircle.setProgress(0);
    time = (TextView) findViewById(R.id.time);

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    if (navigationView != null)
      navigationView.setNavigationItemSelectedListener(settings);


    if (startButton != null) {
      startButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          startTimer();
        }
      });
    }

    if (stopButton != null) {
      stopButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if (isChecked) {
            // The toggle is enabled
            stopButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
            progressCircle.setVisibility(View.INVISIBLE);

            //stop the timer
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;
            seconds = 0;
            minutes = 0;
            handler.removeCallbacks(updateTimer);
            updateTime();
            time.setTextColor(Color.BLUE);

            t = 1;

            //reset the toggle button
            stopButton.setChecked(false);

          } else {
            // The toggle is disabled
            stopButton.setVisibility(View.VISIBLE);
          }
        }
      });
    }

    mapInit();
  }

  public void updateTime() {
    time.setText(String.format("%02d", settings.getTimeMax()) + ":00");
  }

  private void updateValuesFromBundle(Bundle savedInstanceState) {

    Boolean motion = false;
    Boolean location = false;
    String phone = getResources().getString(R.string.ali);
    String message = "Hey, it's hk. This is an automated request for help. I haven't moved in a while after using. Would you mind checking up on me?";
    int timeout = 30;

    if (savedInstanceState != null) {
      motion = savedInstanceState.getBoolean("motion", motion);
      message = savedInstanceState.getString("message", message);
      phone = savedInstanceState.getString("phone", phone);
      timeout = savedInstanceState.getInt("timeout", timeout);
      location = savedInstanceState.getBoolean("location", location);
    }

    settings.setMotion(motion);
    settings.setMessage(message);
    settings.setContact_phone(phone);
    settings.setTimeMax(timeout);
    settings.setLocation(location);
  }

  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putBoolean("motion", settings.isMotion());
    savedInstanceState.putString("message", settings.getMessage());
    savedInstanceState.putString("phone", settings.getContact_phone());
    savedInstanceState.putInt("timeout", settings.getTimeMax());
    savedInstanceState.putBoolean("location", settings.isLocation());

    super.onSaveInstanceState(savedInstanceState);
  }


  private void startTimer() {
    Log.d(TAG, "startTimer() called with: " + "");

    if (t == 1) {
//timer will start
      startButton.setVisibility(View.INVISIBLE);
      stopButton.setVisibility(View.VISIBLE);
      progressCircle.setVisibility(View.VISIBLE);

      startTime = SystemClock.uptimeMillis();
      handler.postDelayed(updateTimer, 500);
      t = 0;
    } else {
      startButton.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  public String getMapsUrl() {
    return mLastLocation == null ? "" : "http://maps.google.com/?q=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
  }

  public void sendSMS(String number, String message) {

    if (settings.isLocation())
      message.concat(getMapsUrl());

    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(number, null, message, null, null);
      Log.d(TAG, "sendSMS() called with: " + "number = [" + number + "], message = [" + message + "]");
    } catch (Exception e) {
      Log.e(TAG, "SMS failed!", e);
    }
  }

  private void mapInit() {

// Create an instance of GoogleAPIClient.
    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(this)
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API)
          .build();
    }
  }

  private Runnable getTimer() {
    return new Runnable() {

      public void run() {
        timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

        updatedTime = timeSwapBuff + timeInMilliseconds;

        seconds = (int) (updatedTime / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes == settings.getTimeMax()) {
          sendSMS(settings.getContact_phone(), settings.getMessage());
          time.setText("00:00");
          time.setTextColor(Color.RED);

        } else {
          if (minutes != 0 || seconds != 0) {
            time.setText(String.format("%02d", (settings.getTimeMax() - minutes - 1)) + ":" + String.format("%02d", (60 - seconds)));
          }

          long p = updatedTime / settings.getTimeMax();
          progressCircle.setProgress((int)p);
          handler.postDelayed(this, 0);
        }
      }
    };
  }


  /**
   * Runs when a GoogleApiClient object successfully connects.
   */
  @Override
  public void onConnected(Bundle connectionHint) {
    // Provides a simple way of getting a device's location and is well suited for
    // applications that do not require a fine-grained location and that do not need location
    // updates. Gets the best and most recent location currently available, which may be null
    // in rare cases when a location is not available.
    if (Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

      mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    } else {
      Log.d(TAG, "location not found");
    }
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
    // onConnectionFailed.
    Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
  }


  @Override
  public void onConnectionSuspended(int cause) {
    // The connection to Google Play services was lost for some reason. We call connect() to
    // attempt to re-establish the connection.
    Log.i(TAG, "Connection suspended");
    mGoogleApiClient.connect();
  }
}
