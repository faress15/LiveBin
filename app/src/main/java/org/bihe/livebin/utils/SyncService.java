package org.bihe.livebin.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.dao.CustomerDao;
import org.bihe.livebin.data.db.dao.ReceivedDao;
import org.bihe.livebin.data.model.Customer;
import org.bihe.livebin.data.model.Received;
import org.bihe.livebin.data.network.impl.CustomerServiceImpl;
import org.bihe.livebin.data.network.impl.ReceivedServiceImpl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncService extends Service {

    private final Handler handler = new Handler();
    private final int SYNC_INTERVAL = 10000;
    private ExecutorService executorService;
    private ReceivedServiceImpl receivedService;
    private CustomerServiceImpl customerService;
    private CustomerDao customerDao;

    private ReceivedDao receivedDao;


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        receivedService = new ReceivedServiceImpl(this);
        customerService = new CustomerServiceImpl(this);
        customerDao = DbManager.getInstance(this).customerDao();
        receivedDao = DbManager.getInstance(this).receivedDao();
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkInternet();
                handler.postDelayed(this, SYNC_INTERVAL);
            }
        }, SYNC_INTERVAL);
        return START_STICKY;
    }

    private void checkInternet(){
        if (isInternetAvailable()){
            syncDataWithServer();
        }
    }

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

    private void syncDataWithServer() {
        syncReceivedDataWithServer();
        syncCustomerDataWithServer();
    }
    private void syncReceivedDataWithServer() {
        executorService.execute(() -> {
            try {
                List<Received> draftReceipts = receivedDao.getReceiptsByStatus("draft");
                if (draftReceipts.isEmpty()) {
                    return;
                }
                for (Received draftReceipt : draftReceipts) {
                    receivedService.insertReceived(draftReceipt, new ResultListener<Received>() {
                        @Override
                        public void onResult(Received result) {
                            if (result != null && result.getServerId() != 0) {
                                draftReceipt.setServerId(1);
                                draftReceipt.setStatus("sent");

                                executorService.execute(() -> {
                                    receivedDao.update(draftReceipt);
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

    private void syncCustomerDataWithServer() {
        executorService.execute(() -> {
            try {
                List<Customer> draftCustomers = customerDao.getCustomersByStatus("draft");
                if (draftCustomers.isEmpty()) {
                    return;
                }
                for (Customer draftCustomer : draftCustomers) {
                    customerService.insertCustomer(draftCustomer, new ResultListener<Customer>() {
                        @Override
                        public void onResult(Customer result) {
                            if (result != null && result.getServerId() != 0) {
                                draftCustomer.setServerId(1);
                                draftCustomer.setStatus("sent");

                                executorService.execute(() -> {
                                    customerDao.update(draftCustomer);
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
                showToastMessage(e.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

