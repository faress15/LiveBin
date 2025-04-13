package org.bihe.livebin.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Admin")
public class Admin {

    @ColumnInfo
    private String username;

    @ColumnInfo
    private String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
