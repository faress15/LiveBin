package org.bihe.livebin.data.db.runnables.Customer;

import android.content.Context;

import org.bihe.livebin.R;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.DbResponse;
import org.bihe.livebin.data.model.Customer;

public class InsertCustomerRunnable implements Runnable {

    private final Context context;
    private final Customer customer;
    private final DbResponse<Customer> dbResponse;

    public InsertCustomerRunnable(Context context, Customer customer, DbResponse<Customer> dbResponse) {
        this.context = context;
        this.customer = customer;
        this.dbResponse = dbResponse;
    }

    @Override
    public void run() {
        DbManager dbManager = DbManager.getInstance(context);
        long id = dbManager.customerDao().insert(customer);

        if (id > 0) {
            dbResponse.onSuccess(customer);
        } else {
            dbResponse.onError(new Error(String.valueOf(R.string.customer_error_insert_failed)));
        }
    }
}
