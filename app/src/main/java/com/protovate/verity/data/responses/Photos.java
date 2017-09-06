package com.protovate.verity.data.responses;

/**
 * Created by Yan on 7/13/15.
 */
public class Photos {
    public boolean success;
    public Data data;
    public User.Error errors[];

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

    public User.Error[] getErrors() {
        return errors;
    }

    public void setErrors(User.Error[] errors) {
        this.errors = errors;
    }

    public class Data {
        public Item items[];

        public Item[] getItems() {
            return items;
        }

        public void setItems(Item[] items) {
            this.items = items;
        }

        public class Item {
            public int id;
            public String type;
            public Photo.Data.File file;
            public boolean checkbox = false;
            public boolean checked = false;

            public void setFile(Photo.Data.File file) {
                this.file = file;
            }

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

            public Photo.Data.File getFile() {
                return file;
            }

            public void setPhoto(Photo.Data.File photo) {
                this.file = photo;
            }
        }

    }
}
