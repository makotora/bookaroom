package com.bookaroom.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class ImageUtils {
    public static Bitmap getBitmapFromImageView(ImageView imageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable)
                imageView.getDrawable();
        return bitmapDrawable.getBitmap();
    }
}
