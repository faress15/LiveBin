package org.bihe.livebin.data.network.service;

import org.bihe.livebin.data.model.LocationObj;
import org.bihe.livebin.data.model.response.LocationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LocationService {


    @POST("/classes/Location")
    @Headers("X-Parse-Revocable-Session: 1")
    Call<LocationObj> registerLocation(@Body LocationObj locationObj);

    @GET("/classes/Location")
    Call<LocationResponse> getLastLocationOfUser(
            @Query("where") String userId,
            @Query("limit") int limit,
            @Query("order") String order
    );

    @GET("/classes/Location")
    Call<LocationResponse> getUserLocations(
            @Query("where") String userId,
            @Query("order") String order
    );

    @GET("/classes/Location")
    Call<LocationResponse> getAllLocations();

}
