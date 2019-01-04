package com.jiafrank.keepreceipt.service;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class UIService {

    public static AlertDialog.OnClickListener DISMISS_ALERT_DIALOG_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public static AlertDialog getAlertDialog(Context context, String title, String message,
                                             String positiveText, DialogInterface.OnClickListener positiveListener,
                                             String negativeText, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(positiveText, positiveListener);
        alertDialogBuilder.setNegativeButton(negativeText, negativeListener);
        return alertDialogBuilder.create();
    }

    /**
     * Go back to the previous activity indicating success or not
     *
     * @param succeeded whether the action was successful or not
     */
    public static void finishActivity(Activity activity, boolean succeeded) {
        if (succeeded) {
            activity.setResult(RESULT_OK);
        } else {
            activity.setResult(RESULT_CANCELED);
        }
        activity.finish();
    }

}
