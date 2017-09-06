package com.protovate.verity.data.responses;

/**
 * Created by Yan on 6/23/15.
 */
public class Credits {
    public boolean success;
    public User.Error errors[];
    public Data data[];

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public User.Error[] getErrors() {
        return errors;
    }

    public void setErrors(User.Error[] errors) {
        this.errors = errors;
    }

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    public class Data {
        public int id;
        public String name;
        public int credit;
        public int amount;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCredit() {
            return credit;
        }

        public void setCredit(int credit) {
            this.credit = credit;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
