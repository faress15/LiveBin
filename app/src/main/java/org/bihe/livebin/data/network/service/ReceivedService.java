package org.bihe.livebin.data.network.service;

import org.bihe.livebin.data.model.Received;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ReceivedService {

    @POST("/classes/Received")
    @Headers("X-Parse-Revocable-Session: 1")
    Call<Received> insertReceived(@Body Received received);

}
