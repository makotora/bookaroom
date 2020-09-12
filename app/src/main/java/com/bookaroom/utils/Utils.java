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
        Toast.makeText(activity, R.string.INTERNAL_ERROR, Toast.LENGTH_SHORT).show();
    }
}
