package com.bookaroom.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bookaroom.R;
import com.bookaroom.models.LoginRequest;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.UserService;
import com.bookaroom.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bookaroom.utils.Constants.AUTHORIZATION_HEADER;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername;
    EditText edtPassword;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        userService = ApiUtils.getUserService();

        findViewById(R.id.btnLogin).setOnClickListener((view) -> {
            login();
        });
    }

    private void login() {
        String username = Utils.getEditTextString(edtUsername);
        String password = Utils.getEditTextString(edtPassword);

        if (!validInput(username, password)) {
            Toast.makeText(this, R.string.LOGIN_INVALID_INPUT, Toast.LENGTH_SHORT).show();
            return;
        }

        loginInternal(username, password);
    }

    private boolean validInput(String username, String password) {
        return username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty();
    }

    private void loginInternal(String username, String password) {
        Call call = userService.login(new LoginRequest(username, password));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(LoginActivity.this, R.string.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, response.headers().get(AUTHORIZATION_HEADER), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, R.string.LOGIN_FAILED, Toast.LENGTH_SHORT).show();
            }
        });
    }

}