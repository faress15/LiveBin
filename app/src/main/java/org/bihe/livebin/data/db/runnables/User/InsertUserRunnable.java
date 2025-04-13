package org.bihe.livebin.data.db.runnables.User;

import android.content.Context;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.model.User;

public class InsertUserRunnable implements Runnable {

    private final Context context;
    private final User user;
    private final DbResponse<User> dbResponse;

    public InsertUserRunnable(Context context, User user, DbResponse<User> dbResponse) {
        this.context = context;
        this.user = user;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        long id = dbManager.userDao().insert(user);

        if (id > 0) {
            dbResponse.onSuccess(user);
        } else {
            dbResponse.onError(new Error(String.valueOf(R.string.user_error_insert_failed)));
        }
    }
}
