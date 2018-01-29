package com.uniting.android.msic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

//    private Prefs prefs;

    private TextView timeDisplay;
    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;
    //private Switch stopButton;
    private ProgressBar progressCircle;
    SwipeButton stopButton;
    private long startTime = 0L;
    private long pauseTime = 0L;
    private long timeInMilliseconds = 0L;
    private long updatedTime = 0L;

    private boolean timerStopped = true;
    private boolean timerPaused = false;

    private int seconds = 0;
    private int minutes = 0;

    private boolean alarmStarted = false;

    private Handler handler = new Handler();
    private Runnable timer;

    private Ringtone ringtone;
    private Vibrator vibrator;
    private LocationService locationService;

    private AlertDialog alertDialog;
    private DrawerLayout drawer;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

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

        this.timeDisplay = findViewById(R.id.time);

        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> confirmInitializeOfSession());

        pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(v -> pauseTimer());

        resumeButton = findViewById(R.id.resume_button);
        resumeButton.setOnClickListener(view -> resumeTimer());

        stopButton = findViewById(R.id.stop_button);
        stopButton.setVisibility(View.INVISIBLE);
        stopButton.setOnActiveListener(this::stopTimer);

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
//        locationService = new LocationService(this);
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

        updateTime();
        super.onResume();

    }

    /**
     * https://developer.android.com/guide/topics/location/strategies.html#BestPerformance
     */
    private void tryToInitLocationService() {
        if (Permissions.locationGranted(this))
            locationService = new LocationService(this);
    }

    public void updateTime() {

        int timeInMinutes = Integer.valueOf(sharedPreferences.getString(getString(R.string.countdown_time_key), "5"));
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
        ringtone.stop();
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

        stopButton.setVisibility(View.INVISIBLE);
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
        ringtone.stop();
        vibrator.cancel();
        timerStopped = true;
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
        } catch (Exception e) {
            Log.e(TAG, "SMS failed!", e);
        }
    }

    @SuppressLint("DefaultLocale")
    public String getGoogleMapsUrl(Location location) {
        String latitude = String.format("%1$,.6f", location.getLatitude());
        String longitude = String.format("%1$,.6f", location.getLongitude());
        return "google.com/maps?q=" + latitude + "," + longitude;
    }


    /**
     * https://developer.android.com/guide/topics/media-apps/volume-and-earphones.html
     */
    private void setRingVolumeMax() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (am != null) {
            am.setStreamVolume(
                    AudioManager.STREAM_ALARM,
                    am.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                    0);
        }
    }

    private Runnable getTimer() {


        return new Runnable() {

            final int time = Integer.valueOf(sharedPreferences.getString(getString(R.string.countdown_time_key), "5"));
            final String message = sharedPreferences.getString(getString(R.string.emergency_message_key), getString(R.string.pref_default_emergency_message));
            final String phoneNumber = sharedPreferences.getString(getString(R.string.emergency_contact_key), getString(R.string.pref_default_contact));
            final boolean location = sharedPreferences.getBoolean(getString(R.string.enable_location_key), false);

            public void run() {
                if (!timerPaused) {

                    timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                    updatedTime = timeInMilliseconds;

                    int remaining = (int) (updatedTime / 1000);
                    minutes = remaining / 60;
                    seconds = remaining % 60;

                    //times up
                    if (minutes == time) {

                        //TODO ask for location permissions
                        if (location) {
                            sendSMS(phoneNumber, message, locationService.getLocation());
                        } else {
                            sendSMS(phoneNumber, message);
                        }

                        timeDisplay.setText(String.format(getString(com.uniting.android.msic.R.string.time_format), 0, 0));
                        timeDisplay.setTextColor(Color.RED);
                    } else {

                        //one minute left
                        if (minutes + 1 >= time && !alarmStarted) {
                            Log.d(TAG, "playing alarm");

                            if (ringtone == null) {
                                ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                            }
                            setRingVolumeMax();
                            ringtone.play();
                            vibrator.vibrate(200);

                            alarmStarted = true;
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected() called with: " + "item = [" + item + "]");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.get_help) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(getResources().getString(R.string.help_website)));
//            startActivity(intent);
//        } else

        switch (id) {


            case R.id.settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.disclaimer: {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.disclaimer)
                        .setMessage(R.string.conditions)
                        .setPositiveButton(R.string.ok, (dialog, which) -> {})
                        .show();
                break;
            }

            case R.id.drug_safety_information: {
                Intent intent = new Intent(this, InfoTextActivity.class);
                intent.putExtra("title", getResources().getString(R.string.drug_safety_information));
                intent.putExtra("titles", getResources().getStringArray(R.array.drug_safety_titles));
                intent.putExtra("bodies", getResources().getStringArray(R.array.drug_safety_bodies));
                intent.putExtra("collapse", false);
                startActivity(intent);
                break;
            }

            case R.id.how_to_use_the_app: {
                Intent intent = new Intent(this, InfoTextActivity.class);
                intent.putExtra("title", getResources().getString(R.string.how_to_use_the_app));
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
