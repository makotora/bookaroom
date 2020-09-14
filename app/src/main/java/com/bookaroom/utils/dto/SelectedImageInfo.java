package com.bookaroom.utils.dto;

import android.graphics.Bitmap;
import android.net.Uri;

public class SelectedImageInfo {
    private final Uri uri;
    private final Bitmap bitmap;
    private final String path;

    public SelectedImageInfo() {
        this.uri = null;
        this.bitmap = null;
        this.path = null;
    }

    public SelectedImageInfo(Uri uri, Bitmap bitmap, String path) {
        this.uri = uri;
        this.bitmap = bitmap;
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getPath() {
        return path;
    }
}
