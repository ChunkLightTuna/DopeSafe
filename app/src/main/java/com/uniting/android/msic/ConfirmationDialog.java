package com.uniting.android.msic;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.Callable;

import static android.graphics.Typeface.BOLD;

/**
 * Created by jeeppeck on 1/27/18.
 */

public class ConfirmationDialog {
    private static String TAG = "ConfirmationDialog";
    private Context context;
    private Callable<Void> continueCallback;
    private Callable<Void> editCallback;

    ConfirmationDialog(Context context) {
        this.context = context;
    }

    public ConfirmationDialog setContinueCallback(Callable<Void> continueCallback) {
        this.continueCallback = continueCallback;
        return this;
    }

    public ConfirmationDialog setEditCallback(Callable<Void> editCallback) {
        this.editCallback = editCallback;
        return this;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(R.string.confirmation_dialog_title).setView(getDialogContent())
                .setView(getDialogContent())
                .setPositiveButton("Continue", (dialog, which) -> ConfirmationDialog.this.executeCallback(continueCallback))
                .setNegativeButton("Edit", (dialog, which) -> ConfirmationDialog.this.executeCallback(editCallback))
                .setCancelable(false)
                .create()
                .show();
    }

    public LinearLayout getDialogContent() {
        LinearLayout dialogLayout = new LinearLayout(context);
        dialogLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.addView(getContent("Emergency Contact:", Prefs.getPhone(context)));
        dialogLayout.addView(getContent("Message:", Prefs.getMsg(context)));
        dialogLayout.addView(getContent("Time:", "" + Prefs.getTime(context)));
        dialogLayout.addView(getContent("Location Enabled:", "" + Prefs.isLoc(context)));
        return dialogLayout;
    }

    public LinearLayout getContent(String label, String value) {
        LinearLayout phoneContent = getLinearLayout();
        TextView labelView = getBoldTextView();
        TextView valueView = getTextView();
        labelView.setText(label);
        valueView.setText(value);
        phoneContent.addView(labelView);
        phoneContent.addView(valueView);
        return phoneContent;
    }

    public LinearLayout getLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    public TextView getBoldTextView() {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, .5f));
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(20, 0, 0, 0);
        textView.setTypeface(null, BOLD);
        return textView;
    }

    public TextView getTextView() {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, .5f));
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(20, 0, 0, 0);
        return textView;
    }


    private void executeCallback(Callable<Void> callback) {
        if (callback != null)
            try {
                callback.call();
            } catch (Exception e) {
                Log.e(TAG, "unable to execute callback");
            }
    }

}
