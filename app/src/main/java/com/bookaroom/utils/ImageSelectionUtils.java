package com.bookaroom.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.bookaroom.utils.Constants;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.dto.SelectedImageInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

public class ImageSelectionUtils {

    public static void requestSelectionOrPermissions(
            Activity currentActivity,
            String selectImageMessage,
            int permissionsRequestCode,
            int pickImageRequestCode) {
        if (ActivityCompat.checkSelfPermission(currentActivity,
                                               Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            requestSelection(currentActivity,
                             selectImageMessage,
                             pickImageRequestCode);
        } else {
            requestPermissions(currentActivity,
                               permissionsRequestCode);
        }
    }

    private static void requestPermissions(
            Activity currentActivity,
            int permissionsRequestCode) {
        ActivityCompat.requestPermissions(
                currentActivity,
                Constants.READ_STORAGE_PERMISSIONS_ARRAY,
                permissionsRequestCode);
    }

    public static void requestSelection(
            Activity currentActivity,
            String selectImageMessage,
            int pickImageRequestCode) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        setPickImageIntentParameters(getIntent);

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                                       MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        setPickImageIntentParameters(pickIntent);

        Intent chooserIntent = Intent.createChooser(getIntent,
                                                    selectImageMessage);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                               new Intent[]{pickIntent});

        currentActivity.startActivityForResult(chooserIntent,
                                               pickImageRequestCode);
    }

    private static void setPickImageIntentParameters(Intent intent) {
        intent.setType(Constants.INTENT_TYPE_IMAGE);
        intent.putExtra("crop",
                        true);
        intent.putExtra("scale",
                        true);
        intent.putExtra("aspectX",
                        16);
        intent.putExtra("aspectY",
                        9);
    }

    public static Uri getSelectedImageUri(Intent pickIntent) {
        return pickIntent.getData();
    }

    public static Bitmap getSelectedImageBitmap(
            Activity currentActivity,
            Intent pickIntent) {
        Uri selectedImageUri = getSelectedImageUri(pickIntent);

        Bitmap selectedImageBitmap = null;
        try {
            selectedImageBitmap = MediaStore.Images.Media.getBitmap(currentActivity.getApplicationContext().getContentResolver(),
                                                                    selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.makeInternalErrorToast(currentActivity);
            return null;
        }

        return selectedImageBitmap;
    }

    public static SelectedImageInfo getSelectedImageInfo(
            Activity currentActivity,
            Intent pickIntent) {
        Uri selectedImageUri = getSelectedImageUri(pickIntent);

        if (selectedImageUri == null) {
            return new SelectedImageInfo();
        }

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = currentActivity.getContentResolver().query(selectedImageUri,
                                                                   filePathColumn,
                                                                   null,
                                                                   null,
                                                                   null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String selectedImagePath = cursor.getString(columnIndex);
        cursor.close();

        Bitmap selectedImageBitmap = getSelectedImageBitmap(currentActivity,
                                                            pickIntent);

        return new SelectedImageInfo(selectedImageUri,
                                     selectedImageBitmap,
                                     selectedImagePath);
    }
}
