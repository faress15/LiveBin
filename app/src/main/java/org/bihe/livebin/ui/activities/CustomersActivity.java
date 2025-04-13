package org.bihe.livebin.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.db.runnables.Customer.GetAllCustomersRunnable;
import org.bihe.livebin.data.model.Customer;
import org.bihe.livebin.data.network.impl.CustomerServiceImpl;
import org.bihe.livebin.databinding.ActivityCustomersBinding;
import org.bihe.livebin.ui.adapters.CustomerAdapter;
import org.bihe.livebin.utils.Constant;
import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomersActivity extends AppCompatActivity {

    private ActivityCustomersBinding binding;

    private CustomerAdapter adapter;

    private CustomerServiceImpl customerService;

    private List<Customer> customers = new ArrayList<>();

    private ExecutorService executorService;


    private final ActivityResultLauncher<Intent> newCustomerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData()!=null) {
                        Customer customer = (Customer) result.getData().getSerializableExtra(Constant.KEY_REPORT);
                        customers.add(0, customer);
                        adapter.itemInsertedOnTop();
                        binding.customersRv.smoothScrollToPosition(0);
                    } else {
                        Log.i("new", "failed to find new report");
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        binding = ActivityCustomersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setUpToolbar();
        setListener();
        setUpRecyclerView();
    }

    private void setListener() {
        binding.addCustomerBtn.setOnClickListener(v -> checkLocationPermission());
    }

    private void init(){
        customerService = new CustomerServiceImpl(this);
        executorService = Executors.newCachedThreadPool();
    }

    private void goToAddCustomerScreen() {
        Intent intent = new Intent(this, AddCustomerActivity.class);
        newCustomerActivityResultLauncher.launch(intent);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && isLocationEnabled()) {
            goToAddCustomerScreen();
        } else {
            Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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


    private void setUpRecyclerView() {
        executorService.execute(new GetAllCustomersRunnable(getApplicationContext(), new DbResponse<List<Customer>>() {
            @Override
            public void onSuccess(List<Customer> customerss) {
                runOnUiThread(() -> {
                    customers = customerss;
                    if (adapter != null) {
                        adapter.updateReports(customers);
                    }
                });
            }

            @Override
            public void onError(Error error) {
                runOnUiThread(() -> showToastMessage(error.getMessage()));
            }
        }));
        binding.customersRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(this, customers);
        binding.customersRv.setAdapter(adapter);
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
