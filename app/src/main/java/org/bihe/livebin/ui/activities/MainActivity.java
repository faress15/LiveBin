package org.bihe.livebin.ui.activities;

import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_IS_LOGIN;
import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_PERFORMANCE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import org.bihe.livebin.R;
import org.bihe.livebin.data.AppData;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.prefrences.PreferencesManager;
import org.bihe.livebin.databinding.ActivityMainBinding;
import org.bihe.livebin.utils.DialogHelper;
import org.bihe.livebin.utils.LocationService;
import org.bihe.livebin.utils.SyncService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferencesManager preferencesManager;
    private ExecutorService executorService;
    private ProgressDialog dialog;

    private User currentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);

        init();
        checkUser();
        setListeners();
    }


    private void init() {
        currentUser = AppData.getInstance().getCurrentUser();
        executorService = Executors.newCachedThreadPool();
        preferencesManager = PreferencesManager.getInstance(this);
        dialog = DialogHelper.getLoadingDialog(this);
    }

    private void checkUser(){
        String userPerformance = preferencesManager.get(PREF_KEY_PERFORMANCE, "");
        if (userPerformance.equals(getString(R.string.performance1))) {
            binding.received.setVisibility(View.GONE);
            binding.receivedIcon.setVisibility(View.GONE);
        } else {
            binding.received.setVisibility(View.VISIBLE);
        }
    }

    private void setListeners() {
        binding.addCustomer.setOnClickListener(v -> goToCustomersScreen());
        binding.received.setOnClickListener(v -> goToAddReceivedScreen());
        binding.map.setOnClickListener(v -> goToMapScreen());
        binding.setting.setOnClickListener(v -> goToSettingScreen());
    }

    private void goToCustomersScreen() {
        Intent intent = new Intent(this, CustomersActivity.class);
        startActivity(intent);
    }

    private void goToAddReceivedScreen() {
        Intent intent = new Intent(this, ReceivedActivity.class);
        startActivity(intent);
    }

    private void goToMapScreen() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void goToSettingScreen() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        preferencesManager.put(PREF_KEY_IS_LOGIN, false);
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
        Intent syncIntent = new Intent(this, SyncService.class);
        stopService(syncIntent);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        executorService.shutdown();
    }
}
