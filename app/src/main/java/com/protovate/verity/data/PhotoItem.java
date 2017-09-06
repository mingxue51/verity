package com.protovate.verity.data;

import android.net.Uri;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by Yan on 6/25/15.
 */
@Parcel
public class PhotoItem {

    // Ivars.
    public Uri thumbnailUri;
    public Uri fullImageUri;
    public boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @ParcelConstructor
    public PhotoItem(Uri thumbnailUri, Uri fullImageUri) {
        this.thumbnailUri = thumbnailUri;
        this.fullImageUri = fullImageUri;
    }

    /**
     * Getters and setters
     */
    public Uri getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(Uri thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public Uri getFullImageUri() {
        return fullImageUri;
    }

    public void setFullImageUri(Uri fullImageUri) {
        this.fullImageUri = fullImageUri;
    }
}
