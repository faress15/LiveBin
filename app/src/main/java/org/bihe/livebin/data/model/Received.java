package org.bihe.livebin.data.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "receipts")
public class Received implements Serializable {


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
    private String typeReceived;

    @ColumnInfo
    private String amountReceived;

    @ColumnInfo
    private String bank;

    @ColumnInfo
    private String checkCode;

    @ColumnInfo
    private String trackingCode;



    public Received(@NonNull int localId, int serverId, String status, String customerName, String typeReceived, String amountReceived, String bank, String checkCode, String trackingCode) {
        this.localId = localId;
        this.serverId = serverId;
        this.status = status;
        this.amountReceived = amountReceived;
        this.typeReceived = typeReceived;
        this.customerName = customerName;
        this.bank = bank;
        this.checkCode = checkCode;
        this.trackingCode = trackingCode;
    }

    @Ignore
    public Received(String customerName, String typeReceived, String amountReceived, String bank, String checkCode, String trackingCode, String status) {
        this.amountReceived = amountReceived;
        this.typeReceived = typeReceived;
        this.customerName = customerName;
        this.bank = bank;
        this.checkCode = checkCode;
        this.trackingCode = trackingCode;
        this.status = status;
    }

    @Ignore
    public Received(String customerName, String typeReceived, String amountReceived) {
        this.amountReceived = amountReceived;
        this.typeReceived = typeReceived;
        this.customerName = customerName;
    }

    @Ignore
    public Received(String customerName, String typeReceived, String amountReceived, String bank, String checkCode) {
        this.amountReceived = amountReceived;
        this.typeReceived = typeReceived;
        this.customerName = customerName;
        this.bank = bank;
        this.checkCode = checkCode;
    }

    @Ignore
    public Received(String customerName, String typeReceived, String amountReceived, String trackingCode) {
        this.amountReceived = amountReceived;
        this.typeReceived = typeReceived;
        this.customerName = customerName;
        this.trackingCode = trackingCode;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(String amountReceived) {
        this.amountReceived = amountReceived;
    }

    public String getTypeReceived() {
        return typeReceived;
    }

    public void setTypeReceived(String typeReceived) {
        this.typeReceived = typeReceived;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
}
