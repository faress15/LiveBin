package org.bihe.livebin.utils;

import android.app.ProgressDialog;
import android.content.Context;

import org.bihe.livebin.R;

public class DialogHelper {
    public static ProgressDialog getLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.title_dialog));
        progressDialog.setMessage(context.getString(R.string.title_dialog_message));
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }
}
