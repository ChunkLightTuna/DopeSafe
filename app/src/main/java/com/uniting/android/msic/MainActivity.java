package com.uniting.android.msic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ebanx.swipebtn.SwipeButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

//    private Prefs prefs;

    private TextView timeDisplay;
    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;
    private ImageView alertView;
    //private Switch stopButton;
    private ProgressBar progressCircle;
    private SwipeButton stopButton;
    private long startTime = 0L;
    private long pauseTime = 0L;
    private long timeInMilliseconds = 0L;
    private long updatedTime = 0L;

    private boolean timerStopped = true;
    private boolean timerPaused = false;

    private int seconds = 0;
    private int minutes = 0;

    private boolean emergencyProcedureInitialized;
    private boolean alarmStarted = false;

    private final Handler handler = new Handler();
    private Runnable timer;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private LocationService locationService;

    private List<AlertDialog> permissionsAlerts = new ArrayList<>();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        progressCircle = findViewById(R.id.progress_bar);
        if (progressCircle != null) {
            progressCircle.setProgress(0);
            progressCircle.setIndeterminate(false);
            progressCircle.setVisibility(View.INVISIBLE);
        }

        this.timeDisplay = findViewById(R.id.time);

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            if (Permissions.smsGranted(this)) {
                confirmInitializeOfSession();
            }

        });

        pauseButton = findViewById(R.id.pause_button);
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.wiggle);

        pauseButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.startAnimation(animShake);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.clearAnimation();
            }

            return false;
        });
        pauseButton.setOnLongClickListener(v -> {
            v.clearAnimation();
            pauseTimer();
            return false;
        });

        resumeButton = findViewById(R.id.resume_button);
        resumeButton.setOnClickListener(view -> resumeTimer());

        stopButton = findViewById(R.id.stop_button);
        stopButton.setVisibility(View.GONE);
        stopButton.setOnActiveListener(this::stopTimer);

        alertView = findViewById(R.id.alert_view);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            int height = decorView.getHeight();
            Log.i(TAG, "Current height: " + height);
        });


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Permissions.requestSMS(this);
        Permissions.requestLocation(this);
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
        permissionsAlerts.add(Permissions.handlePermissionResult(this, requestCode, grantResults));
    }

    @Override
    protected void onResume() {
//        if user enabled SMS through OS app settings dismiss the box. working on sony, not motorola

        if (permissionsAlerts != null) {
            if (Permissions.smsGranted(this)) {
                for (AlertDialog alertDialog : permissionsAlerts) {
                    if (alertDialog != null) {
                        TextView message = alertDialog.findViewById(android.R.id.message);
                        if (message != null && message.getText() == getString(R.string.sms_permissions_dialog_message)) {
                            alertDialog.dismiss();
                        }
                    }
                }
                permissionsAlerts.clear();
            } else {
                for (int i = 0, j = permissionsAlerts.size(); i < j; ) {
                    if (permissionsAlerts.get(i) == null) {
                        permissionsAlerts.remove(i);
                        j--;
                    } else {
                        i++;
                    }
                }
                while (permissionsAlerts.size() > 1) {
                    AlertDialog alertDialog = permissionsAlerts.get(0);
                    alertDialog.setOnDismissListener(d -> {
                    });
                    alertDialog.dismiss();
                    permissionsAlerts.remove(0);
                }
            }
        }


        updateTime();
        tryToInitLocationService();

        super.onResume();
    }

    /**
     * https://developer.android.com/guide/topics/location/strategies.html#BestPerformance
     */
    private void tryToInitLocationService() {
        if (Permissions.locationGranted(this) && locationService == null)
            locationService = new LocationService(this);
    }

    public void updateTime() {
        int timeInMinutes = Prefs.getTime(this.getApplicationContext());
        timeDisplay.setText(String.format(getString(com.uniting.android.msic.R.string.time_format), timeInMinutes, 0));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void toggleHideyBar() {

        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 19) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void startTimer() {
        Log.d(TAG, "startTimer() called");


        if (timerStopped) {
            //timer will start
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.hide();

            toggleHideyBar();


//            Window window = getWindow();
//
//            // clear FLAG_TRANSLUCENT_STATUS flag:
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            }
//            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            }
//
//            // finally change the color
//            window.setStatusBarColor(getColor(R.color.unitingPurpleDark));


            startButton.setVisibility(View.INVISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            progressCircle.setVisibility(View.VISIBLE);

            startTime = SystemClock.uptimeMillis();
            timerPaused = false;

            timer = getTimer();

            handler.postDelayed(timer, 500);
            timerStopped = false;


            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (am != null) {

                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                /*timeDisplay in miliseconds*/
                am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, alarmIntent);

            }

        } else {
            startButton.setVisibility(View.VISIBLE);
        }
    }

    private void pauseTimer() {
        timerPaused = true;
        alarmStarted = false;
        pauseTime = SystemClock.uptimeMillis();
        mediaPlayer.pause();
        pauseButton.setVisibility(View.INVISIBLE);
        resumeButton.setVisibility(View.VISIBLE);
        stopButton.setText(getResources().getString(R.string.reset));
    }

    private void resumeTimer() {
        timerPaused = false;
        resumeButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        startTime = startTime + (SystemClock.uptimeMillis() - pauseTime);
        pauseTime = 0L;
        stopButton.setText(getResources().getString(R.string.stop));
    }

    private void stopTimer() {
        Log.d(TAG, "stopTimer() called");
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggleHideyBar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.show();
        stopButton.setVisibility(View.GONE);
        alertView.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        resumeButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        progressCircle.setProgress(0);
        progressCircle.setVisibility(View.INVISIBLE);
        startTime = 0L;
        timeInMilliseconds = 0L;
        updatedTime = 0L;
        seconds = 0;
        minutes = 0;
        handler.removeCallbacks(timer);
        updateTime();
        timeDisplay.setTextColor(Color.WHITE);

        alarmStarted = false;
        emergencyProcedureInitialized = false;
        mediaPlayer.reset();
        vibrator.cancel();
        timerStopped = true;
    }

    private void confirmInitializeOfSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.init_dialog_message)
                .setTitle(R.string.init_dialog_title)
                .setPositiveButton(R.string.cont, (dialogInterface, i) -> startTimer())
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    //
                })
                .setCancelable(false)
                .create()
                .show();
    }

    void sendSMS(Set<String> numbers, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();

            for (String number : numbers) {
                if (android.os.Build.VERSION.SDK_INT >= 22) {
                    Log.e("Alert", "Checking SubscriptionId");
                    try {
                        Log.e("Alert", "SubscriptionId is " + smsManager.getSubscriptionId());
                    } catch (Exception e) {
                        Log.e("Alert", e.getMessage());
                        Log.e("Alert", "Fixed SubscriptionId to 1");
                        smsManager = SmsManager.getSmsManagerForSubscriptionId(1);
                    }
                }

                smsManager.sendTextMessage(number, null, message, null, null);
                Log.d(TAG, "sendSMS() called with: " + "number = [" + number + "], message = [" + message + "]");
            }
        } catch (Exception e) {
            Log.e(TAG, "SMS failed!", e);
        }
    }

    @SuppressLint("DefaultLocale")
    public String getGoogleMapsUrl() {
        Location location = locationService.getLocation();
        String latitude = String.format("%1$,.6f", location.getLatitude());
        String longitude = String.format("%1$,.6f", location.getLongitude());
        return "google.com/maps?q=" + latitude + "," + longitude;
    }


    /**
     * https://developer.android.com/guide/topics/media-apps/volume-and-earphones.html
     */
    private void setAlarmVolumeMax() {
        Log.d(TAG, "Setting ring volume max");
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            Log.d(TAG, "Max alarm volume: " + am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
            am.setStreamVolume(
                    AudioManager.STREAM_ALARM,
                    am.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                    0);
        }
    }

    private void startAlarm() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (IOException e){
            //TODO - what do we do here?
        }
    }

    private void playSiren(){
        if(alarmStarted)
            stopAlarm();

        AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributesBuilder.setUsage(AudioAttributes.USAGE_ALARM);
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.siren, audioAttributesBuilder.build(), 0);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopAlarm(){
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        alarmStarted = false;
    }

    private Runnable getTimer() {

        final int time = Prefs.getTime(this);
        final String message = Prefs.getMsg(this);
        final Set<String> phoneNumbers = Prefs.getPhones(this);
        final boolean location = Prefs.isLoc(this);

        return new Runnable() {
            public void run() {
                if (!timerPaused && !timerStopped) {

                    timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                    updatedTime = timeInMilliseconds;

                    int remaining = (int) (updatedTime / 1000);
                    minutes = remaining / 60;
                    seconds = remaining % 60;

                    //times up
                    if (minutes == time) {
                        if(!emergencyProcedureInitialized){
                            emergencyProcedureInitialized = true;
                            if (location) {
                                sendSMS(phoneNumbers, message + "\n" + getGoogleMapsUrl());
                            } else {
                                sendSMS(phoneNumbers, message);
                            }
                            pauseButton.setVisibility(View.INVISIBLE);
                            progressCircle.setVisibility(View.INVISIBLE);
                            alertView.setVisibility(View.VISIBLE);
                            timeDisplay.setText(String.format(getString(com.uniting.android.msic.R.string.time_format), 0, 0));
                            timeDisplay.setTextColor(Color.RED);
                            playSiren();
                        }
                    } else {

                        //one minute left
                        if (minutes + 1 >= time && !alarmStarted) {
                            Log.d(TAG, "playing alarm");

                            setAlarmVolumeMax();
                            if (!mediaPlayer.isPlaying())
                                startAlarm();
                            alarmStarted = true;
                        }else if(minutes + 1 >= time && alarmStarted){
                            //vibrate?
                        }

                        if (minutes != 0 || seconds != 0) {
                            timeDisplay.setText(String.format(getString(com.uniting.android.msic.R.string.time_format), (time - minutes - 1), (60 - seconds)));
                        }

                        double currentSeconds = ((time - minutes - 1) * 60) + (60 - seconds);
                        double definedSeconds = (time * 60);
                        double p = (100) - (currentSeconds / definedSeconds) * 100;
                        progressCircle.setProgress(0);
                        progressCircle.setProgress((int) p);
                    }
                }
                Log.d(TAG, "run() called");
                handler.postDelayed(this, 0);
            }
        };
    }

    private void initializeEmergencyProcedure(){

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.disclaimer: {
                Intent intent = new Intent(this, InfoTextActivity.class);
                intent.putExtra("activity_title", getResources().getString(R.string.disclaimer));
                intent.putExtra("bodies", getResources().getStringArray(R.array.conditions));
                startActivity(intent);
                break;
            }

            case R.id.drug_safety_information: {
                Intent intent = new Intent(this, InfoTextActivity.class);
                intent.putExtra("activity_title", getResources().getString(R.string.drug_safety_information));
                intent.putExtra("titles", getResources().getStringArray(R.array.drug_safety_titles));
                intent.putExtra("bodies", getResources().getStringArray(R.array.drug_safety_bodies));
                intent.putExtra("collapse", false);
                startActivity(intent);
                break;
            }

            case R.id.how_to_use_the_app: {
                Intent intent = new Intent(this, InfoTextActivity.class);
                intent.putExtra("activity_title", getResources().getString(R.string.how_to_use_the_app));
                intent.putExtra("titles", getResources().getStringArray(R.array.how_to_titles));
                intent.putExtra("bodies", getResources().getStringArray(R.array.how_to_bodies));
                intent.putExtra("collapse", true);
                startActivity(intent);
                break;
            }
        }

        DrawerLayout drawer = getWindow().getDecorView().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
