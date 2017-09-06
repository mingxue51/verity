package com.protovate.verity.data.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan on 7/14/15.
 */
public class Videos {
    public boolean success;
    public Data data;

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
            public String file;
            @SerializedName("file_2") public Photo file2;

            public Photo getFile2() {
                return file2;
            }

            public void setFile2(Photo file2) {
                this.file2 = file2;
            }

            public class Photo {
                public String orig;
                public String thumb;

                public String getOrig() {
                    return orig;
                }

                public void setOrig(String orig) {
                    this.orig = orig;
                }

                public String getThumb() {
                    return thumb;
                }

                public void setThumb(String thumb) {
                    this.thumb = thumb;
                }
            }

            public boolean checkbox = false;
            public boolean checked = false;

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

            public String getFile() {
                return file;
            }

            public void setFile(String file) {
                this.file = file;
            }
        }
    }

}
