package org.bihe.livebin.data;

import android.app.Application;


import org.bihe.livebin.data.model.User;


public class AppData extends Application {

    private static AppData appData;

    private User currentUser;


    public static AppData getInstance() {
        return appData;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appData = this;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

}

