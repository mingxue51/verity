package com.protovate.verity.data.responses;

/**
 * Created by Yan on 8/6/15.
 */
public class StepZero {

    public boolean success;
    public Error errors[];
    public boolean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Error[] getErrors() {
        return errors;
    }

    public void setErrors(Error[] errors) {
        this.errors = errors;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }
}
