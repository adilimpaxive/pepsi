package com.pepsi.battleofthebands.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Toast;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.app.PepsiApplication;

public class AppDialog {

    public static void showToast(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static Dialog getProgressDialog(Context context) {
        Dialog dialog;
        if (context != null) {
            dialog = new Dialog(context);
        } else {
            dialog = new Dialog(PepsiApplication.getAppContext());
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_bar);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        return dialog;
    }
}
