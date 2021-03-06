package com.bookaroom.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.util.Consumer;

import com.bookaroom.R;
import com.bookaroom.activities.HostActivity;
import com.bookaroom.models.ActionResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import retrofit2.Response;

public class Utils {

    public static String getEditTextString(EditText editText) {
        return editText.getText().toString();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static void makeInternalErrorToast(Activity activity) {
        Toast.makeText(activity, R.string.internal_error, Toast.LENGTH_SHORT).show();
    }

    public static void makeLoadErrorToast(Activity activity) {
        Toast.makeText(activity, R.string.loading_error, Toast.LENGTH_SHORT).show();
    }

    public static String padStringLeft(String str, char padCharacter, int desiredLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = str.length(); i < desiredLength; i++) {
            sb.append(padCharacter);
        }
        sb.append(str);

        return sb.toString();
    }

    public static Integer parseInteger(EditText editText) {
        return parseInteger(editText.getText().toString());
    }

    public static Integer parseInteger(String str) {
        Integer value;
        try {
            value = Integer.parseInt(str);
        }
        catch (Exception e) {
            value = null;
        }

        return value;
    }

    public static Double parseDouble(EditText editText) {
        return parseDouble(editText.getText().toString());
    }

    public static Double parseDouble(String str) {
        Double value;
        try {
            value = Double.parseDouble(str);
        }
        catch (Exception e) {
            value = null;
        }

        return value;
    }

    public static void displayInvalidInputMessage(Activity currentActivity, int strResource) {
        Toast.makeText(currentActivity, strResource, Toast.LENGTH_SHORT).show();
    }

    public static String getIntegerStringOrDefault(
            Integer value,
            String defaultString) {
        if (value == null) {
            return defaultString;
        }

        try {
            return String.valueOf(value);
        }
        catch (Exception e) {
            return defaultString;
        }
    }

    public static String getDoubleStringOrDefault(
            Double value,
            String defaultString) {
        if (value == null) {
            return defaultString;
        }

        try {
            return String.valueOf(value);
        }
        catch (Exception e) {
            return defaultString;
        }
    }
}
