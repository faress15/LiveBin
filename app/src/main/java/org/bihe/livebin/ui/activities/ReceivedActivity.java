package org.bihe.livebin.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import org.bihe.livebin.data.db.runnables.Received.GetAllReceivedRunnable;
import org.bihe.livebin.data.model.Received;
import org.bihe.livebin.databinding.ActivityReceivedBinding;
import org.bihe.livebin.ui.adapters.ReceivedAdapter;
import org.bihe.livebin.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReceivedActivity extends AppCompatActivity {


    private ActivityReceivedBinding binding;

    private List<Received> receipts = new ArrayList<>();

    private ReceivedAdapter adapter;

    private ExecutorService executorService;



    private final ActivityResultLauncher<Intent> newReceivedActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData()!=null) {
                        Received received = (Received) result.getData().getSerializableExtra(Constant.KEY_REPORT);
                        if (received != null) {
                            receipts.add(0, received);
                            adapter.itemInsertedOnTop();
                            binding.receivedRv.smoothScrollToPosition(0);
                        }
                    } else {
                        Log.i("new", "failed to find new report");
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceivedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        init();
        setUpToolbar();
        setUpRecyclerView();
        loadData();
    }

    private void init(){
        executorService = Executors.newCachedThreadPool();
        binding.addReceivedBtn.setOnClickListener(v -> goToAddReceivedScreen());
    }

    private void goToAddReceivedScreen() {
        Intent intent = new Intent(this, AddReceivedActivity.class);
        newReceivedActivityResultLauncher.launch(intent);
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
        adapter = new ReceivedAdapter(this, receipts);
        binding.receivedRv.setLayoutManager(new LinearLayoutManager(this));
        binding.receivedRv.setAdapter(adapter);
    }

    private void loadData() {
        executorService.execute(new GetAllReceivedRunnable(getApplicationContext(), new DbResponse<List<Received>>() {
            @Override
            public void onSuccess(List<Received> receiptss) {
                runOnUiThread(() -> {
                    receipts.clear();
                    receipts.addAll(receiptss);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Error error) {
                runOnUiThread(() -> showToastMessage("Error: " + error.getMessage()));
            }
        }));
    }

        private void showToastMessage(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
