package com.uniting.android.msic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

public class SetupActivity extends AppCompatActivity implements StepperLayout.StepperListener{

    private final static String TAG = "SetUpActivity";

    private StepperLayout stepperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        stepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        stepperLayout.setAdapter(new StepAdapter(getSupportFragmentManager(), this));
        stepperLayout.setListener(this);
    }

    @Override
    public void onCompleted(View completeButton) {
        Prefs.setSetupComplete(this, true);
        startMainActivity();
    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
        hideKeyboard();
    }

    @Override
    public void onReturn() {

    }

    private void hideKeyboard(){
        if(getCurrentFocus() != null){
            Log.d(TAG, "we should be hiding the keyboard");
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void startMainActivity() {
        Log.d(TAG, "starting main activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
