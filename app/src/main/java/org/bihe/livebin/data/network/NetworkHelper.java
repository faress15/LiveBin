package org.bihe.livebin.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.bihe.livebin.data.AppData;
import org.bihe.livebin.utils.ResultListener;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkHelper {

    public Retrofit buildRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new CustomInterceptor())
                .addInterceptor(httpLoggingInterceptor).build();
        return new Retrofit.Builder().client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://parseapi.back4app.com")
                .build();
    }


    public <T, U> U convertResponseToError(retrofit2.Response<T> response, Class<U> className) {
        try (ResponseBody responseBody = response.errorBody()) {
            if (responseBody == null) {
                return null;
            }
            String responseStr = responseBody.string();
            return new Gson().fromJson(responseStr, className);
        } catch (IOException e) {
            return null;
        }
    }

    public <T> void showNetworkError(@NonNull ResultListener<T> resultListener, int resourceId) {
        Context context = AppData.getInstance().getApplicationContext();
        String error = context.getString(resourceId);
        resultListener.onError(new Error(error));
    }

    public boolean isNetworkConnected() {
        Context context = AppData.getInstance().getApplicationContext();
        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return true;
        }
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        return networkCapabilities == null || !networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}
