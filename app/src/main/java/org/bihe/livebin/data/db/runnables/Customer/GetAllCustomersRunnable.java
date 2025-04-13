package org.bihe.livebin.data.db.runnables.Customer;

import android.content.Context;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.model.Customer;


import java.util.List;

public class GetAllCustomersRunnable implements Runnable {

    private final Context context;
    private final DbResponse<List<Customer>> dbResponse;

    public GetAllCustomersRunnable(Context context, DbResponse<List<Customer>> dbResponse) {
        this.context = context;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        List<Customer> customers = dbManager.customerDao().getAllCustomers();
        if (customers == null) {
            dbResponse.onError(new Error(String.valueOf(R.string.error_get_all_customers_failed)));
        } else {
            dbResponse.onSuccess(customers);
        }
    }
}
