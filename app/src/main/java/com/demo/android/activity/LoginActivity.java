package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.helpers.OkHttpHelper;
import com.demo.android.helpers.PreferencesKeys;
import com.demo.android.helpers.PrefsHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.demo.android.helpers.OkHttpHelper.JSON;

public class LoginActivity extends Activity {
    private static final String API = "https://demo.maffinca.design/api/login";
    private Button loginBtn;
    private EditText login;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = this.findViewById(R.id.loginBtn);
        login = this.findViewById(R.id.login);
        password = this.findViewById(R.id.password);
        loginBtn.setOnClickListener(v -> {
            loginBtn.setText("Загрузка...");
            fetchLogin(login.getText().toString(), password.getText().toString());
        });
    }

    private void fetchLogin(String login, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("login", login);
            jsonObject.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = OkHttpHelper.getPostRequest("login", body);
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    callbackLogin(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void callbackLogin(JSONObject response) throws JSONException {
        if (response.has("error")) {
            String error = response.getString("error");
            this.runOnUiThread(() -> {
                loginBtn.setText("ВОЙТИ");
                Toast.makeText(Application.getInstance(), error, Toast.LENGTH_SHORT).show();
            });
            return;
        }
        int role = response.getInt("role");
        String token = response.getString("token");
        PrefsHelper.setValue().putString(PreferencesKeys.ACCESS_TOKEN, token).apply();
        PrefsHelper.setValue().putInt(PreferencesKeys.ROLE, role).apply();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
