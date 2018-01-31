package com.uniting.android.msic;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

//    public LinearLayout getDialogContent() {
//        LinearLayout dialogLayout = new LinearLayout(context);
//        dialogLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        dialogLayout.setOrientation(LinearLayout.VERTICAL);
//        dialogLayout.addView(getContent("Emergency Contact:", Prefs.getPhone(context)));
//        dialogLayout.addView(getContent("Message:", Prefs.getMsg(context)));
//        dialogLayout.addView(getContent("Time:", "" + Prefs.getTime(context)));
//        dialogLayout.addView(getContent("Location Enabled:", "" + Prefs.isLoc(context)));
//        return dialogLayout;
//    }

    public TableLayout getDialogContent(){
        TableLayout dialogLayout = new TableLayout(context);
        dialogLayout.setPadding(20, 0,20,0 );
        dialogLayout.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dialogLayout.addView(getContent("Emergency Contact:", Prefs.getPhone(context)));
        dialogLayout.addView(getContent("Message:", Prefs.getMsg(context)));
        dialogLayout.addView(getContent("Time:", "" + Prefs.getTime(context)));
        dialogLayout.addView(getContent("Location Enabled:", "" + Prefs.isLoc(context)));
        return dialogLayout;
    }

    public TableRow getContent(String label, String value) {
        TableRow tableRow = getRow();
        TextView labelView = getBoldTextView();
        TextView valueView = getTextView();
        labelView.setText(label);
        valueView.setText(value);
        tableRow.addView(labelView);
        tableRow.addView(valueView);
        return tableRow;
    }

    public TableRow getRow(){
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tableRow.setBackgroundColor(context.getResources().getColor(R.color.colorMaterialGrey300));
        return tableRow;
    }

    public TextView getBoldTextView() {
        TextView textView = new TextView(context);
        textView.setTypeface(null, BOLD);
        textView.setGravity(Gravity.END);
        textView.setBackgroundColor(context.getColor(R.color.unitingPurple));
        return textView;
    }

    public TextView getTextView() {
        TextView textView = new TextView(context);
        textView.setBackgroundColor(context.getColor(R.color.unitingYellow));
        textView.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        textView.setPadding(10, 0 ,0 ,0);
//        textView.setGravity(Gravity.START);
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
