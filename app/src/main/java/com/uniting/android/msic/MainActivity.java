package com.uniting.android.msic;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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


/**
 * Created by Chris Oelerich on 5/20/16.
 * <p/>
 * Main
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 15423;

    private UserPrefs prefs;

    private TextView time;
    private Button startButton;
    private Switch stopButton;//  SeekBar stopButton;
    private ProgressBar progressCircle;

    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean t = true;
    private int seconds = 0;
    private int minutes = 0;
    private boolean alarm = false;

    private Handler handler = new Handler();
    private Runnable timer;

    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: " + "savedInstanceState = [" + savedInstanceState + "]");

        setContentView(com.uniting.android.msic.R.layout.activity_main);

        prefs = UserPrefs.getInstance();
        getSharedPrefs(getPreferences(Context.MODE_PRIVATE));

        timer = getTimer();

        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        if (ringtone == null) {
            Log.d(TAG, "volume is muted, might want to fix that!");
        }

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        progressCircle = (ProgressBar) findViewById(com.uniting.android.msic.R.id.progress_bar);
        if (progressCircle != null) {
            progressCircle.setProgress(0);
            progressCircle.setVisibility(View.INVISIBLE);
        }

        time = (TextView) findViewById(com.uniting.android.msic.R.id.time);
        updateTime();

        NavigationView navigationView = (NavigationView) findViewById(com.uniting.android.msic.R.id.nav_view);
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(new SettingsMenu(this, prefs));

        startButton = (Button) findViewById(com.uniting.android.msic.R.id.start_button);
        if (startButton != null) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTimer();
                }
            });

        }

        stopButton = (Switch) findViewById(com.uniting.android.msic.R.id.stop_button);
        if (stopButton != null) {
            stopButton.setVisibility(View.INVISIBLE);
            stopButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    Log.d(TAG, "onCheckedChanged() called with: " + "buttonView = [" + buttonView + "], isChecked = [" + isChecked + "]");
                    if (isChecked) {
                        // The toggle is enabled
                        stopButton.setVisibility(View.INVISIBLE);
                        startButton.setVisibility(View.VISIBLE);
                        progressCircle.setProgress(0);
                        progressCircle.setVisibility(View.INVISIBLE);

                        //stop the timer
                        startTime = 0L;
                        timeInMilliseconds = 0L;
                        timeSwapBuff = 0L;
                        updatedTime = 0L;
                        seconds = 0;
                        minutes = 0;
                        handler.removeCallbacks(timer);
                        updateTime();
                        time.setTextColor(Color.WHITE);

                        alarm = false;
                        ringtone.stop();
                        vibrator.cancel();
                        t = true;

                        //reset the toggle button
                        stopButton.setChecked(false);

                    } else {
                        // The toggle is disabled
                        Log.wtf(TAG, "onCheckedChanged: this shouldn't have happened!");
                        stopButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        getPermissions();
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
        setSharedPrefs();
    }

    public void updateTime() {
        time.setText(String.format(getString(com.uniting.android.msic.R.string.display_time), prefs.getTime(), 0));
    }

    private void getSharedPrefs(SharedPreferences sharedPref) {
        Log.d(TAG, "getSharedPrefs() called with: " + "sharedPref = [" + sharedPref + "]");

        String phone = getString(com.uniting.android.msic.R.string.default_phone);
        String message = getString(com.uniting.android.msic.R.string.default_message);
        int timeout = 30;

        if (sharedPref != null) {
            message = sharedPref.getString("message", message);
            phone = sharedPref.getString("phone", phone);
            timeout = sharedPref.getInt("time", timeout);
        }

        prefs.setMsg(message);
        prefs.setPhone(phone);
        prefs.setTime(timeout);

        Log.d(TAG, "getSharedPrefs() msg: " + prefs.getMsg());
        Log.d(TAG, "getSharedPrefs() phone: " + prefs.getPhone());
        Log.d(TAG, "getSharedPrefs() time: " + prefs.getTime());
        Log.d(TAG, "getSharedPrefs() motion: " + prefs.isMotion());
        Log.d(TAG, "getSharedPrefs() loc: " + prefs.isLoc());
    }

    private void setSharedPrefs() {
        Log.d(TAG, "setSharedPrefs() msg: " + prefs.getMsg());
        Log.d(TAG, "setSharedPrefs() phone: " + prefs.getPhone());
        Log.d(TAG, "setSharedPrefs() time: " + prefs.getTime());
        Log.d(TAG, "setSharedPrefs() motion: " + prefs.isMotion());
        Log.d(TAG, "setSharedPrefs() loc: " + prefs.isLoc());

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("message", prefs.getMsg());
        editor.putString("phone", prefs.getPhone());
        editor.putInt("time", prefs.getTime());
        editor.putBoolean("motion", prefs.isMotion());
        editor.putBoolean("location", prefs.isLoc());

        editor.apply();
    }

    private void startTimer() {
        Log.d(TAG, "startTimer() called with: " + "");

        if (t) {
//timer will start
            startButton.setVisibility(View.INVISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            progressCircle.setVisibility(View.VISIBLE);

            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(timer, 500);
            t = false;
        } else {
            startButton.setVisibility(View.VISIBLE);
        }
    }

    public void sendSMS(String number, String message) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Log.d(TAG, "sendSMS() called with: " + "number = [" + number + "], message = [" + message + "]");
        } catch (Exception e) {
            Log.e(TAG, "SMS failed!", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: permissions not granted!");
                    View contentView = findViewById(android.R.id.content);
                    if (contentView != null) {
                        final Snackbar snackbar = Snackbar.make(contentView, "App will not work if you don't grant it SMS permissions", Snackbar.LENGTH_INDEFINITE);

                        snackbar.getView().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                getPermissions();
                            }
                        });
                        snackbar.show();
                    }
                }
            }
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

                //times up
                if (minutes == prefs.getTime()) {
                    sendSMS(prefs.getPhone(), prefs.getMsg());
                    time.setText(String.format(getString(com.uniting.android.msic.R.string.display_time), 0, 0));
                    time.setTextColor(Color.RED);
                } else {

                    //one minute left
                    if (minutes + 1 >= prefs.getTime() && !alarm) {
                        Log.d(TAG, "playing alarm");

                        if (ringtone == null) {
                            ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                        }

                        ringtone.play();


                        //noinspection deprecation
                        vibrator.vibrate(200);


                        alarm = true;
                    }

                    if (minutes != 0 || seconds != 0) {
                        time.setText(String.format(getString(com.uniting.android.msic.R.string.display_time), (prefs.getTime() - minutes - 1), (60 - seconds)));
                    }
                    long p = updatedTime / prefs.getTime();
                    progressCircle.setProgress((int) p);
                    handler.postDelayed(this, 0);
                }
            }
        };
    }
}
