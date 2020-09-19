package com.bookaroom.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {
    public static File createTemporaryFile(
            Context context,
            String prefix,
            String suffix) throws IOException {
        File outputDir = context.getCacheDir();
        return File.createTempFile(prefix,
                                   suffix,
                                   outputDir);
    }

    public static void writeBitmapToFile(
            Bitmap bitmap,
            Bitmap.CompressFormat compressFormat,
            int quality,
            File file) throws IOException {
        OutputStream outStream;
        outStream = new FileOutputStream(file);
        bitmap.compress(compressFormat,
                        quality,
                        outStream);
        outStream.close();
    }

    public static File createTempFileFromBitmap(
            Context context,
            String prefix,
            String suffix,
            Bitmap bitmap,
            Bitmap.CompressFormat compressFormat,
            int quality) throws IOException {
        File tempFile = createTemporaryFile(context,
                                            prefix,
                                            suffix);
        writeBitmapToFile(bitmap,
                          compressFormat,
                          quality,
                          tempFile);

        return tempFile;
    }
}
