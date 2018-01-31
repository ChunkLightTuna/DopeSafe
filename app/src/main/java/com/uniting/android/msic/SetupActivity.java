package com.uniting.android.msic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stepstone.stepper.StepperLayout;

public class SetupActivity extends AppCompatActivity {

    private StepperLayout stepperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        stepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        stepperLayout.setAdapter(new StepAdapter(getSupportFragmentManager(), this));
    }

}
