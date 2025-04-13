package org.bihe.livebin.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.bihe.livebin.data.model.Received;

import java.util.List;

@Dao
public interface ReceivedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Received received);

    @Query("SELECT * FROM receipts")
    List<Received> getAllReceipts();

    @Query("SELECT * FROM receipts WHERE status = :status")
    List<Received> getReceiptsByStatus(String status);

    @Update
    void update(Received received);

    @Query("SELECT * FROM receipts WHERE localId = :localId")
    Received getReceivedByLocalId(int localId);

}
