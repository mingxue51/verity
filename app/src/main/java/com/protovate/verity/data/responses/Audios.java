package com.protovate.verity.data.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan on 7/15/15.
 */
public class Audios {
    public boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data data;

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
            @SerializedName("transcript_audio") public String transcriptAudio;
            @SerializedName("created_at") public String createdAt;
            public int id;
            public String file;
            public String type;
            public boolean checkbox;
            public String duration = "00:00";
            public boolean checked = false;

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public boolean isChecked() {
                return checked;
            }

            public void setChecked(boolean checked) {
                this.checked = checked;
            }

            public boolean isCheckbox() {
                return checkbox;
            }

            public void setCheckbox(boolean checkbox) {
                this.checkbox = checkbox;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getTranscriptAudio() {
                return transcriptAudio;
            }

            public void setTranscriptAudio(String transcriptAudio) {
                this.transcriptAudio = transcriptAudio;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getFile() {
                return file;
            }

            public void setFile(String file) {
                this.file = file;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
