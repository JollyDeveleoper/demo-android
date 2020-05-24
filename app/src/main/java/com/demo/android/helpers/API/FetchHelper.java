package com.demo.android.helpers.API;

import android.util.Log;

import com.demo.android.helpers.OkHttpHelper;
import com.demo.android.interfaces.OnCallback;

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

public class FetchHelper {
    public static final void fetchDeleteItem(int id, OnCallback callback) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, object.toString());
        Request request = OkHttpHelper.getPostRequest("items/delete", body);
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.OnError(call);

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.OnSuccess(response);
            }
        });
    }

    public static final void fetchUsers(OnCallback callback) {
        Request request = OkHttpHelper.getRequest("users");
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.OnError(call);

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.OnSuccess(response);
            }
        });
    }

    public static final void fetchDeleteRole(int id, OnCallback callback) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, object.toString());
        Request request = OkHttpHelper.getPostRequest("roles/delete", body);
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.OnError(call);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.OnSuccess(response);
            }
        });
    }

    public static final void fetchDeleteUser(int id, OnCallback callback) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, object.toString());
        Request request = OkHttpHelper.getPostRequest("users/delete", body);
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.OnError(call);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.OnSuccess(response);
            }
        });
    }

    public static final void fetchRoles(OnCallback callback) {
        Request request = OkHttpHelper.getRequest("roles");
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.OnError(call);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.OnSuccess(response);
            }
        });
    }

    public static final void fetchCreateOrEditUser(int id, String login, String password, int role, OnCallback callback) {
        JSONObject object = new JSONObject();
        try {
            if (id > 0) {
                object.put("id", id);
            }
            object.put("login", login);
            object.put("password", password);
            object.put("role_id", role);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, object.toString());
        Request request = OkHttpHelper.getPostRequest(id > 0 ? "users/update" : "users/create", body);
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.OnError(call);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.OnSuccess(response);
            }
        });
    }

}
