package com.uniting.android.msic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

/**
 * Created by jeeppeck on 1/30/18.
 */

public class StepAdapter extends AbstractFragmentStepAdapter {
    private final static String TAG = "AbstractFragmentStepAdapter";

    private final String CURRENT_STEP_POSITION_KEY = "CURRENT_STEP_POSITION_KEY";

    public StepAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position){
        final Step step = getCurrentStep(position);
        return step;
    }

    public int getCount(){
        return 4;
    }

    @Override
    public Step findStep(int position) {
        return super.findStep(position);
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(int position) {
        return new StepViewModel.Builder(context)
                .setTitle(getCurrentTitle(position))
                .create();
    }

    public Step getCurrentStep(int position){
       Step retStep = new ContactStep();
       switch(position) {
           case 0:
               retStep = new ContactStep();
               break;
           case 1:
               retStep = new LocationStep();
               break;
           case 2:
               retStep = new MessageStep();
               break;
           case 3:
               retStep = new TimeStep();
               break;
       }
       return retStep;
    }

    public String getCurrentTitle(int position){
        switch(position){
            case 0:
              return context.getString(R.string.phone_tab_title);
            case 1:
                return context.getString(R.string.location_tab_title);
            case 2:
                return context.getString(R.string.message_tab_title);
            case 3:
                return context.getString(R.string.time_tab_title);
            default:
                return "";
        }
    }
}
