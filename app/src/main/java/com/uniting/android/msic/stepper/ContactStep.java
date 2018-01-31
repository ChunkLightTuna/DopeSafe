package com.uniting.android.msic.stepper;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.uniting.android.msic.Prefs;
import com.uniting.android.msic.R;

/**
 * Created by jeeppeck on 1/30/18.
 */

public class ContactStep extends Fragment implements Step {

    private final static String TAG = "ContactStep";
    private EditText phoneField;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.emergency_contact_step, container, false);
        phoneField = v.findViewById(R.id.initial_phone_field);
        return v;
    }


    @Override
    public VerificationError verifyStep(){
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneField.getText().toString())){
            Prefs.setPhone(getContext(),  phoneField.getText().toString());
            return null;
        }else{
            return new VerificationError(getString(R.string.phone_verification_error));
        }
    }

    @Override
    public void onSelected(){
    }

    @Override
    public void onError(@NonNull VerificationError error){
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.error))
                .setMessage(error.getErrorMessage())
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialog, which) -> {})
                .create()
                .show();
    }

}
