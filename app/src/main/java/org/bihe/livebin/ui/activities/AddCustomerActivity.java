package org.bihe.livebin.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.bihe.livebin.R;
import org.bihe.livebin.data.AppData;
import org.bihe.livebin.data.db.DbResponse;

import org.bihe.livebin.data.db.runnables.Customer.InsertCustomerRunnable;
import org.bihe.livebin.data.db.runnables.Location.GetLastLocationOfUserRunnable;
import org.bihe.livebin.data.model.Customer;
import org.bihe.livebin.data.model.LocationObj;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.network.impl.CustomerServiceImpl;
import org.bihe.livebin.data.network.impl.LocationServiceImpl;
import org.bihe.livebin.databinding.ActivityAddCustomerBinding;
import org.bihe.livebin.utils.Constant;
import org.bihe.livebin.utils.DialogHelper;
import org.bihe.livebin.utils.ResultListener;
import org.bihe.livebin.utils.Validator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddCustomerActivity extends AppCompatActivity {


    private ActivityAddCustomerBinding binding;
    private ExecutorService executorService;
    private ProgressDialog dialog;
    private CustomerServiceImpl customerService;

    private LocationServiceImpl locationService;

    private User currentUser;

    private double latitude;

    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCustomerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        init();
        setUpToolbar();
        setListeners();

        binding.locationButton.setOnClickListener(v -> checkInternetAndProceed());
    }

    private void init() {
        currentUser = AppData.getInstance().getCurrentUser();
        executorService = Executors.newCachedThreadPool();
        customerService = new CustomerServiceImpl(this);
        dialog = DialogHelper.getLoadingDialog(this);
        locationService = new LocationServiceImpl(this);
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

    private void setListeners() {
        binding.uploadBtn.setOnClickListener(v -> onUploadClicked());
    }


    private void checkInternetAndProceed() {
        if (isInternetAvailable()) {
            getLocationFromServer();
        } else {
            getLocationLocally();
        }
    }

    private void getLocationFromServer() {
        locationService.getLastLocationOfUser(currentUser.getId(), new ResultListener<LocationObj>() {
            @Override
            public void onResult(LocationObj locationObj) {
                latitude = locationObj.getLatitude();
                longitude = locationObj.getLongitude();
                showToastMessage(getString(R.string.location_received));
            }

            @Override
            public void onError(Throwable throwable) {
                showToastMessage(throwable.getMessage());
            }
        });

    }

    private void getLocationLocally() {
        executorService.execute(
                new GetLastLocationOfUserRunnable(getApplicationContext(), currentUser.getId(), new DbResponse<LocationObj>() {
                    @Override
                    public void onSuccess(LocationObj locationObj) {
                        runOnUiThread(() -> {
                            showToastMessage(getString(R.string.location_received));
                            latitude = locationObj.getLatitude();
                            longitude = locationObj.getLongitude();
                        });
                    }

                    @Override
                    public void onError(Error error) {
                        runOnUiThread(() -> showToastMessage(error.getMessage()));
                    }
                }));
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


    private void onUploadClicked() {
        binding.customerNameEt.setError(null);
        binding.phoneNumberEt.setError(null);
        binding.shopAreaEt.setError(null);

        int shopArea = Integer.parseInt(binding.shopAreaEt.getText().toString());
        String customerName = binding.customerNameEt.getText().toString();
        String phoneNumber = binding.phoneNumberEt.getText().toString();

        if (!customerIsValid(customerName, phoneNumber, shopArea)) {
            return;
        }


        Customer newCustomer = new Customer("draft", customerName, shopArea, phoneNumber, currentUser.getId(), latitude, longitude);

        if (isInternetAvailable()) {
            customerService.insertCustomer(newCustomer, new ResultListener<Customer>() {
                @Override
                public void onResult(Customer customer) {
                    showToastMessage(getString(R.string.add_customer_successfully));
                    insertCustomerToDatabase(newCustomer);
                }

                @Override
                public void onError(Throwable throwable) {
                    showToastMessage(throwable.getMessage());
                }
            });
        } else {
            executorService.execute(new InsertCustomerRunnable(getApplicationContext(), newCustomer, new DbResponse<Customer>() {
                @Override
                public void onSuccess(Customer customer) {
                    runOnUiThread(() -> {
                        showToastMessage(getString(R.string.add_customer_successfully));
                        setResult(customer);
                    });
                }

                @Override
                public void onError(Error error) {
                    runOnUiThread(() -> showToastMessage(error.getMessage()));
                }
            }));
        }

    }

    private void insertCustomerToDatabase(Customer newCustomer) {
        executorService.execute(
                new InsertCustomerRunnable(getApplicationContext(), newCustomer, new DbResponse<Customer>() {
                    @Override
                    public void onSuccess(Customer customer) {
                        runOnUiThread(() -> setResult(customer));
                    }

                    @Override
                    public void onError(Error error) {
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(AddCustomerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                })
        );
    }

//    private void insertCustomerToServer(Customer newCustomer){
//        customerService.insertCustomer(newCustomer, new ResultListener<Customer>() {
//            @Override
//            public void onResult(Customer customer) {
//                setResult(customer);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                showToastMessage(throwable.getMessage());
//            }
//        });
//    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean customerIsValid(String customerName, String phoneNumber, int shopArea) {
        if (TextUtils.isEmpty(customerName)) {
            binding.customerNameEt.setError(getString(R.string.error_name_empty));
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.phoneNumberEt.setError(getString(R.string.error_phoneNumber_empty));
            return false;
        }
        if (!Validator.isValidIranianPhoneNumber(phoneNumber)) {
            binding.phoneNumberEt.setError(getString(R.string.error_phoneNumber_invalid));
            return false;
        }
        if (shopArea == 0) {
            binding.shopAreaEt.setError(getString(R.string.error_shopArea_empty));
            return false;
        }
        if (latitude == 0 || longitude == 0) {
            showToastMessage(getString(R.string.error_location_empty));
            return false;
        }
        return true;
    }

    private void setResult(Customer customer) {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_REPORT, customer);
        setResult(RESULT_OK, intent);
        finish();
    }
}