package com.protovate.verity.data;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Yan on 7/12/15.
 */
@Parcel
public class PhotosSelected {
    public List<PhotoItem> photos;

    public List<PhotoItem> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoItem> photos) {
        this.photos = photos;
    }
}
