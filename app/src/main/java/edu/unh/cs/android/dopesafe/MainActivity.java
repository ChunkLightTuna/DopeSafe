package edu.unh.cs.android.dopesafe;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final String TAG = "MainActivity";

  TextView time;
  Button startButton;
  long startTime = 0L;
  long timeInMilliseconds = 0L;
  long timeSwapBuff = 0L;
  long updatedtime = 0L;
  int t = 1;
  int seconds = 0;
  int minutes = 0;
  int milliseconds = 0;
  Handler handler = new Handler();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    startButton = (Button) findViewById(R.id.start_button);

    time = (TextView) findViewById(R.id.time);


    if (startButton != null) {
      startButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          startTimer();
        }
      });
    }

    Button smsButton = (Button) findViewById(R.id.test_sms);

    if (smsButton != null) {
      smsButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String number = "7024301384";
          String sms = "test test yo!";

          try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, sms, null, null);
            Log.d(TAG, "onClick() called with: " + "v = [" + v + "]" + " SMS Sent!");
          } catch (Exception e) {
            Log.e(TAG, "SMS failed, please try again later!", e);
          }
        }
      });
    }


  }

  public Runnable updateTimer = new Runnable() {

    public void run() {

      timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

      updatedtime = timeSwapBuff + timeInMilliseconds;

      seconds = (int) (updatedtime / 1000);
      minutes = seconds / 60;
      seconds = seconds % 60;
      milliseconds = (int) (updatedtime % 1000);
      time.setText("" + minutes + ":" + String.format("%02d", seconds) + ":"
          + String.format("%03d", milliseconds));
      time.setTextColor(Color.RED);
      handler.postDelayed(this, 0);
    }
  };


  private void startTimer() {
    Log.d(TAG, "startTimer() called with: " + "");

    if (t == 1) {
//timer will start
      //startButton.setText("Pause");
      startTime = SystemClock.uptimeMillis();
      handler.postDelayed(updateTimer, 0);
      t = 0;
    } else {
//timer will pause
      startButton.setText("Start");
      time.setTextColor(Color.BLUE);
      timeSwapBuff += timeInMilliseconds;
      handler.removeCallbacks(updateTimer);
      t = 1;
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

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_camera) {
      // Handle the camera action
    } else if (id == R.id.nav_gallery) {

    } else if (id == R.id.nav_slideshow) {

    } else if (id == R.id.nav_manage) {

    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
