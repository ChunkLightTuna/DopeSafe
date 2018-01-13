package com.uniting.android.msic;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private UserPrefs prefs;

    private TextView time;
    private Button startButton;
    private ImageButton pauseButton;
    private ImageButton resumeButton;
    private Switch stopButton;
    private ProgressBar progressCircle;

    private long startTime = 0L;
    private long pauseTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean t = true;
    private int seconds = 0;
    private int minutes = 0;
    private boolean alarm = false;
    private boolean timerRunning = false;
    private int priorRingVolume;

    private Handler handler = new Handler();
    private Runnable timer;

    private Ringtone ringtone;
    private Vibrator vibrator;
    private LocationService locationService;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = UserPrefs.getInstance();
        getSharedPrefs(getPreferences(Context.MODE_PRIVATE));

        timer = getTimer();

        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        setVolumeControlStream(AudioManager.STREAM_ALARM);

        if (ringtone == null) {
            Log.d(TAG, "volume is muted, might want to fix that!");
        }

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        progressCircle = findViewById(R.id.progress_bar);
        if (progressCircle != null) {
            progressCircle.setProgress(0);
            progressCircle.setIndeterminate(false);
            progressCircle.setVisibility(View.INVISIBLE);
        }

        time = findViewById(R.id.time);
        updateTime();

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> confirmInitializeOfSession());


        pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(v -> pauseTimer());


        resumeButton = findViewById(R.id.resume_button);
        resumeButton.setOnClickListener(view -> resumeTimer());

        stopButton = findViewById(R.id.stop_button);

        stopButton.setVisibility(View.INVISIBLE);
        stopButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "onCheckedChanged() called with: " + "buttonView = [" + buttonView + "], isChecked = [" + isChecked + "]");
            if (isChecked) {
                // The toggle is enabled
                stopButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                resumeButton.setVisibility(View.INVISIBLE);
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
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new SideMenuLogic(this, prefs));

        Permissions.requestSMS(this);
        Permissions.requestNotificationPolicy(this);
