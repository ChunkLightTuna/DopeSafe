package edu.unh.cs.android.dopesafe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;


/**
 * Created by Chris Oelerich on 5/20/16.
 *
 * Main
 */
public class MainActivity extends AppCompatActivity
    /*implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener*/ {

  private static final String TAG = "MainActivity";

  TextView time;
  Button startButton;
  //  SeekBar stopButton;
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

  private Preferences prefs;

//  private GoogleApiClient mGoogleApiClient;
//  protected Location mLastLocation;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    prefs = Preferences.getInstance();

    updateValuesFromBundle(getPreferences(Context.MODE_PRIVATE));

    updateTimer = getTimer();

    setContentView(R.layout.activity_main);

    startButton = (Button) findViewById(R.id.start_button);
//    stopButton = (SeekBar) findViewById(R.id.stop_button);
    stopButton = (Switch) findViewById(R.id.stop_button);
    progressCircle = (ProgressBar) findViewById(R.id.progress_bar);

    stopButton.setVisibility(View.INVISIBLE);
    progressCircle.setVisibility(View.INVISIBLE);
    progressCircle.setProgress(0);
    time = (TextView) findViewById(R.id.time);

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    if (navigationView != null)
      navigationView.setNavigationItemSelectedListener(new SettingsMenu(this, prefs));


    if (startButton != null) {
      startButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          startTimer();
        }
      });
    }

    if (stopButton != null) {
//      stopButton.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//          if(progress>95) {
//            seekBar.setThumb(getResources().getDrawable(R.drawable.common_google_signin_btn_icon_light_focused));
//          }
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//
//          if(seekBar.getProgress()>95) {
//            seekBar.setThumb(getResources().getDrawable(R.drawable.circular_progress_bar));
//          }
//        }
//      });

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
            time.setTextColor(Color.WHITE);

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

//    mapInit();
  }

  public void updateTime() {
//    time.setText(String.format("%02d", prefs.getTime()) + ":00");
    time.setText(String.format(getString(R.string.display_time), prefs.getTime(), 0));
  }

  private void updateValuesFromBundle(SharedPreferences sharedPref) {
    Boolean motion = false;
    Boolean location = false;
    String phone = getResources().getString(R.string.default_phone);
    String message = getResources().getString(R.string.default_message);
    int timeout = 30;

    if (sharedPref != null) {
      message = sharedPref.getString("message", message);
      phone = sharedPref.getString("phone", phone);
      timeout = sharedPref.getInt("timeout", timeout);
      motion = sharedPref.getBoolean("motion", motion);
      location = sharedPref.getBoolean("location", location);
    }
    prefs.setMsg(message);
    prefs.setPhone(phone);
    prefs.setTime(timeout);
    prefs.setMotion(motion);
    prefs.setLoc(location);
  }

  @Override
  public void onPause() {

    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
    editor.putBoolean("motion", prefs.isMotion());
    editor.putString("message", prefs.getMsg());
    editor.putString("phone", prefs.getPhone());
    editor.putInt("timeout", prefs.getTime());
    editor.putBoolean("location", prefs.isLoc());
    editor.apply();

    super.onPause();
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
//    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
//    if (mGoogleApiClient.isConnected()) {
//      mGoogleApiClient.disconnect();
//    }
  }

//  public String getMapsUrl() {
//    return mLastLocation == null ? "" : "http://maps.google.com/?q=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
//  }

  public void sendSMS(String number, String message) {

//    if (prefs.isLoc())
//      message += getMapsUrl();

      try {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
        Log.d(TAG, "sendSMS() called with: " + "number = [" + number + "], message = [" + message + "]");
      } catch (Exception e) {
        Log.e(TAG, "SMS failed!", e);
      }
  }

//  private void mapInit() {
//// Create an instance of GoogleAPIClient.
//      if (mGoogleApiClient == null) {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//            .addConnectionCallbacks(this)
//            .addOnConnectionFailedListener(this)
//            .addApi(LocationServices.API)
//            .build();
//      }
//    }
//  }


  private Runnable getTimer() {

    return new Runnable() {

      public void run() {

        timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

        updatedTime = timeSwapBuff + timeInMilliseconds;

        seconds = (int) (updatedTime / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes == prefs.getTime()) {
          sendSMS(prefs.getPhone(), prefs.getMsg());
          time.setText(String.format(getString(R.string.display_time), 0, 0));
          time.setTextColor(Color.RED);
        } else {
          if (minutes != 0 || seconds != 0) {

            time.setText(String.format(getString(R.string.display_time), (prefs.getTime() - minutes - 1), (60 - seconds)));

          }
          long p = updatedTime / prefs.getTime();
          progressCircle.setProgress((int) p);
          handler.postDelayed(this, 0);
        }
      }
    };
  }

//  /**
//   * Runs when a GoogleApiClient object successfully connects.
//   */
//  @Override
//  public void onConnected(Bundle connectionHint) {
//    // Provides a simple way of getting a device's location and is well suited for
//    // applications that do not require a fine-grained location and that do not need location
//    // updates. Gets the best and most recent location currently available, which may be null
//    // in rare cases when a location is not available.
//    if (Build.VERSION.SDK_INT >= 23 &&
//        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//      mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//    } else {
//      Log.d(TAG, "location not found");
//    }
//  }
//
//  @Override
//  public void onConnectionFailed(ConnectionResult result) {
//    // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
//    // onConnectionFailed.
//    Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
//  }
//
//
//  @Override
//  public void onConnectionSuspended(int cause) {
//    // The connection to Google Play services was lost for some reason. We call connect() to
//    // attempt to re-establish the connection.
//    Log.i(TAG, "Connection suspended");
//    mGoogleApiClient.connect();
//  }
}
