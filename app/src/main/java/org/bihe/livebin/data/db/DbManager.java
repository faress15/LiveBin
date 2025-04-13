package org.bihe.livebin.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import org.bihe.livebin.data.db.dao.CustomerDao;
import org.bihe.livebin.data.db.dao.LocationDao;
import org.bihe.livebin.data.db.dao.ReceivedDao;
import org.bihe.livebin.data.db.dao.UserDao;
import org.bihe.livebin.data.model.Customer;
import org.bihe.livebin.data.model.LocationObj;
import org.bihe.livebin.data.model.Received;
import org.bihe.livebin.data.model.User;

@Database(entities = {User.class, Customer.class, LocationObj.class, Received.class}, version = 74, exportSchema = false)
public abstract class DbManager extends RoomDatabase {

    private final static String DB_NAME = "LiveBin";

    private static DbManager instance;

    public abstract UserDao userDao();

    public abstract CustomerDao customerDao();

    public abstract LocationDao locationDao();

    public abstract ReceivedDao receivedDao();

    public static synchronized DbManager getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, DbManager.class, DB_NAME)
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
