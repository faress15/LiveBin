package org.bihe.livebin.data.network.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.dao.CustomerDao;
import org.bihe.livebin.data.model.Customer;
import org.bihe.livebin.data.model.response.CustomerResponse;
import org.bihe.livebin.data.model.ServerError;
import org.bihe.livebin.data.network.NetworkHelper;
import org.bihe.livebin.data.network.service.CustomerService;
import org.bihe.livebin.utils.ResultListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CustomerServiceImpl {

    private final NetworkHelper networkHelper;
    private final CustomerService customerService;
    private ExecutorService executorService;
    private CustomerDao customerDao;

    public CustomerServiceImpl(Context context) {
        networkHelper = new NetworkHelper();
        Retrofit retrofit = networkHelper.buildRetrofit();
        this.customerService = retrofit.create(CustomerService.class);
        executorService = Executors.newCachedThreadPool();
        customerDao = DbManager.getInstance(context).customerDao();
    }


    public void insertCustomer(Customer customer, ResultListener<Customer> resultListener) {
        customer.setServerId(1);
        Call<Customer> call = customerService.insertCustomer(customer);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                if (response.isSuccessful()) {
                    Customer received = response.body();
                    if (received == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }
                    executorService.execute(() -> {
                        Customer customerRecord = customerDao.getCustomerByLocalId(customer.getLocalId());
                        if (customerRecord != null) {
                            customerRecord.setServerId(1);
                            customerRecord.setStatus("sent");
                            customerDao.update(customerRecord);
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
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }

    public void getAllCustomers(ResultListener<List<Customer>> resultListener) {
        if (networkHelper.isNetworkConnected()) {
            networkHelper.showNetworkError(resultListener, R.string.network_connection_error);
            return;
        }
        Call<CustomerResponse> call = customerService.getAllCustomers();
        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(@NonNull Call<CustomerResponse> call, @NonNull Response<CustomerResponse> response) {
                if (response.isSuccessful()) {
                    List<Customer> customers = response.body().getResults();
                    if (customers == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }

                    resultListener.onResult(customers);
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
            public void onFailure(@NonNull Call<CustomerResponse> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }

    public void getAllUserCustomers(String userId, ResultListener<List<Customer>> resultListener) {
        if (networkHelper.isNetworkConnected()) {
            networkHelper.showNetworkError(resultListener, R.string.network_connection_error);
            return;
        }
        String whereJson = "{\"publisherId\":\"" + userId + "\"}";
        Call<CustomerResponse> call = customerService.getAllUserCustomers(whereJson);
        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(@NonNull Call<CustomerResponse> call, @NonNull Response<CustomerResponse> response) {
                if (response.isSuccessful()) {
                    List<Customer> customers = response.body().getResults();
                    if (customers == null) {
                        networkHelper.showNetworkError(resultListener, R.string.network_general_error);
                        return;
                    }

                    resultListener.onResult(customers);
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
            public void onFailure(@NonNull Call<CustomerResponse> call, @NonNull Throwable throwable) {
                networkHelper.showNetworkError(resultListener, R.string.network_general_error);
            }
        });
    }

}
