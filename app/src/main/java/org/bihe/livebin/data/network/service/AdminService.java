package org.bihe.livebin.data.network.service;

import org.bihe.livebin.data.model.Admin;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AdminService {

    @GET("/classes/Admin")
    Call<Admin> loginAdmin();
}
