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
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.bihe.livebin.R;
import org.bihe.livebin.data.AppData;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.db.runnables.Customer.GetAllCustomerNamesRunnable;
import org.bihe.livebin.data.db.runnables.Received.InsertReceivedRunnable;
import org.bihe.livebin.data.model.Received;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.network.impl.ReceivedServiceImpl;
import org.bihe.livebin.databinding.ActivityAddReceivedBinding;
import org.bihe.livebin.utils.Constant;
import org.bihe.livebin.utils.DialogHelper;
import org.bihe.livebin.utils.ResultListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddReceivedActivity extends AppCompatActivity {

    private ActivityAddReceivedBinding binding;

    private ExecutorService executorService;
    private ProgressDialog dialog;
    private ReceivedServiceImpl receivedService;

    private User currentUser;

    private LiveData<List<String>> customersName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReceivedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        loadCustomersName();
        setUpToolbar();
        setListeners();


        binding.checkDetails.setVisibility(View.GONE);
        binding.posDetails.setVisibility(View.GONE);

        binding.radioGroupReceivedType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                binding.checkDetails.setVisibility(View.GONE);
                binding.posDetails.setVisibility(View.GONE);


                if (checkedId == binding.checkRb.getId()) {
                    binding.checkDetails.setVisibility(View.VISIBLE);
                } else if (checkedId == binding.posRb.getId()) {
                    binding.posDetails.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void loadCustomersName() {
        executorService.execute(new GetAllCustomerNamesRunnable(getApplicationContext(), new DbResponse<LiveData<List<String>>>() {
            @Override
            public void onSuccess(LiveData<List<String>> strings) {
                runOnUiThread(() -> {
                    if (strings != null) {
                        customersName = strings;
                        customersName.observe(AddReceivedActivity.this, new Observer<List<String>>() {
                            @Override
                            public void onChanged(List<String> customerNames) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddReceivedActivity.this,
                                        android.R.layout.simple_spinner_item, customerNames);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                binding.customersSpn.setAdapter(adapter);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Error error) {
                runOnUiThread(() -> showToastMessage(error.getMessage()));
            }
        }));
    }


    private void setListeners() {
        binding.submitBtn.setOnClickListener(v -> onSubmitClicked());
    }

    private void init() {
        currentUser = AppData.getInstance().getCurrentUser();
        executorService = Executors.newCachedThreadPool();
        receivedService = new ReceivedServiceImpl(this);
        dialog = DialogHelper.getLoadingDialog(this);
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

    private void onSubmitClicked() {
        binding.amountEt.setError(null);
        binding.checkCodeEt.setError(null);
        binding.trackingCodeEt.setError(null);

        String selectedCustomer = binding.customersSpn.getSelectedItem().toString();
        String amount = binding.amountEt.getText().toString().trim();

        int selectedReceivedTypeId = binding.radioGroupReceivedType.getCheckedRadioButtonId();
        String receivedType = "";
        String bankName = "";
        String checkCode = "";
        String trackingCode = "";

        if (selectedReceivedTypeId == binding.cashRb.getId()) {
            receivedType = getString(R.string.cash);
        } else if (selectedReceivedTypeId == binding.checkRb.getId()) {
            receivedType = getString(R.string.check);
            bankName = binding.bankNamesSpn.getSelectedItem().toString();
            checkCode = binding.checkCodeEt.getText().toString().trim();
        } else if (selectedReceivedTypeId == binding.posRb.getId()) {
            receivedType = getString(R.string.pos);
            trackingCode = binding.trackingCodeEt.getText().toString().trim();
        }

        if (!isReceivedValid(selectedCustomer, amount, checkCode, trackingCode, bankName, selectedReceivedTypeId)) {
            return;
        }


        Received newReceived = new Received(selectedCustomer, receivedType, amount, bankName, checkCode, trackingCode, "draft");

        if (isInternetAvailable()) {
            receivedService.insertReceived(newReceived, new ResultListener<Received>() {
                @Override
                public void onResult(Received received) {
                    showToastMessage(getString(R.string.Received_added));
                    insertReceivedToDatabase(received);
                }

                @Override
                public void onError(Throwable throwable) {
                    showToastMessage(throwable.getMessage());
                }
            });
        }else{
            insertReceivedToDatabase(newReceived);
        }
    }

    private boolean isReceivedValid(String customerName, String amount, String checkCode, String trackingCode, String bankName, int typOfReceived) {
        if (TextUtils.isEmpty(customerName)) {
            showToastMessage(getString(R.string.error_name_empty));
            return false;
        }
        if (amount.isEmpty()) {
            binding.amountEt.setError(getString(R.string.error_amount_empty));
            return false;
        }
        if (typOfReceived == -1) {
            showToastMessage(getString(R.string.error_typeofreceived_empty));
            return false;
        }
        if (typOfReceived == binding.checkRb.getId()) {
            if (TextUtils.isEmpty(checkCode)) {
                binding.checkCodeEt.setError(getString(R.string.error_checkcode_empty));
                return false;
            }
            if (TextUtils.isEmpty(bankName)) {
                showToastMessage(getString(R.string.error_bankname_empty));
                return false;
            }
        }
        if (typOfReceived == binding.posRb.getId()) {
            if (TextUtils.isEmpty(trackingCode)) {
                binding.trackingCodeEt.setError(getString(R.string.error_trackingcode_empty));
                return false;
            }
        }
        return true;
    }

    private void insertReceivedToDatabase(Received received) {
        executorService.execute(
                new InsertReceivedRunnable(getApplicationContext(), received, new DbResponse<Received>() {
                    @Override
                    public void onSuccess(Received received) {
                        runOnUiThread(() -> setResult(received));
                    }

                    @Override
                    public void onError(Error error) {
                        runOnUiThread(() -> showToastMessage(error.getMessage()));
                    }
                })
        );
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

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void setResult(Received received) {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_REPORT, received);
        setResult(RESULT_OK, intent);
        finish();
    }

}
