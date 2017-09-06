package com.protovate.verity.data;

import android.graphics.Bitmap;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by Yan on 7/8/15.
 */
@Parcel
public class VideoItem {
    public Bitmap bitmap;
    public String path;
    public String thumb = "";

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @ParcelConstructor
    public VideoItem(Bitmap bitmap, String path) {
        this.bitmap = bitmap;
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
