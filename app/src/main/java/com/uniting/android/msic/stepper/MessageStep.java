package com.uniting.android.msic.stepper;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.uniting.android.msic.Prefs;
import com.uniting.android.msic.R;

/**
 * Created by jeeppeck on 1/30/18.
 */

public class MessageStep extends Fragment implements Step {

    private final static String TAG = "MessageStep";
    private int maxChars;
    private TextView charRemainingView;
    private EditText messageField;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.message_step, container, false);
        charRemainingView = v.findViewById(R.id.characters_remaining);
        messageField = v.findViewById(R.id.initial_message);
        messageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharsRemainingField();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            setMaxChars();
            if(isMessageWithinLength())
                updateCharsRemainingField();
            else
                handleMessageToLong();
        }
    }

    private void updateCharsRemainingField(){
        int charsRemaining = maxChars - messageField.length();
        charRemainingView.setText(charsRemaining + getString(R.string.chars_remaining));
    }

    private boolean isMessageWithinLength(){
      return maxChars >= messageField.length();
    }

    private void handleMessageToLong(){
        String newMessage = messageField.getText().toString().substring(0, maxChars);
        messageField.setText(newMessage);
        updateCharsRemainingField();
        showErrorDialog(getResources().getString(R.string.message_shortened_error));
    }
    public void showErrorDialog(String message){
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setMessage(message)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, which) -> {})
                .create()
                .show();
    }

    private void setMaxChars(){
        maxChars = (Prefs.isLoc(getContext())? 120: 160);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxChars);
        messageField.setFilters(fArray);
    }

    @Override
    public VerificationError verifyStep(){
        if(messageField.length() <= 0)
           return new VerificationError(getString(R.string.message_verification_error_empty));
        else if(messageField.length() > maxChars)
            return new VerificationError(getString(R.string.message_verification_error_long));

        Prefs.setMsg(getContext(), messageField.getText().toString());
        return null;
    }

    @Override
    public void onSelected(){
    }

    @Override
    public void onError(@NonNull VerificationError error){
        showErrorDialog(error.getErrorMessage());
    }
}
