package org.bihe.livebin.data.network.service;

import org.bihe.livebin.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {

        @POST("/users")
        @Headers("X-Parse-Revocable-Session: 1")
        Call<User> signupUser(@Body User user);

        @GET("/login")
        @Headers("X-Parse-Revocable-Session: 1")
        Call<User> loginUser(@Query("username") String username, @Query("password") String password);
}
