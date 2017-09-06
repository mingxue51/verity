package com.protovate.verity.data.responses;

/**
 * Created by Yan on 7/19/15.
 */
public class Count {
    public boolean success;
    public int data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
