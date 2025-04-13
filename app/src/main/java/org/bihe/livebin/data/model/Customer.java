package org.bihe.livebin.data.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


import java.io.Serializable;


@Entity(tableName = "customers")
public class Customer implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int localId;

    @ColumnInfo
    private int serverId;

    @ColumnInfo
    private String status;

    @ColumnInfo
    private String customerName;

    @ColumnInfo
    private int shopArea;

    @ColumnInfo
    private String phoneNumber;

    @ColumnInfo
    private String publisherId;

    @ColumnInfo
    private double latitude;
    @ColumnInfo
    private double longitude;



    public Customer(@NonNull int localId, int serverId, String status, String customerName, int shopArea, String phoneNumber, String publisherId, double latitude, double longitude) {
        this.localId = localId;
        this.serverId = serverId;
        this.status = status;
        this.customerName = customerName;
        this.shopArea = shopArea;
        this.phoneNumber = phoneNumber;
        this.publisherId = publisherId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Ignore
    public Customer(String status, String customerName, int shopArea, String phoneNumber, String publisherId, double latitude, double longitude) {
        this.status = status;
        this.customerName = customerName;
        this.shopArea = shopArea;
        this.phoneNumber = phoneNumber;
        this.publisherId = publisherId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getShopArea() {
        return shopArea;
    }

    public void setShopArea(int shopArea) {
        this.shopArea = shopArea;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
