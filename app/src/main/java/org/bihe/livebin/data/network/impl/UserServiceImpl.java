package org.bihe.livebin.data.network.impl;

import androidx.annotation.NonNull;

import org.bihe.livebin.R;
import org.bihe.livebin.data.model.ServerError;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.network.NetworkHelper;
import org.bihe.livebin.data.network.service.UserService;
import org.bihe.livebin.utils.ResultListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserServiceImpl {


    private final NetworkHelper networkHelper;

    private final UserService userService;


    public UserServiceImpl() {
        networkHelper = new NetworkHelper();
        Retrofit retrofit = networkHelper.buildRetrofit();
        userService = retrofit.create(UserService.class);
    }

    public void signupUser(User user, ResultListener<User> resultListener) {
        if (networkHelper.isNetworkConnected()) {
            networkHelper.showNetworkError(resultListener, R.string.network_connection_error);
            return;
        }
        Call<User> call = userService.signupUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User received = response.body();
                    if (received == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    received.setUsername(user.getUsername());
                    received.setPassword(user.getPassword());
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
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });

    }


    public void loginUser(User user, ResultListener<User> resultListener) {
        if (networkHelper.isNetworkConnected()) {
            networkHelper.showNetworkError(resultListener, R.string.network_connection_error);
            return;
        }
        Call<User> call = userService.loginUser(user.getUsername(), user.getPassword());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User received = response.body();
                    if (received == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    received.setUsername(user.getUsername());
                    received.setPassword(user.getPassword());
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
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }
}
