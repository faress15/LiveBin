package org.bihe.livebin.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    @SerializedName("objectId")
    private String id;

    @ColumnInfo
    private String username;

    @ColumnInfo
    private String password;
    @ColumnInfo(name = "session_token")
    private String sessionToken;

    @ColumnInfo
    private String identificationCode;

    @ColumnInfo
    private String performance;

    @ColumnInfo
    private int dayOfBirth;

    @ColumnInfo
    private int monthOfBirth;

    @ColumnInfo
    private int yearOfBirth;


    public User(@NonNull String id, String username, String password, String identificationCode, String performance, int dayOfBirth, int monthOfBirth, int yearOfBirth) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.identificationCode = identificationCode;
        this.performance = performance;
        this.dayOfBirth = dayOfBirth;
        this.monthOfBirth = monthOfBirth;
        this.yearOfBirth = yearOfBirth;
    }


    @Ignore
    public User(String username, String password, String identificationCode, String performance, int dayOfBirth, int monthOfBirth, int yearOfBirth) {
        this.username = username;
        this.password = password;
        this.identificationCode = identificationCode;
        this.performance = performance;
        this.dayOfBirth = dayOfBirth;
        this.monthOfBirth = monthOfBirth;
        this.yearOfBirth = yearOfBirth;
    }


    @Ignore
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Ignore
    public User(String username, String password, String performance) {
        this.username = username;
        this.password = password;
        this.performance = performance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }


    public int getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(int dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public int getMonthOfBirth() {
        return monthOfBirth;
    }

    public void setMonthOfBirth(int monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }
}
