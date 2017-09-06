package com.protovate.verity.data.responses;

/**
 * Created by Yan on 7/14/15.
 */
public class Success {
    public boolean success;
    public Error errors[];

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
}