//        locationService = new LocationService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setSharedPrefs();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        alertDialog = Permissions.dealWithIt(this, requestCode, grantResults);
    }

    @Override
    protected void onResume() {
//        if user enabled SMS through OS app settings dismiss the box
        if (alertDialog != null) {
            TextView message = alertDialog.findViewById(android.R.id.message);
            if (message != null && message.getText() == getString(R.string.sms_permissions_dialog_message) && Permissions.smsGranted(this)) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(am != null) {
            am.setStreamVolume(
                    AudioManager.STREAM_RING,
                    priorRingVolume,
                    0);
        }
    }

    /**
     * https://developer.android.com/guide/topics/location/strategies.html#BestPerformance
     */
    private void tryToInitLocationService() {
        if (Permissions.locationGranted(this))
            locationService = new LocationService(this);
    }

    public void updateTime() {
        time.setText(String.format(getString(com.uniting.android.msic.R.string.time_format), prefs.getTime(), 0));
    }


    private void getSharedPrefs(SharedPreferences sharedPref) {
        Log.d(TAG, "getSharedPrefs() called with: " + "sharedPref = [" + sharedPref + "]");

        String phone = getString(com.uniting.android.msic.R.string.pref_default_contact);
        String message = getString(com.uniting.android.msic.R.string.default_message);
        int timeout = 1;

        if (sharedPref != null) {
            message = sharedPref.getString("message", message);
            phone = sharedPref.getString("phone", phone);
            timeout = sharedPref.getInt("time", timeout);
        }

        prefs.setMsg(message);
        prefs.setPhone(phone);
        prefs.setTime(timeout);
    }

    private void setSharedPrefs() {
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
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            progressCircle.setVisibility(View.VISIBLE);

            startTime = SystemClock.uptimeMillis();
            timerRunning = true;
            handler.postDelayed(timer, 500);
            t = false;
        } else {
            startButton.setVisibility(View.VISIBLE);
        }
    }

    private void pauseTimer() {
        timerRunning = false;
        pauseTime = SystemClock.uptimeMillis();
        ringtone.stop();
        pauseButton.setVisibility(View.INVISIBLE);
        resumeButton.setVisibility(View.VISIBLE);
        //progressCircle.setVisibility(View.INVISIBLE);
        stopButton.setText(R.string.reset);
    }

    private void resumeTimer() {
        timerRunning = true;
        resumeButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        startTime = startTime + (SystemClock.uptimeMillis() - pauseTime);
        pauseTime = 0L;
        //progressCircle.setVisibility(View.VISIBLE);
        stopButton.setText(R.string.stop);
        if (alarm)
            ringtone.play();
    }

    private void confirmInitializeOfSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.init_dialog_message)
                .setTitle(R.string.init_dialog_title)
                .setPositiveButton(R.string.init_dialog_positive_button_text, (dialogInterface, i) -> startTimer())
                .setNegativeButton(R.string.init_dialog_negative_button_text, (dialogInterface, i) -> {
                    //
                })
                .create()
                .show();
    }


    void sendSMS(String number, String message, Location location) {
        sendSMS(number, message + "\n" + getGoogleMapsUrl(location));
    }

    void sendSMS(String number, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Log.d(TAG, "sendSMS() called with: " + "number = [" + number + "], message = [" + message + "]");
        } catch (Exception e) {
            Log.e(TAG, "SMS failed!", e);
        }
    }

    public String getGoogleMapsUrl(Location location) {
        try {
            return "google.com/maps?q=" +
                    location.getLatitude() +
                    "," +
                    location.getLongitude();
        } catch (NullPointerException e) {
            Log.e(TAG, "unable to get location string(Probably because emulator is being used and location was not sent.)", e);
            return "Location unavailable";
        }
    }


    /**
     * https://developer.android.com/guide/topics/media-apps/volume-and-earphones.html
     */
    private void setRingVolumeMax() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (am != null && nm != null && nm.isNotificationPolicyAccessGranted()) {
            priorRingVolume = am.getStreamVolume(AudioManager.STREAM_RING);
            am.setStreamVolume(
                    AudioManager.STREAM_RING,
                    am.getStreamMaxVolume(AudioManager.STREAM_RING),
                    0);
        }
    }

    private Runnable getTimer() {

        return new Runnable() {

            public void run() {
                if (timerRunning) {

                    timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                    updatedTime = timeSwapBuff + timeInMilliseconds;

                    seconds = (int) (updatedTime / 1000);
                    minutes = seconds / 60;
                    seconds = seconds % 60;

                    //times up
                    if (minutes == prefs.getTime()) {

                        //TODO ask for location permissions
                        if(locationService != null ) {
                            sendSMS(prefs.getPhone(), prefs.getMsg(), locationService.getLocation());
                        }else {
                            sendSMS(prefs.getPhone(), prefs.getMsg());
                        }

                        time.setText(String.format(getString(com.uniting.android.msic.R.string.time_format), 0, 0));
                        time.setTextColor(Color.RED);
                    } else {

                        //one minute left
                        if (minutes + 1 >= prefs.getTime() && !alarm) {
                            Log.d(TAG, "playing alarm");

                            if (ringtone == null) {
                                ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                            }
                            setRingVolumeMax();
                            ringtone.play();

                            //noinspection deprecation
                            vibrator.vibrate(200);

                            alarm = true;
                        }

                        if (minutes != 0 || seconds != 0) {
                            time.setText(String.format(getString(com.uniting.android.msic.R.string.time_format), (prefs.getTime() - minutes - 1), (60 - seconds)));
                        }

                        double currentSeconds = ((prefs.getTime() - minutes - 1) * 60) + (60 - seconds);
                        double definedSeconds = (prefs.getTime() * 60);
                        double p = (100) - (currentSeconds / definedSeconds) * 100;
                        progressCircle.setProgress(0);
                        progressCircle.setProgress((int) p);
                    }
                }
                handler.postDelayed(this, 0);
            }
        };
    }
}
