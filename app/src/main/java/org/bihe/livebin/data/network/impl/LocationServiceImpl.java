package org.bihe.livebin.data.network.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.dao.LocationDao;
import org.bihe.livebin.data.model.LocationObj;
import org.bihe.livebin.data.model.response.LocationResponse;
import org.bihe.livebin.data.model.ServerError;
import org.bihe.livebin.data.network.NetworkHelper;
import org.bihe.livebin.data.network.service.LocationService;
import org.bihe.livebin.utils.ResultListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LocationServiceImpl {

    private final NetworkHelper networkHelper;
    private final LocationService locationService;

    private ExecutorService executorService;
    
    private LocationDao locationDao;


    public LocationServiceImpl(Context context) {
        networkHelper = new NetworkHelper();
        Retrofit retrofit = networkHelper.buildRetrofit();
        locationService = retrofit.create(LocationService.class);
        executorService = Executors.newCachedThreadPool();
        locationDao = DbManager.getInstance(context).locationDao();
    }


    public void registerLocation(LocationObj locationObj, ResultListener<LocationObj> resultListener) {
        locationObj.setServerId(1);
        Call<LocationObj> call = locationService.registerLocation(locationObj);

        call.enqueue(new Callback<LocationObj>() {
            @Override
            public void onResponse(@NonNull Call<LocationObj> call, @NonNull Response<LocationObj> response) {
                if (response.isSuccessful()) {
                    LocationObj received = response.body();
                    if (received == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
//                    chatgpt
                    executorService.execute(() -> {
                        LocationObj localRecord = locationDao.getLocationByLocalId(locationObj.getLocalId());
                        if (localRecord != null) {
                            localRecord.setServerId(1);
                            localRecord.setStatus("sent");
                            locationDao.update(localRecord);
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
            public void onFailure(@NonNull Call<LocationObj> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }


    public void getLastLocationOfUser(String userId, ResultListener<LocationObj> resultListener) {
        if (networkHelper.isNetworkConnected()) {
            networkHelper.showNetworkError(resultListener, R.string.network_connection_error);
            return;
        }
        String whereJson = "{\"userId\":\"" + userId + "\"}";
        Call<LocationResponse> call = locationService.getLastLocationOfUser(whereJson,1,"desc");
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    List<LocationObj> locations = response.body().getResults();
                    if (locations == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    if (!locations.isEmpty()) {
                        LocationObj location = locations.get(0);
                        resultListener.onResult(location);
                        return;
                    }
                }
                ServerError serverError = networkHelper.convertResponseToError(response, ServerError.class);
                if (serverError == null) {
                    networkHelper.showNetworkError(resultListener, R.string.network_json_error);
                    return;
                }
                resultListener.onError(new Error(serverError.getError()));
            }

            @Override
            public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }


    public void getLocationsOfUser(String userId, ResultListener<List<LocationObj>> resultListener) {
        if (networkHelper.isNetworkConnected()) {
            networkHelper.showNetworkError(resultListener, R.string.network_connection_error);
            return;
        }
        String whereJson = "{\"userId\":\"" + userId + "\"}";
        Call<LocationResponse> call = locationService.getUserLocations(whereJson,"desc");
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    List<LocationObj> locations = response.body().getResults();
                    if (locations == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                        resultListener.onResult(locations);
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
            public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }

    public void getAllLocations(ResultListener<List<LocationObj>> resultListener) {
        if (networkHelper.isNetworkConnected()) {
            networkHelper.showNetworkError(resultListener, R.string.network_connection_error);
            return;
        }
        Call<LocationResponse> call = locationService.getAllLocations();
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    List<LocationObj> locations = response.body().getResults();
                    if (locations == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    resultListener.onResult(locations);
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
            public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }
}
