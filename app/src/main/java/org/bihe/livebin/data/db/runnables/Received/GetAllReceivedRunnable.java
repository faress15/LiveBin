package org.bihe.livebin.data.db.runnables.Received;

import android.content.Context;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.model.Received;


import java.util.List;

public class GetAllReceivedRunnable implements Runnable {

    private final Context context;
    private final DbResponse<List<Received>> dbResponse;

    public GetAllReceivedRunnable(Context context, DbResponse<List<Received>> dbResponse) {
        this.context = context;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        List<Received> receipts = dbManager.receivedDao().getAllReceipts();
        if (receipts == null) {
            dbResponse.onError(new Error(String.valueOf(R.string.error_get_all_Receipts_failed)));
        } else {
            dbResponse.onSuccess(receipts);
        }
    }
}
