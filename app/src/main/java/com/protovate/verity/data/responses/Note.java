package com.protovate.verity.data.responses;

/**
 * Created by Yan on 6/25/15.
 */
public class Note {
    public boolean success;
    public User.Error errors[];
    public Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        public int id;
        public String notes;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
