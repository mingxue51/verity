package com.protovate.verity.data.events;

/**
 * Created by Yan on 19.8.2015..
 */
public class FileConverted {
    private String filename;

    public FileConverted(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
