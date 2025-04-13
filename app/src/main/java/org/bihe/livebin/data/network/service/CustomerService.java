package org.bihe.livebin.data.network.service;

import org.bihe.livebin.data.model.Customer;
import org.bihe.livebin.data.model.response.CustomerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CustomerService {

    @POST("/classes/Customer")
    @Headers("X-Parse-Revocable-Session: 1")
    Call<Customer> insertCustomer(@Body Customer customer);


    @GET("/classes/Customer")
    @Headers("X-Parse-Revocable-Session: 1")
    Call<CustomerResponse> getAllCustomers();

    @GET("/classes/Customer")
    @Headers("X-Parse-Revocable-Session: 1")
    Call<CustomerResponse> getAllUserCustomers(@Query("where") String publisherId);

}

