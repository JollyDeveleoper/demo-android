package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.models.Role;
import com.demo.android.utils.Validator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class CreateOrEditUserActivity extends Activity {
    private int id = 0;

    private TextInputEditText login, password;
    private AutoCompleteTextView role;
    private TextInputLayout loginBox, passwordBox;
    private List<String> rolesSpinner = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        role = findViewById(R.id.filled_exposed_dropdown);

        loginBox = findViewById(R.id.loginBox);
        passwordBox = findViewById(R.id.passwordBox);

        login.addTextChangedListener(new Validator(loginBox, false));
        password.addTextChangedListener(new Validator(passwordBox, false));

        getRoles();

        findViewById(R.id.primary_btn).setOnClickListener(v -> {
            Role role1 = findRoleByName(role.getText().toString());
            if (role1 == null) {
                Toast.makeText(Application.getInstance(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchUpdateOrCreate(role1.getId());
        });
        ((TextView) findViewById(R.id.primary_btn)).setText("Сохранить");
    }

    private Role findRoleByName(String name) {
        Role role = null;
        for (Role role1 : this.roles) {
            if (role1.getName().equals(name)) role = role1;
        }
        return role;
    }

    private void fetchUpdateOrCreate(int role) {
        FetchHelper.fetchCreateOrEditUser(this.id,
                login.getText().toString(),
                password.getText().toString(), role, new OnCallback() {
                    @Override
                    public void OnSuccess(Response response) {
                        if (response.isSuccessful()) {
                            finishAffinity();
                            startActivity(new Intent(CreateOrEditUserActivity.this, MainActivity.class));
                        }
                    }

                    @Override
                    public void OnError(Call response) {
                    }
                });
    }

    private void fillData() {
        Bundle bundle = getIntent().getExtras();
        this.id = bundle.getInt("id");

        login.setText(bundle.getString("login"));
        role.setText(bundle.getString("role"), false);
        isEdit = true;
    }


    private void getRoles() {
        FetchHelper.fetchRoles(new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    try {
                        fillRoles(new JSONArray(response.body().string()));
                        runOnUiThread(() -> {
                            setRolesAdapter();
                            if (getIntent().getExtras() != null) {
                                fillData();
                            }
                            ((TextView) findViewById(R.id.title)).setText(isEdit ? "Редактирование" : "Создание");
                        });
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void OnError(Call response) {
            }
        });
    }

    private void fillRoles(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String name = object.getString("name");
            Role role = new Role();
            role.setName(name);
            role.setId(object.getInt("id"));
            role.setDescription(object.getString("description"));
            roles.add(role);
            rolesSpinner.add(name);
        }
    }

    private void setRolesAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, rolesSpinner);
        role.setAdapter(adapter);
        findViewById(R.id.loader).setVisibility(View.GONE);
        role.setVisibility(View.VISIBLE);
    }
}
