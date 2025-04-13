package org.bihe.livebin.data.db.runnables.Location;

import android.content.Context;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.model.LocationObj;

public class GetLastLocationOfUserRunnable implements Runnable {

    private final Context context;
    private final String userId;
    private final DbResponse<LocationObj> dbResponse;

    public GetLastLocationOfUserRunnable(Context context, String userId, DbResponse<LocationObj> dbResponse) {
        this.context = context;
        this.userId = userId;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        LocationObj locationObj = dbManager.locationDao().getLastLocationOfUser(userId);

        if (locationObj != null) {
            dbResponse.onSuccess(locationObj);
        } else {
            dbResponse.onError(new Error(String.valueOf(R.string.error_get_last_location_failed)));
        }
    }
}
