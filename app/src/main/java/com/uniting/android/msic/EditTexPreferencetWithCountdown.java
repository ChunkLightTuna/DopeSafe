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
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Chris.Oelerich on 11/25/2017.
 */

public class EditTexPreferencetWithCountdown extends EditTextPreference {
    private static final String TAG = "EditTexPreferencetWithCountdown";
    private int maxChars;
    private static final String REMAINING_FORMAT = "%1$d/%2$d";

    /**
     * for now we're only instantiating this programmatically, but to do it proper through XML would require this method
     * https://developer.android.com/training/custom-views/create-view.html
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    public EditTexPreferencetWithCountdown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    EditTexPreferencetWithCountdown(Context context, int maxChars, String title, String defaultValue, String key, String summary) {
        super(context);
        this.maxChars = maxChars;

        getEditText().setId(View.generateViewId());
        Log.wtf(TAG, "EditTexPreferencetWithCountdown: " + getEditText().getId());

        setDefaultValue(defaultValue);
        setKey(key);
        getEditText().setInputType(EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        getEditText().setMaxLines(6);
        getEditText().setSelectAllOnFocus(true);
        getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxChars)});
//        setText(title);
        setTitle(title);
        setSummary(summary);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        TextView textCounter = new TextView(getContext());
        textCounter.setText(String.format(Locale.getDefault(), REMAINING_FORMAT, getSummary().length(), maxChars));

        final LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.setMargins(params.leftMargin + 4, params.topMargin + 4, params.rightMargin, params.bottomMargin);

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
