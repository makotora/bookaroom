package com.bookaroom.utils;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Toast;

import com.bookaroom.R;

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

    public static String padStringLeft(String str, char padCharacter, int desiredLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = str.length(); i < desiredLength; i++) {
            sb.append(padCharacter);
        }
        sb.append(str);

        return sb.toString();
    }
}
