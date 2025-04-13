package org.bihe.livebin.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.bihe.livebin.data.model.Customer;

import java.util.List;

@Dao
public interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Customer customer);

    @Query("SELECT * FROM customers")
    List<Customer> getAllCustomers();

    @Query("SELECT customerName FROM customers")
    LiveData<List<String>> getAllCustomerNames();

    @Query("SELECT * FROM customers WHERE status = :status")
    List<Customer> getCustomersByStatus(String status);

    @Update
    void update(Customer customer);

    @Query("SELECT * FROM customers WHERE localId = :localId")
    Customer getCustomerByLocalId(int localId);

    @Query("SELECT * FROM customers WHERE publisherId = :userId")
    List<Customer> getCustomersByUserId(String userId);

}
