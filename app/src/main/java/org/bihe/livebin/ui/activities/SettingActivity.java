package org.bihe.livebin.ui.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.bihe.livebin.R;
import org.bihe.livebin.databinding.ActivitySettingBinding;
import org.bihe.livebin.utils.LocationService;

public class SettingActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private ActivitySettingBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpToolbar();

        binding.startButton.setOnClickListener(v -> {
            requestLocationPermission();
        });

        binding.stopButton.setOnClickListener(v -> {
            stopLocationService();
        });

    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable backDrawable = ContextCompat.getDrawable(this, R.drawable.ic_back);
        actionBar.setHomeAsUpIndicator(backDrawable);
    }

    //    android developer
    private void requestLocationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                ActivityCompat.requestPermissions(this, new String[]{
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                }, LOCATION_PERMISSION_REQUEST_CODE);
//            } else {
//                startLocationService();
//            }
//        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                startLocationService();
            }

    }

    //    chatgpt
    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            Toast.makeText(this, getString(R.string.location_service_started), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.location_service_already_running), Toast.LENGTH_SHORT).show();
        }
    }

    //    chatgpt
    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            stopService(serviceIntent);
            Toast.makeText(this, getString(R.string.location_service_stopped), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.location_service_not_running), Toast.LENGTH_SHORT).show();
        }
    }

//    chatgpt
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    //    android developer
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(this, "دسترسی به لوکیشن رد شد!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
