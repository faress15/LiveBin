package org.bihe.livebin.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.bihe.livebin.data.model.LocationObj;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    long insert(LocationObj locationObj);

    @Query("SELECT * FROM location")
    List<LocationObj> getAllLocations();

    @Query("SELECT * FROM Location WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    LocationObj getLastLocationOfUser(String userId);

    @Update
    void update(LocationObj location);

    @Query("SELECT * FROM location WHERE localId = :localId")
    LocationObj getLocationByLocalId(int localId);

    @Query("SELECT * FROM location WHERE status = :status")
    List<LocationObj> getLocationsByStatus(String status);


}


