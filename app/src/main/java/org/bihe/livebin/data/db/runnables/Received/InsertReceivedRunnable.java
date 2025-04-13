package org.bihe.livebin.data.db.runnables.Received;

import android.content.Context;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.model.Received;

public class InsertReceivedRunnable implements Runnable{

    private final Context context;
    private final Received received;
    private final DbResponse<Received> dbResponse;

    public InsertReceivedRunnable(Context context, Received received, DbResponse<Received> dbResponse) {
        this.context = context;
        this.received = received;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        long id = dbManager.receivedDao().insert(received);

        if (id > 0) {
            dbResponse.onSuccess(received);
        } else {
            dbResponse.onError(new Error(String.valueOf(R.string.received_error_insert_failed)));
        }
    }
}
