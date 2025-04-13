package org.bihe.livebin.ui.activities;


import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_DAY_OF_BIRTH;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_IDENTIFICATION_CODE;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_IS_LOGIN;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_MONTH_OF_BIRTH;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_NAME;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_PASSWORD;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_PERFORMANCE;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_USER_ID;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_YEAR_OF_BIRTH;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.bihe.livebin.R;
import org.bihe.livebin.data.AppData;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.prefrences.PreferencesManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PreferencesManager preferencesManager = PreferencesManager.getInstance(this);
        boolean hasLoggedIn = preferencesManager.get(PREF_KEY_IS_LOGIN, false);

        Intent intent;
        if (hasLoggedIn) {
            User user = new User(preferencesManager.get(PREF_KEY_USER_ID, ""), preferencesManager.get(PREF_KEY_NAME, ""), preferencesManager.get(PREF_KEY_PASSWORD, ""), preferencesManager.get(PREF_KEY_IDENTIFICATION_CODE, ""), preferencesManager.get(PREF_KEY_PERFORMANCE, ""), preferencesManager.get(PREF_KEY_DAY_OF_BIRTH, 0), preferencesManager.get(PREF_KEY_MONTH_OF_BIRTH, 0), preferencesManager.get(PREF_KEY_YEAR_OF_BIRTH, 0));
            AppData.getInstance().setCurrentUser(user);
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}