package com.bookaroom.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.bookaroom.R;
import com.bookaroom.enums.UserRole;
import com.bookaroom.models.ActionResponse;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.UserService;
import com.bookaroom.utils.Constants;
import com.bookaroom.utils.RequestUtils;
import com.bookaroom.utils.ResponseUtils;
import com.bookaroom.utils.Utils;
import com.bookaroom.utils.navigation.NavigationUtils;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends Activity {
    private UserService userService;

    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private EditText edtName;
    private EditText edtSurname;
    private EditText edtEmail;
    private EditText edtPhone;
    private Spinner roleSpinner;
    private ImageView userSelectedImage;
    private Button registerButton;

    private boolean selectedImage = false;
    private Uri selectedImageUri = null;
    private String selectedImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userService = ApiUtils.getUserService(this);

        initializeFormData();
        setOnClickListeners();
    }

    private void initializeFormData() {
        userSelectedImage = (ImageView) findViewById(R.id.registerSelectImage);
        userSelectedImage.setImageResource(R.drawable.register_image);

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtPasswordConfirm);
        edtName = (EditText) findViewById(R.id.edtName);
        edtSurname = (EditText) findViewById(R.id.edtSurname);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);

        roleSpinner = (Spinner) findViewById(R.id.spinnerRole);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                                                android.R.layout.simple_spinner_item,
                                                                UserRole.names());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        registerButton = (Button) findViewById(R.id.btnRegister);
    }

    private void setOnClickListeners() {
        userSelectedImage.setOnClickListener(view -> onSelectImageClick());
        registerButton.setOnClickListener(view -> onRegisterButtonClick());
    }

    private void onSelectImageClick() {
        if (ActivityCompat.checkSelfPermission(this,
                                               Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    Constants.READ_STORAGE_PERMISSIONS_ARRAY,
                    Constants.READ_EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }

        selectUserImage();
    }

    private void onRegisterButtonClick() {
        String username = Utils.getEditTextString(edtUsername);
        String password1 = Utils.getEditTextString(edtPassword);
        String password2 = Utils.getEditTextString(edtConfirmPassword);
        String name = Utils.getEditTextString(edtName);
        String surname = Utils.getEditTextString(edtSurname);
        String email = Utils.getEditTextString(edtEmail);
        String phone = Utils.getEditTextString(edtPhone);
        UserRole userRole = UserRole.valueOf(roleSpinner.getSelectedItem().toString());

        if (!validateInput(username,
                           password1,
                           password2,
                           name,
                           surname,
                           email,
                           phone,
                           userRole)) {
            return;
        }

        File userPicture = new File(selectedImagePath);

        register(username,
                 password1,
                 name,
                 surname,
                 email,
                 phone,
                 userRole,
                 userPicture);
    }

    private boolean validateInput(
            String username,
            String password1,
            String password2,
            String name,
            String surname,
            String email,
            String phone,
            UserRole userRole) {
        if (Utils.isNullOrEmpty(username)) {
            Toast.makeText(RegisterActivity.this,
                           R.string.register_empty_username,
                           Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(password1) && Utils.isNullOrEmpty(password2)) {
            Toast.makeText(RegisterActivity.this,
                           R.string.register_empty_passwords,
                           Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password1.equals(password2)) {
            Toast.makeText(RegisterActivity.this,
                           R.string.register_passwords_dont_match,
                           Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(name)) {
            Toast.makeText(RegisterActivity.this,
                           R.string.register_empty_name,
                           Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(surname)) {
            Toast.makeText(RegisterActivity.this,
                           R.string.register_empty_surname,
                           Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(email)) {
            Toast.makeText(RegisterActivity.this,
                           R.string.register_empty_email,
                           Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(phone)) {
            Toast.makeText(RegisterActivity.this,
                           R.string.register_empty_phone,
                           Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!selectedImage) {
            Toast.makeText(RegisterActivity.this,
                           R.string.register_empty_image,
                           Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void register(
            String username,
            String password,
            String name,
            String surname,
            String email,
            String phone,
            UserRole userRole,
            File userPicture) {

        Call call = userService.register(
                RequestUtils.getRequestBodyForString(username),
                RequestUtils.getRequestBodyForString(password),
                RequestUtils.getRequestBodyForString(name),
                RequestUtils.getRequestBodyForString(surname),
                RequestUtils.getRequestBodyForString(email),
                RequestUtils.getRequestBodyForString(phone),
                RequestUtils.getRequestBodyForString(userRole.name()),
                RequestUtils.getMultipartBodyPartForFile(userPicture,
                                                         UserService.REGISTER_PART_USER_IMAGE)
        );
        call.enqueue(new Callback<ActionResponse>() {
            @Override
            public void onResponse(
                    Call<ActionResponse> call,
                    Response<ActionResponse> response) {
                ResponseUtils.handleActionResponse(RegisterActivity.this,
                                                   response,
                                                   (actionResponse) -> NavigationUtils.startLoginActivity(RegisterActivity.this),
                                                   (actionResponse -> {
                                                   }));
            }

            @Override
            public void onFailure(
                    Call call,
                    Throwable t) {
                Toast.makeText(RegisterActivity.this,
                               t.getMessage(),
                               Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage(String selectImageMessage) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        setPickImageIntentParameters(getIntent);

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                                       MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        setPickImageIntentParameters(pickIntent);

        Intent chooserIntent = Intent.createChooser(getIntent,
                                                    selectImageMessage);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                               new Intent[]{pickIntent});

        startActivityForResult(chooserIntent,
                               Constants.PICK_IMAGE_REQUEST_CODE);
    }

    private void setPickImageIntentParameters(Intent intent) {
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

    @Override
    public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case Constants.PICK_IMAGE_REQUEST_CODE:
                setSelectedImage(data);
                break;
            case Constants.READ_EXTERNAL_STORAGE_REQUEST_CODE:
                selectUserImage();
                break;
        }
    }

    private void selectUserImage() {
        selectImage(getResources().getString(R.string.register_select_picture));
    }

    private void setSelectedImage(Intent data) {
        selectedImageUri = data.getData();

        Bitmap selectedImageBitmap = null;
        try {
            selectedImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                                                                    data.getData());
        } catch (IOException e) {
            e.printStackTrace();
            Utils.makeInternalErrorToast(this);
            return;
        }

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImageUri,
                                                   filePathColumn,
                                                   null,
                                                   null,
                                                   null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        selectedImagePath = cursor.getString(columnIndex);
        cursor.close();

        userSelectedImage.setImageBitmap(selectedImageBitmap);
        selectedImage = true;
    }
}
