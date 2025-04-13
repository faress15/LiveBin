package org.bihe.livebin.data.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request newRequest = original.newBuilder()
                .header("X-Parse-Application-Id", " 0Ybov9ZeG5hQoe5DgD8c2SYsV8GIdGsdHIxiGO5J")
                .header("X-Parse-REST-API-Key", "V3obq1SbLnEyUhukxfpsAhWABzqedcQRsiC61TAn").build();
        return chain.proceed(newRequest);
    }
}
