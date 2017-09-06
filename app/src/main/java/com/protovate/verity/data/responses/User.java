package com.protovate.verity.data.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Yan on 5/20/15.
 */
public class User implements Serializable {
    public boolean success;
    public Data data;
    public Error[] errors;

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

    public class Data implements Serializable {
        @SerializedName("first_name") public String firstName;
        @SerializedName("last_name") public String lastName;
        @SerializedName("access_token") public String accessToken;
        @SerializedName("lock_count_today") public int lockCount;
        @SerializedName("pending_invitation_count") public int invitationCount;

        public int getInvitationCount() {
            return invitationCount;
        }

        public void setInvitationCount(int invitationCount) {
            this.invitationCount = invitationCount;
        }

        public int id;
        public String email;
        public int credits;
        public Photo photo;


        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getCredits() {
            return credits;
        }

        public void setCredits(int credits) {
            this.credits = credits;
        }

        public int getLockCount() {
            return lockCount;
        }

        ;

        public void setLockCount(int lockCount) {
            this.lockCount = lockCount;
        }

        ;


        public Photo getPhoto() {
            return photo;
        }

        public void setPhoto(Photo photo) {
            this.photo = photo;
        }

        public class Photo implements Serializable {
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

    public class Error implements Serializable {
        public String field;
        public String message;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
