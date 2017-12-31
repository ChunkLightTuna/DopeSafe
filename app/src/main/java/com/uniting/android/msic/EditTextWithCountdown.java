package com.uniting.android.msic;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Chris.Oelerich on 11/25/2017.
 */

public class EditTextWithCountdown extends EditTextPreference {
    private static final String TAG = "EditTextWithCountdown";
    private TextView textCounter;
    private int maxChars;
    private static final String REMAINING_FORMAT = "%1$d/%2$d";

    /**
     * for now we're only instantiating this programmatically, but to do it proper through XML would require this method
     * https://developer.android.com/training/custom-views/create-view.html
     * @param context Context
     * @param attrs AttributeSet
     */
    public EditTextWithCountdown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    EditTextWithCountdown(Context context, int maxChars, String title, String defaultValue, String key, String summary) {
        super(context);
        this.textCounter = new TextView(context);
        this.maxChars = maxChars;

        getEditText().setId(View.generateViewId());
        Log.wtf(TAG, "EditTextWithCountdown: " + getEditText().getId());

        setDefaultValue(defaultValue);
        setKey(key);
        //        getEditText().setInputType(EditorInfo.);
        getEditText().setMaxLines(6);
        getEditText().setSelectAllOnFocus(true);
        getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxChars)});
        setText(title);
        setSummary(summary);

        textCounter.setText(String.format(Locale.getDefault(), REMAINING_FORMAT, getSummary().length(), maxChars));
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        final LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        getDialog().addContentView(textCounter, params);

        getEditText().addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textCounter.setText(String.format(
                        Locale.getDefault(),
                        REMAINING_FORMAT,
                        s.length(),
                        maxChars
                ));
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }
}
