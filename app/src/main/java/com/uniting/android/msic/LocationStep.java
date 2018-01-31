package com.uniting.android.msic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

/**
 * Created by jeeppeck on 1/30/18.
 */

public class LocationStep extends Fragment implements Step {

    private final static String TAG = "LocationStep";

    private Switch locationSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_step, container, false);
        locationSwitch = v.findViewById(R.id.location_switch);
        return v;
    }

    @Override
    public VerificationError verifyStep(){
        Prefs.setLoc(getContext(), locationSwitch.isChecked());
        return null;
    }

    @Override
    public void onSelected(){
    }

    @Override
    public void onError(@NonNull VerificationError error){
        //handle error
    }
}
