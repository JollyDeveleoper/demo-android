package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.R;
import com.demo.android.adapters.UserAdapter;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.interfaces.OnItemClickListener;
import com.demo.android.models.Role;
import com.demo.android.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class UsersActivity extends Activity implements OnItemClickListener {
    private RecyclerView recyclerView;
    private ProgressBar loader;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_recycler);
        ((TextView) findViewById(R.id.title)).setText("Пользователи");
        this.recyclerView = findViewById(R.id.recycler);
        this.loader = findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        findViewById(R.id.primary_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, CreateOrEditUserActivity.class));
        });
        fetchData();
    }

    private void fetchData() {
        FetchHelper.fetchUsers(new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    try {
                        String data = response.body().string();
                        JSONArray arr = new JSONArray(data);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            setData(obj);
                        }
                        runOnUiThread(() -> setAdapter());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void OnError(Call response) { }
        });
    }

    private void setAdapter() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(new UserAdapter(this.userList, this));
        loader.setVisibility(View.GONE);
    }

    private void setData(JSONObject object) throws JSONException {
        User user = new User();
        user.setId(object.getInt("id"));
        user.setLogin(object.getString("login"));
        JSONObject roleObj = object.getJSONObject("role");
        Role role = new Role();
        role.setId(roleObj.getInt("id"));
        role.setName(roleObj.getString("name"));
        role.setDescription(roleObj.getString("description"));
        user.setRole(role);
        this.userList.add(user);
    }

    @Override
    public void onClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        Intent intent = new Intent(this, CreateOrEditUserActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("login", userList.get(position).getLogin());
        intent.putExtra("role", userList.get(position).getRole().getName());
        startActivity(intent);
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage("Удалить пользователя?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    FetchHelper.fetchDeleteUser(id, new OnCallback() {
                        @Override
                        public void OnSuccess(Response response) {
                            if (response.isSuccessful()) {
                                runOnUiThread(() -> {
                                    recyclerView.getAdapter().notifyItemRemoved(position);
                                    userList.remove(position);
                                });
                            }
                        }

                        @Override
                        public void OnError(Call response) { }
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
