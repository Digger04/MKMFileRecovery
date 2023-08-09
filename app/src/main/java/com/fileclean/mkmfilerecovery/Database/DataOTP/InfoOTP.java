package com.fileclean.mkmfilerecovery.Database.DataOTP;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "otp")
public class InfoOTP {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int otp;

    public InfoOTP(int otp) {
        this.otp = otp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }
}