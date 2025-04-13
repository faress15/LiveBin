package org.bihe.livebin.utils;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.bihe.livebin.data.AppData;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.network.impl.LocationServiceImpl;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.dao.LocationDao;
import org.bihe.livebin.data.model.LocationObj;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationService extends Service {

    private static final long LOCATION_UPDATE_INTERVAL = 5000;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private LocationDao locationDao;
    private LocationServiceImpl locationService;

    private User currentUser;

    private ExecutorService executorService;

    private static final String CHANNEL_ID = "location_service_channel";
    private static final int NOTIFICATION_ID = 1;


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        locationService = new LocationServiceImpl(this);
        locationDao = DbManager.getInstance(this).locationDao();
        currentUser = AppData.getInstance().getCurrentUser();
        executorService = Executors.newCachedThreadPool();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    chatgpt
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_NOT_STICKY;
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        startLocationUpdates();

        return START_STICKY;
    }


//    chatgpt
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL / 2);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLastLocation();
                    checkInternet(location);
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            stopSelf();
        } catch (Exception e) {
            stopSelf();
        }
    }

    private void checkInternet(Location location) {
        if (location != null){
            saveLocationLocally(location);

            if (isInternetAvailable()) {
                syncOfflineLocations();
            }
        }else {
            Log.e("location", "location is null");
        }
    }


    private void syncOfflineLocations() {
        executorService.execute(() -> {
            try {
                List<LocationObj> draftLocations = locationDao.getLocationsByStatus("draft");
                if (draftLocations.isEmpty()) {
                    return;
                }
                for (LocationObj draftLocation : draftLocations) {
                    locationService.registerLocation(draftLocation, new ResultListener<LocationObj>() {
                        @Override
                        public void onResult(LocationObj result) {
                            if (result != null && result.getServerId() != 0) {
                                draftLocation.setServerId(1);
                                draftLocation.setStatus("sent");

                                executorService.execute(() -> {
                                    locationDao.update(draftLocation);
                                });
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            showToastMessage(throwable.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
            }
        });
    }


    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //    chatgpt
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) {
                return false;
            }
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (networkCapabilities == null) {
                return false;
            }

            boolean isWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            boolean isCellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            boolean isEthernet = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

            return isWifi || isCellular || isEthernet;
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    private void saveLocationLocally(Location location) {
        LocationObj appLocation = new LocationObj(currentUser.getId(), location.getLatitude(), location.getLongitude(), location.getTime(), "draft");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    locationDao.insert(appLocation);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LocationService.this, "ذخیره لوکیشن به مشکل خورد!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //    chatgpt
    private void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopLocationUpdates();
        stopSelf();
    }


}
