package com.protovate.verity.data;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Yan on 6/22/15.
 */
public class Vlock implements Serializable {
    private File photo;
    private String voiceAnswer;
    private File voiceFile;

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public void setVoiceAnswer(String voiceAnswer) {
        this.voiceAnswer = voiceAnswer;
    }

    public String getVoiceAnswer() {
        return voiceAnswer;
    }

    public void setVoiceFile(File voiceFile) {
        this.voiceFile = voiceFile;
    }

    public File getVoiceFile() {
        return voiceFile;
    }
}
