package org.bihe.livebin.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "location")
public class LocationObj {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int localId;

    @ColumnInfo
    private int serverId;

    @ColumnInfo
    private String userId;

    @ColumnInfo
    private double latitude;

    @ColumnInfo
    private double longitude;

    @ColumnInfo
    private long timestamp;

    @ColumnInfo
    private String status;

    public LocationObj(@NonNull int localId, int serverId, double latitude, double longitude, long timestamp, String userId, String status) {
        this.localId = localId;
        this.serverId = serverId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.userId = userId;
        this.status = status;
    }

    @Ignore
    public LocationObj(String userId, double latitude, double longitude, long timestamp, String status) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.status = status;
    }

    @Ignore
    public LocationObj(String userId, double latitude, double longitude) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = "draft";
    }


    public int getServerId() {
        return serverId;
    }
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
    public int getLocalId() {
        return localId;
    }
    public void setLocalId(int localId) {
        this.localId = localId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}





