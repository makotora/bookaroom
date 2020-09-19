package com.bookaroom.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bookaroom.R;
import com.bookaroom.models.LoginRequest;
import com.bookaroom.remote.ApiUtils;
import com.bookaroom.remote.services.UserService;
import com.bookaroom.utils.Constants;
import com.bookaroom.utils.NavigationUtils;
import com.bookaroom.utils.SessionManager;
import com.bookaroom.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        userService = ApiUtils.getUserService(this);

        findViewById(R.id.btnLogin).setOnClickListener((view) -> {
            login();
        });

        findViewById(R.id.textRegister).setOnClickListener((view) -> {
            NavigationUtils.startRegisterActivity(LoginActivity.this);
        });
    }

    private void login() {
        String username = Utils.getEditTextString(edtUsername);
        String password = Utils.getEditTextString(edtPassword);

        if (!validInput(username, password)) {
            Toast.makeText(this, R.string.login_invalid_input, Toast.LENGTH_SHORT).show();
            return;
        }

        loginInternal(username, password);
    }

    private boolean validInput(String username, String password) {
        return !Utils.isNullOrEmpty(username) && !Utils.isNullOrEmpty(password);
    }

    private void loginInternal(String username, String password) {
        Call call = userService.login(new LoginRequest(username, password));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                    String authenticationToken =
                            response.headers().get(Constants.AUTHORIZATION_HEADER);
                    SessionManager.setAuthenticationToken(LoginActivity.this, authenticationToken);

                    NavigationUtils.startHomeActivity(LoginActivity.this);
                }
                else {
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, R.string.login_failed_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

}