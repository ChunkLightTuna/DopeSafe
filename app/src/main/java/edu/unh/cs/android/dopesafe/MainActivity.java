package edu.unh.cs.android.dopesafe;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  TextView time;
  Button startButton;
  Switch stopButton;
  ProgressBar progressCircle;
  long startTime = 0L;
  long timeInMilliseconds = 0L;
  long timeSwapBuff = 0L;
  long updatedTime = 0L;
  int t = 1;
  int seconds = 0;
  int minutes = 0;
  Handler handler = new Handler();
  Settings settings;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    settings = new Settings(this);

    startButton = (Button) findViewById(R.id.start_button);
    stopButton = (Switch) findViewById(R.id.stop_button);
    progressCircle = (ProgressBar) findViewById(R.id.progress_bar);

    stopButton.setVisibility(View.INVISIBLE);
    progressCircle.setVisibility(View.INVISIBLE);
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
          time.setText("00:00");
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

//    Button smsButton = (Button) findViewById(R.id.test_sms);
//
//    if (smsButton != null) {
//      smsButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          String number = "7024301384";
//          String sms = "test test yo!";
//
//          try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(number, null, sms, null, null);
//            Log.d(TAG, "onClick() called with: " + "v = [" + v + "]" + " SMS Sent!");
//          } catch (Exception e) {
//            Log.e(TAG, "SMS failed!", e);
//          }
//        }
//      });
//    }


  }

  public Runnable updateTimer = new Runnable() {

    public void run() {

      timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

      updatedTime = timeSwapBuff + timeInMilliseconds;

      seconds = (int) (updatedTime / 1000);
      minutes = seconds / 60;
      seconds = seconds % 60;
      time.setText("" + minutes + ":" + String.format("%02d", seconds));
      time.setTextColor(Color.GREEN);
      handler.postDelayed(this, 0);
    }
  };


  private void startTimer() {
    Log.d(TAG, "startTimer() called with: " + "");

    if (t == 1) {
//timer will start
      //startButton.setText("Stop");
      startButton.setVisibility(View.INVISIBLE);
      stopButton.setVisibility(View.VISIBLE);
      progressCircle.setVisibility(View.VISIBLE);

      startTime = SystemClock.uptimeMillis();
      handler.postDelayed(updateTimer, 0);
      t = 0;
    } else {
//timer will pause
      //startButton.setText("Start");

      /*startTime = 0L;
      timeInMilliseconds = 0L;
      timeSwapBuff = 0L;
      updatedTime = 0L;
      seconds = 0;
      minutes = 0;
      handler.removeCallbacks(updateTimer);
      time.setText("00:00");
      time.setTextColor(Color.BLUE);

      t = 1;*/

      startButton.setVisibility(View.VISIBLE);
    }


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
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
