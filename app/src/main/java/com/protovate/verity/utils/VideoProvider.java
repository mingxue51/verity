package com.protovate.verity.utils;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.protovate.verity.data.VideoItem;

import java.util.ArrayList;

/**
 * Created by Yan on 7/8/15.
 */
public class VideoProvider {
    public static ArrayList<VideoItem> getVideos(Context context) {
        ArrayList<VideoItem> items = new ArrayList<>();

        String projection[] = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                context, queryUri, projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " ASC"
        );

        Cursor cursor = cursorLoader.loadInBackground();

        int pathIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
        if (cursor.moveToNext()) {
            do {
                String path = cursor.getString(pathIndex);
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);
                VideoItem item = new VideoItem(curThumb, path);
                if (curThumb != null)
                    items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();


        return items;
    }
}
