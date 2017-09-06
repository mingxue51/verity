package com.protovate.verity.data.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Yan on 6/22/15.
 */
public class LockResponse implements Serializable {

    private boolean success;
    public Error errors[];
    public Data data;

    public Error[] getErrors() {
        return errors;
    }

    public void setErrors(Error[] errors) {
        this.errors = errors;
    }

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

    public class Data implements Serializable {
        @SerializedName("case_number") public String caseNumber;
        @SerializedName("file_voice") public String fileVoice;
        @SerializedName("file_picture") public User.Data.Photo filePicture;
        @SerializedName("address_line_1") public String addressLine1;
        @SerializedName("address_line_2") public String addressLine2;
        @SerializedName("unlock_at") public String unlockAt;
        @SerializedName("audio_count") public int audioCount;
        @SerializedName("video_count") public int videoCount;
        @SerializedName("photo_count") public int photoCount;
        @SerializedName("note_count") public int noteCount;
        public int id;
        public String city;
        public String state;
        public String country;
        public String zip;
        public String lat;
        public String lng;
        public String notes;
        public String status;

        public int getAudioCount() {
            return audioCount;
        }

        public void setAudioCount(int audioCount) {
            this.audioCount = audioCount;
        }

        public int getVideoCount() {
            return videoCount;
        }

        public void setVideoCount(int videoCount) {
            this.videoCount = videoCount;
        }

        public int getPhotoCount() {
            return photoCount;
        }

        public void setPhotoCount(int photoCount) {
            this.photoCount = photoCount;
        }

        public int getNoteCount() {
            return noteCount;
        }

        public void setNoteCount(int noteCount) {
            this.noteCount = noteCount;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCaseNumber() {
            return caseNumber;
        }

        public void setCaseNumber(String caseNumber) {
            this.caseNumber = caseNumber;
        }

        public String getFileVoice() {
            return fileVoice;
        }

        public void setFileVoice(String fileVoice) {
            this.fileVoice = fileVoice;
        }

        public User.Data.Photo getFilePicture() {
            return filePicture;
        }

        public void setFilePicture(User.Data.Photo filePicture) {
            this.filePicture = filePicture;
        }

        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public String getAddressLine2() {
            return addressLine2;
        }

        public void setAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUnlockAt() {
            return unlockAt;
        }

        public void setUnlockAt(String unlockAt) {
            this.unlockAt = unlockAt;
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
