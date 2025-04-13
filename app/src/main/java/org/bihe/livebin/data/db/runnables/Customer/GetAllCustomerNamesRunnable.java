package org.bihe.livebin.data.db.runnables.Customer;

import android.content.Context;

import androidx.lifecycle.LiveData;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.DbResponse;

import java.util.List;

public class GetAllCustomerNamesRunnable implements Runnable {

    private final Context context;
    private final DbResponse<LiveData<List<String>>> dbResponse;

    public GetAllCustomerNamesRunnable(Context context, DbResponse<LiveData<List<String>>> dbResponse) {
        this.context = context;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        LiveData<List<String>> customerNames = dbManager.customerDao().getAllCustomerNames();
        if (customerNames == null) {
            dbResponse.onError(new Error(String.valueOf(R.string.error_get_all_customers_name_failed)));
        } else {
            dbResponse.onSuccess(customerNames);
        }
    }
}
