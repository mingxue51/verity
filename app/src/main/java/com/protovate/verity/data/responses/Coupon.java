package com.protovate.verity.data.responses;

/**
 * Created by Yan on 7/25/15.
 */
public class Coupon {
//    {"success":false,"data":null,"errors":[{"field":"code","message":"Invalid coupon code."}]}

    public boolean success;
    public Info.Error errors[];

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Info.Error[] getErrors() {
        return errors;
    }

    public void setErrors(Info.Error[] errors) {
        this.errors = errors;
    }
}

