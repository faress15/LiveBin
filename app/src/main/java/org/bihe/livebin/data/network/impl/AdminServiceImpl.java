package org.bihe.livebin.data.network.impl;

import androidx.annotation.NonNull;

import org.bihe.livebin.R;
import org.bihe.livebin.data.model.Admin;
import org.bihe.livebin.data.model.ServerError;
import org.bihe.livebin.data.network.NetworkHelper;
import org.bihe.livebin.data.network.service.AdminService;
import org.bihe.livebin.utils.ResultListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdminServiceImpl {

    private final NetworkHelper networkHelper;

    private final AdminService adminService;


    public AdminServiceImpl() {
        networkHelper = new NetworkHelper();
        Retrofit retrofit = networkHelper.buildRetrofit();
        adminService = retrofit.create(AdminService.class);
    }

    public void loginAdmin(ResultListener<Admin> resultListener) {
        if (networkHelper.isNetworkConnected()) {
            networkHelper.showNetworkError(resultListener, R.string.network_connection_error);
            return;
        }
        Call<Admin> call = adminService.loginAdmin();
        call.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(@NonNull Call<Admin> call, @NonNull Response<Admin> response) {
                if (response.isSuccessful()) {
                    Admin received = response.body();
                    if (received == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    resultListener.onResult(received);
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
            public void onFailure(@NonNull Call<Admin> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }
}
