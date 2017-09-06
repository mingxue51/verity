package com.protovate.verity.data.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan on 7/1/15.
 */
public class Notes {
    public boolean success;
    public Data data;
    public Error errors[];

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Error[] getErrors() {
        return errors;
    }

    public void setErrors(Error[] errors) {
        this.errors = errors;
    }

    public class Data {
        public Items items[];

        public Items[] getItems() {
            return items;
        }

        public void setItems(Items[] items) {
            this.items = items;
        }
    }

    public class Items {
        public int id;
        public String type;
        public String notes;
        @SerializedName("created_at") String createdAt;

        public boolean checkbox;
        public boolean checked;

        public boolean isCheckbox() {
            return checkbox;
        }

        public void setCheckbox(boolean checkbox) {
            this.checkbox = checkbox;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
