package org.bihe.livebin.data.network.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.dao.ReceivedDao;
import org.bihe.livebin.data.model.Received;
import org.bihe.livebin.data.model.ServerError;
import org.bihe.livebin.data.network.NetworkHelper;
import org.bihe.livebin.data.network.service.ReceivedService;
import org.bihe.livebin.utils.ResultListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReceivedServiceImpl {

    private final NetworkHelper networkHelper;
    private final ReceivedService receivedService;
    private ExecutorService executorService;
    private ReceivedDao receivedDao;

    public ReceivedServiceImpl(Context context) {
        networkHelper = new NetworkHelper();
        Retrofit retrofit = networkHelper.buildRetrofit();
        executorService = Executors.newCachedThreadPool();
        this.receivedService = retrofit.create(ReceivedService.class);
        receivedDao = DbManager.getInstance(context).receivedDao();
    }


    public void insertReceived(Received received, ResultListener<Received> resultListener) {
        received.setServerId(1);
        Call<Received> call = receivedService.insertReceived(received);
        call.enqueue(new Callback<Received>() {
            @Override
            public void onResponse(@NonNull Call<Received> call, @NonNull Response<Received> response) {
                if (response.isSuccessful()) {
                    Received receivedRsp = response.body();
                    if (receivedRsp == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    executorService.execute(() -> {
                        Received receivedRecord = receivedDao.getReceivedByLocalId(received.getLocalId());
                        if (receivedRecord != null) {
                            receivedRecord.setServerId(1);
                            receivedRecord.setStatus("sent");
                            receivedDao.update(receivedRecord);
                        }
                        new Handler(Looper.getMainLooper()).post(() -> resultListener.onResult(received));
                    });
                    return;
                }
                ServerError serverError = networkHelper.convertResponseToError(response, ServerError.class);
                if (serverError == null) {
                    networkHelper.showNetworkError(resultListener, R.string.network_json_error);
                    return;
                }
                resultListener.onError(new Error(serverError.getError()));
            }

            @Override
            public void onFailure(@NonNull Call<Received> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }

}
