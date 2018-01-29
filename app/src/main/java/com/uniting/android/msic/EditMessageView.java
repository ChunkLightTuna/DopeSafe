package com.uniting.android.msic;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by jeeppeck on 1/28/18.
 */

public class EditMessageView extends LinearLayout {

    private static final String TAG = "EditMessageView";

    private static final String REMAINING_FORMAT = "%1$d/%2$d";
    LayoutInflater inflater;
    EditText messageField;

    public EditMessageView(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        init();
    }

    public void init(){
        int maxChars = Prefs.isLoc(this.getContext()) ? 120 : 160;
        View view = inflater.inflate(R.layout.edit_message_view, this, true);
        TextView textCounter = view.findViewById(R.id.text_counter);
        messageField = view.findViewById(R.id.message_field);
        textCounter.setText(String.format(Locale.getDefault(), REMAINING_FORMAT, 0, maxChars));
        messageField.setMinLines(5);
        messageField.setMaxLines(6);
        messageField.setSelectAllOnFocus(true);
        messageField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxChars)});

        messageField.addTextChangedListener(new TextWatcher() {
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

    public String getMessage(){
       return messageField.getText().toString();
    }

    public void setMessage(String message){
       Log.d(TAG, "Message: " + message);
       messageField.setText(message);
       Log.d(TAG, "Message Field: " + messageField.getText());
    }
}
