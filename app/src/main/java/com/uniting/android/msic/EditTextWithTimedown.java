package com.uniting.android.msic;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

/**
 * Created by Chris.Oelerich on 11/25/2017.
 */

public class EditTextWithTimedown extends EditTextPreference {
    private TextView textCounter;

    EditTextWithTimedown(Context context) {
        super(context);

        textCounter =  new TextView(context);
        textCounter.setText("lolol");

        getEditText().setId(View.generateViewId());
//        getEditText().setInputType(EditorInfo.);
        setDefaultValue(context.getString(R.string.pref_default_emergency_message));
        setKey(context.getString(R.string.emergency_message_key));
        getEditText().setMaxLines(4);
        getEditText().setSelectAllOnFocus(true);
        setTitle(context.getString(R.string.emergency_contact));

        setSummary("asdfasdf");
        textCounter.setText(String.format(context.getString(R.string.chars_remaining_format), getSummary().length()));

    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        getDialog().addContentView(textCounter, textCounter.getLayoutParams());


        getEditText().addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textCounter.setText(String.format(
                        getContext().getString(R.string.chars_remaining_format),
                        s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }
}
