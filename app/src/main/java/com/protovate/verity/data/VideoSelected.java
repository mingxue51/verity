package com.protovate.verity.data;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Yan on 7/12/15.
 */
@Parcel
public class VideoSelected {
    public List<VideoItem> videos;

    public List<VideoItem> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoItem> videos) {
        this.videos = videos;
    }
}
