package com.protovate.verity.data.responses;

/**
 * Created by Yan on 7/13/15.
 */
public class Photo {
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
        public int id;
        public String type;
        public File file;

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

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public class File {
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
    }
}
